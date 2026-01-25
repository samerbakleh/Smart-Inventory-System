package com.inventory.app.viewer;
import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {

    private JButton openLogBtn;
    private JButton makeTestErrorBtn;

    private JLabel statusLabel;

    public MainFrame() {
        super("Smart Inventory System");

        applyLookAndFeel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 720);
        setLocationRelativeTo(null);

        setJMenuBar(buildMenuBar());

        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(14, 14, 14, 14));
        setContentPane(root);

        root.add(buildHeader(), BorderLayout.NORTH);
        root.add(buildCenter(), BorderLayout.CENTER);
        root.add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ---------------- UI SECTIONS ----------------

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setBorder(new EmptyBorder(6, 6, 12, 6));

        JLabel title = new JLabel("Smart Inventory System");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));

        JLabel sub = new JLabel("Dashboard");
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 13f));
        sub.setForeground(new Color(90, 90, 90));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(title);
        left.add(Box.createVerticalStrut(3));
        left.add(sub);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);

        openLogBtn = new JButton("Open Error Log");
        openLogBtn.setFocusPainted(false);

        makeTestErrorBtn = new JButton("Test Error");
        makeTestErrorBtn.setFocusPainted(false);

        right.add(openLogBtn);
        right.add(makeTestErrorBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        openLogBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ErrorLogViewerDialog(MainFrame.this).setVisible(true);
                setStatus("Opened Error Log Viewer");
            }
        });

        makeTestErrorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new RuntimeException("Test error from MainFrame");
            }
        });

        return header;
    }

    private JComponent buildCenter() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);

        JPanel card = createCard();
        card.setLayout(new BorderLayout(12, 12));

        JLabel head = new JLabel("Quick Actions");
        head.setFont(head.getFont().deriveFont(Font.BOLD, 16f));

        JLabel hint = new JLabel("<html>Use the buttons above to open the Error Log viewer.<br>" +
                "Later, you'll add Items/Categories screens here.</html>");
        hint.setForeground(new Color(85, 85, 85));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(head);
        body.add(Box.createVerticalStrut(8));
        body.add(hint);
        body.add(Box.createVerticalStrut(14));

        body.add(createInfoRow("• Inventory", "Not connected yet"));
        body.add(Box.createVerticalStrut(6));
        body.add(createInfoRow("• Categories", "Not connected yet"));
        body.add(Box.createVerticalStrut(6));
        body.add(createInfoRow("• Reports", "Not connected yet"));

        card.add(body, BorderLayout.CENTER);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;

        center.add(card, gc);
        return center;
    }

    private JComponent buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(new EmptyBorder(10, 6, 0, 6));
        bar.setOpaque(false);

        statusLabel = new JLabel("Ready");
        statusLabel.setForeground(new Color(90, 90, 90));
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));

        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu tools = new JMenu("Tools");
        JMenuItem errorLog = new JMenuItem("Error Log");

        errorLog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ErrorLogViewerDialog(MainFrame.this).setVisible(true);
                setStatus("Opened Error Log Viewer");
            }
        });

        tools.add(errorLog);
        bar.add(tools);

        return bar;
    }


    private JPanel createCard() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(16, 16, 16, 16)
        ));
        p.setBackground(Color.WHITE);
        return p;
    }

    private JComponent createInfoRow(String left, String right) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel l = new JLabel(left);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 12.5f));

        JLabel r = new JLabel(right);
        r.setForeground(new Color(110, 110, 110));

        row.add(l, BorderLayout.WEST);
        row.add(r, BorderLayout.EAST);
        return row;
    }

    private void setStatus(String msg) {
        if (statusLabel != null) statusLabel.setText(msg);
    }

    private void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
            }
        } catch (Exception ignored) {
        }
    }
    public static void startMainFrame()
    {
        SwingUtilities.invokeLater(() -> {
            try {
                // محاولة تفعيل الستايل الخاص بالنظام
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}