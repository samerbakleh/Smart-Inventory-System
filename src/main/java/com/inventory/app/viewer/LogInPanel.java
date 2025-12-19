package com.inventory.app.viewer;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.*;
import java.awt.event.*;
import java.awt.geom.*;


public class LogInPanel extends JPanel {

    private final Color GRADIENT_START = new Color(99, 102, 241);
    private final Color GRADIENT_END = new Color(168, 85, 247);
    private final Color ACCENT = new Color(236, 72, 153);
    private final Color BORDER = new Color(229, 231, 235);

    private JTextField userField;
    private JPasswordField passField;
    private JComboBox<String> roleCombo;
    private JLabel errorLabel;
    private float animProgress = 0f;

    public LogInPanel() {
        setLayout(new GridLayout(1, 2));
        add(createCoverPanel());
        add(createFormPanel());
        new Timer(30, e -> { animProgress += 0.02f; repaint(); }).start();
    }

    private JPanel createCoverPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, GRADIENT_START, getWidth(), getHeight(), GRADIENT_END));
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2.setColor(Color.WHITE);
                int o1 = (int)(Math.sin(animProgress * 2) * 30);
                int o2 = (int)(Math.cos(animProgress * 3) * 20);
                g2.fillOval(-150 + o1, -150 + o1, 500, 500);
                g2.fillOval(getWidth() - 400 + o2, getHeight() - 400 - o2, 500, 500);
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 40, 10, 40);

        JLabel icon = new JLabel("🏭");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        panel.add(icon, gbc);

        JLabel title = new JLabel("Production System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 48));
        title.setForeground(Color.WHITE);
        panel.add(title, gbc);

        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(100, 4));
        divider.setBackground(ACCENT);
        panel.add(divider, gbc);

        addLabel(panel, "Manage Production Lines", 22, gbc);
        addLabel(panel, "And Inventory Control", 22, gbc);

        return panel;
    }

    private void addLabel(JPanel panel, String text, int size, GridBagConstraints gbc) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, size));
        label.setForeground(new Color(240, 240, 255));
        panel.add(label, gbc);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(249, 250, 251));

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 20),
                new EmptyBorder(55, 45, 55, 45)
        ));

        addFormTitle(form, "Welcome Back! ", 34);
        addFormTitle(form, "Sign in to continue to your account", 14);
        form.add(Box.createVerticalStrut(40));

        roleCombo = addField(form, "Select Role", new JComboBox<>(new String[]{"Manager", "Supervisor"}));
        userField = addField(form, "Username", new JTextField(20));
        passField = addField(form, "Password", new JPasswordField(20));

        errorLabel = new JLabel("✖ Invalid username or password");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        errorLabel.setForeground(new Color(239, 68, 68));
        errorLabel.setVisible(false);
        errorLabel.setAlignmentX(LEFT_ALIGNMENT);
        form.add(errorLabel);
        form.add(Box.createVerticalStrut(28));

        GradientButton btn = new GradientButton("LOGIN");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> performLogin());
        form.add(btn);

        form.setPreferredSize(new Dimension(450, 650));
        panel.add(form);
        return panel;
    }

    private void addFormTitle(JPanel form, String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", size > 20 ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(size > 20 ? new Color(31, 41, 55) : new Color(107, 114, 128));
        label.setAlignmentX(LEFT_ALIGNMENT);
        form.add(label);
        form.add(Box.createVerticalStrut(8));
    }

    private <T extends JComponent> T addField(JPanel form, String label, T field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lbl.setForeground(new Color(31, 41, 55));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        form.add(lbl);
        form.add(Box.createVerticalStrut(8));

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        field.setAlignmentX(LEFT_ALIGNMENT);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(BORDER, 10),
                new EmptyBorder(12, 16, 12, 16)
        ));

        if (field instanceof JComboBox) {
            ((JComboBox)field).setBackground(Color.WHITE);
        } else {
            field.addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(GRADIENT_START, 10), new EmptyBorder(12, 16, 12, 16)));
                }
                public void focusLost(FocusEvent e) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(BORDER, 10), new EmptyBorder(12, 16, 12, 16)));
                }
            });
        }

        form.add(field);
        form.add(Box.createVerticalStrut(24));
        return field;
    }

    private void performLogin() {
        String role = (String) roleCombo.getSelectedItem();
        String username = userField.getText();
        String password = new String(passField.getPassword());
        boolean success = ("Manager".equals(role) && "admin".equals(username) && "admin123".equals(password)) ||
                ("Supervisor".equals(role) && "sup".equals(username) && "sup123".equals(password));

        errorLabel.setVisible(!success);
        if (success) {
            JOptionPane.showMessageDialog(this, "Welcome " + role + "!");
        }
    }

    class GradientButton extends JButton {
        private boolean hover = false;

        public GradientButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(MouseEvent e) { hover = false; repaint(); }
            });
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color start = hover ? GRADIENT_START.darker() : GRADIENT_START;
            Color end = hover ? GRADIENT_END.darker() : GRADIENT_END;
            g2.setPaint(new GradientPaint(0, 0, start, getWidth(), getHeight(), end));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    class RoundedBorder extends AbstractBorder {
        private Color color;
        private int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2));
            g2.draw(new RoundRectangle2D.Float(x + 1, y + 1, width - 2, height - 2, radius, radius));
            g2.dispose();
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }
    }
}