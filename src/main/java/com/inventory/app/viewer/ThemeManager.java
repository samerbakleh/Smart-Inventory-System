package com.inventory.app.viewer;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Objects;
import java.util.prefs.Preferences;
import java.awt.Window;
import javax.swing.RootPaneContainer;

public final class ThemeManager {

    public enum Mode { LIGHT, DARK }

    public static final class Theme {
        public final Mode mode;

        public final Color bg;
        public final Color surface;
        public final Color surface2;
        public final Color border;

        public final Color text;
        public final Color textDim;

        public final Color accent;
        public final Color danger;
        public final Color success;
        public final Color warning;

        public final Color tableRowAlt;
        public final Color tableSelection;

        private Theme(Mode mode,
                      Color bg, Color surface, Color surface2, Color border,
                      Color text, Color textDim,
                      Color accent, Color danger, Color success, Color warning,
                      Color tableRowAlt, Color tableSelection) {
            this.mode = mode;
            this.bg = bg;
            this.surface = surface;
            this.surface2 = surface2;
            this.border = border;
            this.text = text;
            this.textDim = textDim;
            this.accent = accent;
            this.danger = danger;
            this.success = success;
            this.warning = warning;
            this.tableRowAlt = tableRowAlt;
            this.tableSelection = tableSelection;
        }

        public static Theme light() {
            return new Theme(
                    Mode.LIGHT,
                    new Color(246, 248, 251),
                    Color.WHITE,
                    new Color(250, 251, 252),
                    new Color(229, 231, 235),
                    new Color(17, 24, 39),
                    new Color(107, 114, 128),
                    new Color(37, 99, 235),
                    new Color(220, 38, 38),
                    new Color(22, 163, 74),
                    new Color(245, 158, 11),
                    new Color(245, 247, 250),
                    new Color(37, 99, 235, 45)
            );
        }

        public static Theme dark() {
            return new Theme(
                    Mode.DARK,
                    new Color(15, 23, 42),      // slate-900
                    new Color(17, 24, 39),      // gray-900
                    new Color(30, 41, 59),      // slate-800
                    new Color(51, 65, 85),      // slate-700
                    new Color(241, 245, 249),   // slate-100
                    new Color(148, 163, 184),   // slate-400
                    new Color(96, 165, 250),    // blue-400
                    new Color(248, 113, 113),   // red-400
                    new Color(74, 222, 128),    // green-400
                    new Color(251, 191, 36),    // amber-400
                    new Color(22, 33, 55),
                    new Color(96, 165, 250, 55)
            );
        }
    }

    private static final ThemeManager INSTANCE = new ThemeManager();
    public static ThemeManager get() { return INSTANCE; }

