package com.inventory.app.viewer;

import com.inventory.app.model.enums.LogLevel;
import com.inventory.app.util.AppConstants;
import com.inventory.app.util.ErrorLog;
import com.inventory.app.util.LogFileUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ErrorLogViewerPanel extends JPanel {

    private static final int DEFAULT_MAX_CHARS = 200_000;

    private final JTextArea area = new JTextArea();
    private final JLabel status = new JLabel(" ");
    private final JTextField searchField = new JTextField(18);
    private final JComboBox<Object> levelBox = new JComboBox<>();
    private final JCheckBox autoRefresh = new JCheckBox("Auto Refresh");
    private final JCheckBox follow = new JCheckBox("Follow");

    private final Timer timer;

    private int maxChars = DEFAULT_MAX_CHARS;

    public ErrorLogViewerPanel() {
        super(new BorderLayout(10, 10));
        setName("Error Log Viewer Panel");
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(14, 14, 14, 14));

        buildUI();
        hookActions();

        timer = new Timer(2000, e -> refresh()); // كل 2 ثانية
        timer.setRepeats(true);

        // defaults
        autoRefresh.setSelected(true);
        follow.setSelected(true);

        if (autoRefresh.isSelected()) timer.start();

        refresh();
    }

    private void buildUI() {
        add(buildTop(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        // style
        area.setEditable(false);
        area.setLineWrap(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setBackground(Color.WHITE);

        status.setForeground(new Color(80, 80, 80));
        status.setFont(AppConstants.DEFAULT_FONT.deriveFont(13f));

        styleField(searchField);
        styleCombo(levelBox);
        styleCheck(autoRefresh);
        styleCheck(follow);
    }

    private JComponent buildTop() {
        JPanel top = new JPanel(new BorderLayout(10, 10));
        top.setOpaque(false);

        JLabel title = new JLabel("Error Logs");
        title.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 22f));
        title.setForeground(new Color(20, 35, 70));

        top.add(title, BorderLayout.NORTH);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        row.setOpaque(false);

        // level combo
        DefaultComboBoxModel<Object> m = new DefaultComboBoxModel<>();
        m.addElement("All Levels");
        for (LogLevel lv : LogLevel.values()) m.addElement(lv);
        levelBox.setModel(m);
        levelBox.setSelectedIndex(0);

        JButton refreshBtn = btn("Refresh");
        JButton clearBtn = btnDanger("Clear");
        JButton copyBtn = btn("Copy");
        JButton openBtn = btn("Open File");

        refreshBtn.addActionListener(e -> refresh());
        clearBtn.addActionListener(e -> onClear());
        copyBtn.addActionListener(e -> onCopy());
        openBtn.addActionListener(e -> onOpenFile());

        row.add(lbl("Level:"));
        row.add(levelBox);

        row.add(lbl("Search:"));
        row.add(searchField);

        row.add(autoRefresh);
        row.add(follow);

        row.add(refreshBtn);
        row.add(copyBtn);
        row.add(openBtn);
        row.add(clearBtn);

        top.add(row, BorderLayout.SOUTH);
        return top;
    }

    private JComponent buildCenter() {
        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createLineBorder(new Color(225, 232, 245)));
        sp.getViewport().setBackground(Color.WHITE);
        sp.setBackground(Color.WHITE);
        return sp;
    }

    private JComponent buildBottom() {
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(status, BorderLayout.WEST);
        return bottom;
    }

    private void hookActions() {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
        });

        levelBox.addActionListener(e -> refresh());

        autoRefresh.addActionListener(e -> {
            if (autoRefresh.isSelected()) timer.start();
            else timer.stop();
        });
    }

    public void refresh() {
        String raw;
        try {
            raw = LogFileUtil.readTail(AppConstants.LOG_FILE, maxChars);
        } catch (Exception ex) {
            ErrorLog.error("ErrorLogViewerPanel.refresh.readTail", ex);
            raw = "Failed to read log file:\n" + ex.getMessage();
        }

        LogLevel selectedLevel = null;
        Object sel = levelBox.getSelectedItem();
        if (sel instanceof LogLevel lv) selectedLevel = lv;

        String q = (searchField.getText() == null) ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);

        String shown = applyFilters(raw, selectedLevel, q);

        area.setText(shown);

        if (follow.isSelected()) {
            area.setCaretPosition(area.getDocument().getLength());
        }

        long size = LogFileUtil.size(AppConstants.LOG_FILE);
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        status.setText("File: " + new File(AppConstants.LOG_FILE).getName()
                + " | Size: " + formatBytes(size)
                + " | Showing last: " + formatBytes(maxChars)
                + " | Refreshed: " + ts);
    }

    private String applyFilters(String raw, LogLevel selectedLevel, String q) {
        if (raw == null || raw.isEmpty()) return "";

        List<LogBlock> blocks = parseBlocks(raw);

        StringBuilder out = new StringBuilder(raw.length());

        for (LogBlock b : blocks) {
            if (selectedLevel != null && b.level != null && b.level != selectedLevel) continue;

            if (q != null && !q.isEmpty()) {
                if (!b.text.toLowerCase(Locale.ROOT).contains(q)) continue;
            }

            out.append(b.text);
            if (!b.text.endsWith("\n")) out.append("\n");
        }

        return out.toString();
    }

    // كل entry (السطر الأساسي + الستاك ترايس يلي تحته) نعتبره block واحد
    private List<LogBlock> parseBlocks(String raw) {
        String[] lines = raw.split("\\R", -1);

        ArrayList<LogBlock> blocks = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        LogLevel curLevel = null;
        boolean haveAny = false;

        for (String line : lines) {
            HeaderParse hp = tryParseHeader(line);

            if (hp != null) {
                // new header => flush previous
                if (haveAny) {
                    blocks.add(new LogBlock(curLevel, cur.toString()));
                    cur.setLength(0);
                }
                curLevel = hp.level;
                cur.append(line).append("\n");
                haveAny = true;
            } else {
                // continuation (stack trace or tail chunk)
                if (!haveAny) {
                    // إذا tail قطع النص من نص entry، نتجاهل الأسطر قبل أول header
                    continue;
                }
                cur.append(line).append("\n");
            }
        }

        if (haveAny) {
            blocks.add(new LogBlock(curLevel, cur.toString()));
        }

        return blocks;
    }

    // header format:
    // yyyy-mm-dd ... | LEVEL | thread | context | message ...
    private HeaderParse tryParseHeader(String line) {
        if (line == null) return null;

        // split by " | "
        String[] parts = line.split("\\s\\|\\s", -1);
        if (parts.length < 3) return null;

        String lvl = parts[1].trim();
        try {
            LogLevel level = LogLevel.valueOf(lvl);
            return new HeaderParse(level);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void onCopy() {
        try {
            String text = area.getText();
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
        } catch (Exception ex) {
            ErrorLog.error("ErrorLogViewerPanel.copy", ex);
            JOptionPane.showMessageDialog(this, "Copy failed: " + ex.getMessage());
        }
    }

    private void onClear() {
        int ok = JOptionPane.showConfirmDialog(this,
                "Clear log file? This will delete current content.",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (ok != JOptionPane.YES_OPTION) return;

        try {
            LogFileUtil.clear(AppConstants.LOG_FILE);
            refresh();
        } catch (Exception ex) {
            ErrorLog.error("ErrorLogViewerPanel.clear", ex);
            JOptionPane.showMessageDialog(this, "Clear failed: " + ex.getMessage());
        }
    }

    private void onOpenFile() {
        try {
            File f = new File(AppConstants.LOG_FILE);
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this, "Log file does not exist yet.");
                return;
            }
            Desktop.getDesktop().open(f);
        } catch (Exception ex) {
            ErrorLog.error("ErrorLogViewerPanel.openFile", ex);
            JOptionPane.showMessageDialog(this, "Open failed: " + ex.getMessage());
        }
    }

    // -------- helpers / styling --------

    private JLabel lbl(String t) {
        JLabel l = new JLabel(t);
        l.setForeground(new Color(90, 90, 90));
        l.setFont(AppConstants.DEFAULT_FONT.deriveFont(13f));
        return l;
    }

    private JButton btn(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setForeground(new Color(30, 30, 30));
        b.setBackground(new Color(245, 248, 255));
        b.setFont(AppConstants.DEFAULT_FONT.deriveFont(13f));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 245)),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        return b;
    }

    private JButton btnDanger(String text) {
        JButton b = btn(text);
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(220, 60, 70));
        b.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        return b;
    }

    private void styleField(JTextField f) {
        f.setForeground(new Color(30, 30, 30));
        f.setCaretColor(new Color(30, 30, 30));
        f.setBackground(Color.WHITE);
        f.setFont(AppConstants.DEFAULT_FONT.deriveFont(13f));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 228, 245)),
                BorderFactory.createEmptyBorder(6, 9, 6, 9)
        ));
    }

    private void styleCombo(JComboBox<?> box) {
        box.setBackground(Color.WHITE);
        box.setForeground(new Color(30, 30, 30));
        box.setFont(AppConstants.DEFAULT_FONT.deriveFont(13f));
        box.setBorder(BorderFactory.createLineBorder(new Color(220, 228, 245)));
    }

    private void styleCheck(JCheckBox c) {
        c.setOpaque(false);
        c.setForeground(new Color(60, 60, 60));
        c.setFont(AppConstants.DEFAULT_FONT.deriveFont(13f));
    }

    private static String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        double kb = bytes / 1024.0;
        if (kb < 1024) return String.format(Locale.ROOT, "%.1f KB", kb);
        double mb = kb / 1024.0;
        if (mb < 1024) return String.format(Locale.ROOT, "%.1f MB", mb);
        double gb = mb / 1024.0;
        return String.format(Locale.ROOT, "%.2f GB", gb);
    }

    private static final class LogBlock {
        final LogLevel level;
        final String text;
        LogBlock(LogLevel level, String text) {
            this.level = level;
            this.text = text == null ? "" : text;
        }
    }

    private static final class HeaderParse {
        final LogLevel level;
        HeaderParse(LogLevel level) { this.level = level; }
    }
}
