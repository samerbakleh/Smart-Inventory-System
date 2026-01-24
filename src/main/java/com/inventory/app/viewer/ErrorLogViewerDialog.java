package com.inventory.app.viewer;

import com.inventory.app.util.AppConstants;
import com.inventory.app.util.LogFileUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ErrorLogViewerDialog extends JDialog {

    private static final int TAIL_CHARS = 80_000;
    private static final int REFRESH_MS = 2000;

    private final JTextArea textArea = new JTextArea();
    private final JTextField searchField = new JTextField();
    private final JLabel statusLabel = new JLabel(" ");
    private final JCheckBox autoRefresh = new JCheckBox("Auto Refresh", true);

    private final JButton refreshBtn = new JButton("Refresh");
    private final JButton clearBtn = new JButton("Clear");
    private final JButton openBtn  = new JButton("Open File");

    private  Timer timer;
    private String lastLoadedText = "";

    public ErrorLogViewerDialog(Window owner) {
        super(owner, "Error Log Viewer", ModalityType.MODELESS);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE   );
        setSize(900, 600);
        setLocationRelativeTo(owner);

        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        textArea.setLineWrap(false);

        setLayout(new BorderLayout(10, 10));
        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(buildBottomBar(), BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> refreshAsync());
        clearBtn.addActionListener(e -> clearLog());
        openBtn.addActionListener(e -> openFile());

        autoRefresh.addActionListener(e -> {
            if (autoRefresh.isSelected()) timer.start();
            else timer.stop();
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { applySearchFilter(); }
            @Override public void removeUpdate(DocumentEvent e) { applySearchFilter(); }
            @Override public void changedUpdate(DocumentEvent e) { applySearchFilter(); }
        });

        timer = new Timer(REFRESH_MS, e -> {
            if (autoRefresh.isSelected()) refreshAsync();
        });
        timer.start();

        refreshAsync();
    }

    private JComponent buildTopBar() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JLabel title = new JLabel("Error Log");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        p.add(title, BorderLayout.WEST);

        JPanel center = new JPanel(new BorderLayout(6, 6));
        center.add(new JLabel("Search:"), BorderLayout.WEST);
        center.add(searchField, BorderLayout.CENTER);
        p.add(center, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.add(autoRefresh);
        actions.add(refreshBtn);
        actions.add(openBtn);
        actions.add(clearBtn);
        p.add(actions, BorderLayout.EAST);

        return p;
    }

    private JComponent buildBottomBar() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(statusLabel, BorderLayout.CENTER);
        return p;
    }

    private void refreshAsync() {
        refreshBtn.setEnabled(false);

        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return LogFileUtil.readTail(AppConstants.LOG_FILE, TAIL_CHARS);
            }

            @Override
            protected void done() {
                refreshBtn.setEnabled(true);
                try {
                    lastLoadedText = get();
                    applySearchFilter();
                    updateStatus("Loaded", null);
                } catch (Exception ex) {
                    updateStatus("Read failed", ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void applySearchFilter() {
        String q = searchField.getText();
        if (q == null || q.trim().isEmpty()) {
            textArea.setText(lastLoadedText);
            textArea.setCaretPosition(textArea.getDocument().getLength());
            return;
        }

        String needle = q.trim().toLowerCase();
        StringBuilder sb = new StringBuilder();

        for (String line : lastLoadedText.split("\\R")) {
            if (line.toLowerCase().contains(needle)) {
                sb.append(line).append(System.lineSeparator());
            }
        }

        textArea.setText(sb.toString());
        textArea.setCaretPosition(textArea.getDocument().getLength());
    }

    private void clearLog() {
        int ok = JOptionPane.showConfirmDialog(
                this,
                "Clear log file?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );
        if (ok != JOptionPane.YES_OPTION) return;

        try {
            LogFileUtil.clear(AppConstants.LOG_FILE);
            lastLoadedText = "";
            textArea.setText("");
            updateStatus("Cleared", null);
        } catch (Exception ex) {
            updateStatus("Clear failed", ex.getMessage());
        }
    }

    private void openFile() {
        try {
            File f = new File(AppConstants.LOG_FILE);
            if (!f.exists()) {
                JOptionPane.showMessageDialog(this, "Log file does not exist yet.");
                return;
            }
            if (!Desktop.isDesktopSupported()) {
                JOptionPane.showMessageDialog(this, "Desktop open is not supported.");
                return;
            }
            Desktop.getDesktop().open(f);
        } catch (Exception ex) {
            updateStatus("Open failed", ex.getMessage());
        }
    }

    private void updateStatus(String action, String details) {
        long bytes = LogFileUtil.size(AppConstants.LOG_FILE);
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String msg = action + " | " + time + " | " + bytes + " bytes";
        if (details != null && !details.trim().isEmpty()) msg += " | " + details;
        statusLabel.setText(msg);
    }

    @Override
    public void dispose() {
        timer.stop();
        super.dispose();
    }
}
