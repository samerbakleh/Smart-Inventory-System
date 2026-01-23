package com.inventory.app.viewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public final class UIEffects {

    public enum ToastType { INFO, SUCCESS, WARNING, ERROR }

    private UIEffects() {}

    public static void shake(Window w) {
        if (w == null) return;
        Point p = w.getLocationOnScreen();
        int[] dx = {0, -10, 10, -8, 8, -5, 5, 0};
        Timer t = new Timer(18, null);
        final int[] i = {0};
        t.addActionListener(e -> {
            w.setLocation(p.x + dx[i[0]], p.y);
            i[0]++;
            if (i[0] >= dx.length) {
                t.stop();
                w.setLocation(p);
            }
        });
        t.start();
    }

    public static void toast(Component anyChild, String message, ToastType type) {
        if (anyChild == null) return;

        Window w = SwingUtilities.getWindowAncestor(anyChild);
        if (!(w instanceof RootPaneContainer rpc)) {
            JOptionPane.showMessageDialog(anyChild, message);
            return;
        }

        JRootPane root = rpc.getRootPane();
        JLayeredPane layered = root.getLayeredPane();

        ToastPanel toast = new ToastPanel(message, type);
        Dimension pref = toast.getPreferredSize();

        int margin = 14;
        int x = w.getWidth() - pref.width - margin;
        int yStart = -pref.height - 10;
        int yEnd = margin;

        toast.setBounds(x, yStart, pref.width, pref.height);
        layered.add(toast, JLayeredPane.POPUP_LAYER);

        // Theme apply (so label colors match)
        ThemeManager.get().applyToContainer(toast);

        // slide in
        animateY(toast, yStart, yEnd, 220, () -> {
            // stay then slide out
            new Timer(1600, (ActionEvent e) -> {
                ((Timer) e.getSource()).stop();
                animateY(toast, yEnd, yStart, 220, () -> {
                    layered.remove(toast);
                    layered.repaint();
                });
            }).start();
        });
    }

    private static void animateY(JComponent c, int fromY, int toY, int durMs, Runnable done) {
        long t0 = System.currentTimeMillis();
        int x = c.getX();
        Timer t = new Timer(15, null);
        t.addActionListener(e -> {
            float tt = Math.min(1f, (System.currentTimeMillis() - t0) / (float) durMs);
            float k = smoothstep(tt);
            int y = (int) (fromY + (toY - fromY) * k);
            c.setLocation(x, y);
            c.repaint();
            if (tt >= 1f) {
                t.stop();
                if (done != null) done.run();
            }
        });
        t.start();
    }

    private static float smoothstep(float t) {
        return t * t * (3f - 2f * t);
    }

    public static Color blend(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (a.getRed() + (b.getRed() - a.getRed()) * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue() + (b.getBlue() - a.getBlue()) * t);
        int al = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(clamp(r), clamp(g), clamp(bl), clamp(al));
    }

    private static int clamp(int x) { return Math.max(0, Math.min(255, x)); }

    // --- Toast panel (rounded + accent bar) ---
    private static class ToastPanel extends JPanel {
        private final String msg;
        private final ToastType type;

        ToastPanel(String msg, ToastType type) {
            this.msg = msg == null ? "" : msg;
            this.type = type == null ? ToastType.INFO : type;

            setOpaque(false);
            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(10, 12, 10, 12));

            JLabel icon = new JLabel(iconFor(this.type));
            JLabel text = new JLabel(this.msg);

            icon.setFont(icon.getFont().deriveFont(Font.PLAIN, 16f));
            text.setFont(text.getFont().deriveFont(Font.PLAIN, 13f));

            add(icon, BorderLayout.WEST);
            add(text, BorderLayout.CENTER);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(360, 52);
        }

        @Override
        protected void paintComponent(Graphics g) {
            ThemeManager.Theme t = ThemeManager.get().theme();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 18;

            // shadow
            g2.setColor(new Color(0, 0, 0, t.mode == ThemeManager.Mode.DARK ? 55 : 35));
            g2.fillRoundRect(4, 4, getWidth() - 6, getHeight() - 6, arc, arc);

            // card
            g2.setColor(t.surface2);
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, arc, arc);

            // border
            g2.setColor(t.border);
            g2.drawRoundRect(0, 0, getWidth() - 6, getHeight() - 6, arc, arc);

            // accent bar
            g2.setColor(colorFor(type, t));
            g2.fillRoundRect(0, 0, 8, getHeight() - 6, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }

        private String iconFor(ToastType type) {
            return switch (type) {
                case SUCCESS -> "✅";
                case WARNING -> "⚠️";
                case ERROR -> "⛔";
                default -> "ℹ️";
            };
        }

        private Color colorFor(ToastType type, ThemeManager.Theme t) {
            return switch (type) {
                case SUCCESS -> t.success;
                case WARNING -> t.warning;
                case ERROR -> t.danger;
                default -> t.accent;
            };
        }
    }
}
