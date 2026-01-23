package com.inventory.app.viewer;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame implements AppNavigator {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cards = new JPanel(cardLayout);
    private final Map<String, JPanel> registered = new HashMap<>();

    // Top bar components
    private final JPanel topBar = new JPanel(new BorderLayout(12, 0));
    private final JLabel titleLabel = new JLabel("Smart Inventory System");
    private final JLabel subTitleLabel = new JLabel("");

    private final ThemeToggleButton themeToggle = new ThemeToggleButton();
    private Timer themeAnim;

    public MainFrame() {
        super("Smart Inventory System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(1200, 720);
        setLocationRelativeTo(null);

        buildTopBar();

        add(topBar, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);

        // apply theme initially
        applyThemeToFrame();
    }

    public static void startMainFrame() {
    }

    private void buildTopBar() {
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        topBar.setOpaque(true);

        // Left (title/subtitle)
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        subTitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        left.add(titleLabel);
        left.add(Box.createVerticalStrut(2));
        left.add(subTitleLabel);

        // Right (theme toggle)
        themeToggle.setPreferredSize(new Dimension(64, 34));
        themeToggle.addPropertyChangeListener("toggle", evt -> animateThemeFlip());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        right.add(themeToggle);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);
    }

    /** Register panels (cards) */
    public void registerView(String key, JPanel panel) {
        if (key == null || key.isBlank()) throw new IllegalArgumentException("key is empty");
        if (panel == null) throw new IllegalArgumentException("panel is null");

        registered.put(key, panel);
        cards.add(panel, key);

        // Apply theme to the view
        ThemeManager.get().applyToContainer(panel);
    }

    /** Switch visible panel */
    @Override
    public void show(String key) {
        if (!registered.containsKey(key)) {
            JOptionPane.showMessageDialog(this, "View not registered: " + key);
            return;
        }
        cardLayout.show(cards, key);
    }

    /** Optional: update subtitle per screen */
    public void setSubTitle(String text) {
        subTitleLabel.setText(text == null ? "" : text);
    }

    private void applyThemeToFrame() {
        // Apply global theme to frame
        ThemeManager.get().applyToFrame(this);

        // Keep top bar distinct
        ThemeManager.Theme t = ThemeManager.get().theme();

        topBar.setBackground(t.surface2);
        titleLabel.setForeground(t.text);
        subTitleLabel.setForeground(t.textDim);

        // sync toggle position
        themeToggle.setSelectedAnimated(t.mode == ThemeManager.Mode.DARK);

        repaint();
    }

    private void animateThemeFlip() {
        ThemeManager tm = ThemeManager.get();

        ThemeManager.Theme start = tm.theme();
        ThemeManager.Mode targetMode = (start.mode == ThemeManager.Mode.DARK)
                ? ThemeManager.Mode.LIGHT
                : ThemeManager.Mode.DARK;

        ThemeManager.Theme end = tm.themeOf(targetMode);

        // animate toggle immediately
        themeToggle.setSelectedAnimated(targetMode == ThemeManager.Mode.DARK);

        if (themeAnim != null && themeAnim.isRunning()) themeAnim.stop();

        final long t0 = System.currentTimeMillis();
        final int dur = 320;

        themeAnim = new Timer(15, null);
        themeAnim.addActionListener(e -> {
            float t = Math.min(1f, (System.currentTimeMillis() - t0) / (float) dur);
            float k = t * t * (3f - 2f * t); // smoothstep

            ThemeManager.Theme mid = tm.lerp(start, end, k);

            // apply mid theme WITHOUT heavy UI update (avoid flicker)
            tm.applyToAllWindows(mid, false);

            topBar.setBackground(mid.surface2);
            titleLabel.setForeground(mid.text);
            subTitleLabel.setForeground(mid.textDim);

            if (t >= 1f) {
                themeAnim.stop();
                tm.setMode(targetMode);          // commit
                tm.applyToAllWindows(tm.theme(), true); // final refresh
            }
        });
        themeAnim.start();
    }
}