    private static final String PREF_KEY = "theme_mode";
    private final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);

    private Theme theme;

    private ThemeManager() {
        Mode saved = Mode.valueOf(prefs.get(PREF_KEY, Mode.LIGHT.name()));
        theme = (saved == Mode.DARK) ? Theme.dark() : Theme.light();
    }

    public Theme theme() { return theme; }

    public void setMode(Mode mode) {
        Objects.requireNonNull(mode);
        theme = (mode == Mode.DARK) ? Theme.dark() : Theme.light();
        prefs.put(PREF_KEY, mode.name());
    }

    public void toggle() {
        setMode(theme.mode == Mode.DARK ? Mode.LIGHT : Mode.DARK);
    }

    /** Apply theme to entire frame */
    public void applyToFrame(JFrame frame) {
        applyRecursive(frame.getContentPane());
        SwingUtilities.updateComponentTreeUI(frame);
        frame.repaint();
    }

    /** Apply theme to a container (panel) */
    public void applyToContainer(Container root) {
        applyRecursive(root);
        SwingUtilities.updateComponentTreeUI(root);
        root.repaint();
    }

    // القديم صار Wrapper
    private void applyRecursive(Component c) {
        applyRecursive(c, theme);
    }

    // ✅ النسخة الجديدة اللي بتقبل Theme (مستخدمة بالأنيميشن)
    private void applyRecursive(Component c, Theme t) {

        if (c instanceof JPanel p) {
            p.setBackground(t.bg);
            p.setForeground(t.text);
        } else if (c instanceof JLabel l) {
            l.setForeground(t.text);
        } else if (c instanceof JButton b) {
            if (!(b instanceof AnimatedColorButton)) {
                styleButton(b, t);
            } else {
                // لا تلمسه — هو بيرسم حاله
                b.setForeground(t.text);
            }
        }

     else if (c instanceof JToggleButton tb) {
            styleToggle(tb, t);
        } else if (c instanceof JTextField tf) {
            tf.setBackground(t.surface);
            tf.setForeground(t.text);
            tf.setCaretColor(t.text);
            tf.setBorder(new javax.swing.border.LineBorder(t.border, 1, true));
        } else if (c instanceof JTextArea ta) {
            ta.setBackground(t.surface);
            ta.setForeground(t.text);
            ta.setCaretColor(t.text);
            ta.setBorder(new javax.swing.border.LineBorder(t.border, 1, true));
        } else if (c instanceof JComboBox<?> cb) {
            cb.setBackground(t.surface);
            cb.setForeground(t.text);
            cb.setBorder(new javax.swing.border.LineBorder(t.border, 1, true));
            cb.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                              boolean isSelected, boolean cellHasFocus) {
                    JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    lbl.setOpaque(true);
                    lbl.setForeground(t.text);
                    lbl.setBackground(isSelected ? t.tableSelection : t.surface);
                    return lbl;
                }
            });
        } else if (c instanceof JTable table) {
            styleTable(table, t);
        } else if (c instanceof JTableHeader header) {
            header.setBackground(t.surface2);
            header.setForeground(t.textDim);
        } else if (c instanceof JScrollPane sp) {
            sp.setBorder(new javax.swing.border.LineBorder(t.border, 1, true));
            if (sp.getViewport() != null) sp.getViewport().setBackground(t.bg);
        }

        // ✅ recursion بنفس الـ Theme
        if (c instanceof Container container) {
            for (Component child : container.getComponents()) {
                applyRecursive(child, t);
            }
        }
    }


    private void styleButton(JButton b, Theme t) {
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setForeground(t.text);
        b.setBackground(t.surface2);
        b.setBorder(new LineBorder(t.border, 1, true));
    }

    private void styleToggle(JToggleButton b, Theme t) {
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setForeground(t.text);
        b.setBackground(b.isSelected() ? t.accent : t.surface2);
        b.setBorder(new LineBorder(t.border, 1, true));
    }

    private void styleTable(JTable table, Theme t) {
        table.setBackground(t.surface);
        table.setForeground(t.text);
        table.setGridColor(t.border);
        table.setSelectionBackground(t.tableSelection);
        table.setSelectionForeground(t.text);

        JTableHeader header = table.getTableHeader();
        if (header != null) {
            header.setOpaque(true);
            header.setBackground(t.surface2);
            header.setForeground(t.textDim);
            header.setBorder(new LineBorder(t.border, 1, true));
        }

        // nice renderer for rows + alternating row colors
        DefaultTableCellRenderer r = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                lbl.setOpaque(true);
                lbl.setForeground(t.text);

                if (isSelected) {
                    lbl.setBackground(t.tableSelection);
                } else {
                    lbl.setBackground((row % 2 == 0) ? t.surface : t.tableRowAlt);
                }
                return lbl;
            }
        };
        table.setDefaultRenderer(Object.class, r);
        table.setDefaultRenderer(Number.class, r);
    }
    // ✅ Apply a provided theme (useful for animation frames)
    public void applyToFrame(JFrame frame, Theme customTheme, boolean updateUI) {
        applyRecursive(frame.getContentPane(), customTheme);
        if (updateUI) SwingUtilities.updateComponentTreeUI(frame);
        frame.repaint();
    }

    public void applyToContainer(Container root, Theme customTheme, boolean updateUI) {
        applyRecursive(root, customTheme);
        if (updateUI) SwingUtilities.updateComponentTreeUI(root);
        root.repaint();
    }

    // ✅ Build theme by mode without changing saved mode
    public Theme themeOf(Mode mode) {
        return (mode == Mode.DARK) ? Theme.dark() : Theme.light();
    }

    // ✅ Lerp two themes (for smooth animation)
    public Theme lerp(Theme a, Theme b, float t) {
        t = clamp01(t);
        return new Theme(
                b.mode, // mode doesn't matter during interpolation
                lerp(a.bg, b.bg, t),
                lerp(a.surface, b.surface, t),
                lerp(a.surface2, b.surface2, t),
                lerp(a.border, b.border, t),
                lerp(a.text, b.text, t),
                lerp(a.textDim, b.textDim, t),
                lerp(a.accent, b.accent, t),
                lerp(a.danger, b.danger, t),
                lerp(a.success, b.success, t),
                lerp(a.warning, b.warning, t),
                lerp(a.tableRowAlt, b.tableRowAlt, t),
                lerp(a.tableSelection, b.tableSelection, t)
        );
    }

    private static float clamp01(float x) { return Math.max(0f, Math.min(1f, x)); }

    private static Color lerp(Color a, Color b, float t) {
        int r = (int) (a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int) (a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        int al = (int) (a.getAlpha() + (b.getAlpha() - a.getAlpha()) * t);
        return new Color(clamp255(r), clamp255(g), clamp255(bl), clamp255(al));
    }
    private static int clamp255(int x) { return Math.max(0, Math.min(255, x)); }
    public void applyToAllWindows(Theme customTheme, boolean updateUI) {
        for (Window w : Window.getWindows()) {
            if (w == null || !w.isDisplayable()) continue;

            if (w instanceof RootPaneContainer rpc) {
                applyRecursive(rpc.getContentPane(), customTheme);
                if (updateUI) SwingUtilities.updateComponentTreeUI(rpc.getContentPane());
            }

            if (updateUI) SwingUtilities.updateComponentTreeUI(w);
            w.repaint();
        }
    }

    public void applyToAllWindows() {
        applyToAllWindows(theme, true);
    }
}
