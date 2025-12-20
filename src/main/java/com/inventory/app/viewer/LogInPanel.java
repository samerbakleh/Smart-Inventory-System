package com.inventory.app.viewer;

import com.inventory.app.controller.AuthenticationController;
import com.inventory.app.service.LoginResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

                // Draw gradient background
                g2.setPaint(new GradientPaint(0, 0, GRADIENT_START, getWidth(), getHeight(), GRADIENT_END));
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Draw animated circles with reduced opacity and darker color for better contrast
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.08f));
                g2.setColor(new Color(255, 255, 255, 200)); // Slightly darker white
                int o1 = (int)(Math.sin(animProgress * 2) * 30);
                int o2 = (int)(Math.cos(animProgress * 3) * 20);
                g2.fillOval(-150 + o1, -150 + o1, 500, 500);
                g2.fillOval(getWidth() - 400 + o2, getHeight() - 400 - o2, 500, 500);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(null);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 40, 10, 40);

        // Icon with improved visibility
        JLabel icon = new JLabel("🏭") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw a subtle glow/shadow behind the icon
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                g2.setColor(Color.BLACK);
                for (int i = 0; i < 3; i++) {
                    g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80 + i * 2));
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth("🏭")) / 2 + i;
                    int y = (getHeight() + fm.getAscent()) / 2 + i;
                    g2.drawString("🏭", x, y);
                }

                g2.dispose();
                super.paintComponent(g);
            }
        };
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        icon.setForeground(Color.WHITE);
        icon.setOpaque(false);
        panel.add(icon, gbc);

        // Title with text shadow for better visibility
        JLabel title = new JLabel("Production System") {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Draw text shadow for depth and contrast
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String text = getText();
                int x = (getWidth() - fm.stringWidth(text)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;

                // Draw shadow (multiple layers for better visibility)
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                g2.setColor(Color.BLACK);
                for (int i = 1; i <= 3; i++) {
                    g2.drawString(text, x + i, y + i);
                }

                // Draw main text
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2.setColor(Color.WHITE);
                g2.drawString(text, x, y);

                g2.dispose();
            }
        };
        Font titleFont = new Font("Segoe UI", Font.BOLD, 48);
        title.setFont(titleFont.deriveFont(Font.BOLD)); // Ensure no underline decoration
        title.setForeground(Color.WHITE);
        title.setOpaque(false);
        title.setBorder(null);
        title.setFocusable(false);
        title.setDisplayedMnemonic(0); // Disable mnemonic to prevent underline
        title.setDisplayedMnemonicIndex(-1); // Ensure no mnemonic index
        panel.add(title, gbc);

        JPanel divider = new JPanel();
        divider.setPreferredSize(new Dimension(100, 4));
        divider.setBackground(ACCENT);
        divider.setOpaque(true);
        divider.setBorder(null);
        panel.add(divider, gbc);

        addLabel(panel, "Manage Production Lines", 22, gbc);
        addLabel(panel, "And Inventory Control", 22, gbc);

        return panel;
    }

    private void addLabel(JPanel panel, String text, int size, GridBagConstraints gbc) {
        JLabel label = new JLabel(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Draw text shadow for better contrast
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                String labelText = getText();
                int x = (getWidth() - fm.stringWidth(labelText)) / 2;
                int y = (getHeight() + fm.getAscent()) / 2;

                // Draw shadow
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
                g2.setColor(Color.BLACK);
                for (int i = 1; i <= 2; i++) {
                    g2.drawString(labelText, x + i, y + i);
                }

                // Draw main text with brighter color
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2.setColor(new Color(255, 255, 255)); // Pure white for better contrast
                g2.drawString(labelText, x, y);

                g2.dispose();
            }
        };
        label.setFont(new Font("Segoe UI", Font.PLAIN, size));
        label.setForeground(new Color(255, 255, 255)); // Changed to pure white
        label.setOpaque(false);
        label.setBorder(null);
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

        errorLabel = new JLabel("✖ Invalid username or password or Role");
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

        if (field instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) field;
            combo.setBackground(Color.WHITE);
            combo.setOpaque(true);
            // Apply only the outer rounded border, no inner borders
            combo.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(BORDER, 10),
                    new EmptyBorder(12, 16, 12, 16)
            ));
            // Remove all borders from combo box components
            combo.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (c instanceof JComponent) {
                        JComponent jc = (JComponent) c;
                        jc.setBorder(null);
                        // Normalize all properties to ensure consistent behavior for both items
                        // This ensures Manager and Supervisor have identical hover/selection behavior
                        jc.setOpaque(false);
                        // Set consistent background (transparent/white) regardless of selection state
                        jc.setBackground(Color.WHITE);
                        // Use consistent foreground color for all items
                        jc.setForeground(new Color(31, 41, 55));
                        // Ensure consistent alignment for both items
                        if (c instanceof JLabel) {
                            JLabel label = (JLabel) c;
                            label.setHorizontalAlignment(SwingConstants.LEFT);
                            // Ensure consistent insets/padding
                            label.setIconTextGap(0);
                        }
                    }
                    return c;
                }
            });
            // Remove border from editor component
            if (combo.getEditor() != null && combo.getEditor().getEditorComponent() != null) {
                Component editorComp = combo.getEditor().getEditorComponent();
                if (editorComp instanceof JComponent) {
                    ((JComponent) editorComp).setBorder(null);
                    ((JComponent) editorComp).setOpaque(false);
                }
            }
            // Customize arrow button to remove border - need to do this after combo is added
            SwingUtilities.invokeLater(() -> {
                for (Component comp : combo.getComponents()) {
                    if (comp instanceof JButton) {
                        JButton arrowButton = (JButton) comp;
                        arrowButton.setBorder(null);
                        arrowButton.setContentAreaFilled(false);
                        arrowButton.setFocusPainted(false);
                        arrowButton.setOpaque(false);
                    }
                }
            });
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(BORDER, 10),
                    new EmptyBorder(12, 16, 12, 16)
            ));
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

        LoginResult result= AuthenticationController.loginController(username,password,role);

        errorLabel.setVisible(!result.isSuccess());
        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, "Welcome " + role + "! Next Window Will Be here...");
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

