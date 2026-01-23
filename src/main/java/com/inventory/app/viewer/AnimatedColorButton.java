package com.inventory.app.viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class AnimatedColorButton extends JButton {

    public enum Kind { PRIMARY, SUCCESS, WARNING, DANGER, NEUTRAL }

    private final Kind kind;

    private float hover = 0f;      // 0..1
    private float press = 0f;      // 0..1 (scale)
    private float ripple = 0f;     // 0..1
    private Point rippleAt = new Point(0, 0);

    private Timer hoverAnim;
    private Timer pressAnim;
    private Timer rippleAnim;

    public AnimatedColorButton(String text, Kind kind) {
        super(text);
        this.kind = (kind == null) ? Kind.NEUTRAL : kind;

        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setMargin(new Insets(10, 14, 10, 14));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { animateHoverTo(1f); }
            @Override public void mouseExited(MouseEvent e)  { animateHoverTo(0f); }

            @Override public void mousePressed(MouseEvent e) {
                rippleAt = e.getPoint();
                startRipple();
                animatePressTo(1f);
            }

            @Override public void mouseReleased(MouseEvent e) {
                animatePressTo(0f);
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        d.height = Math.max(d.height, 42);
        d.width = Math.max(d.width, 120);
        return d;
    }

    private void animateHoverTo(float target) {
        if (hoverAnim != null && hoverAnim.isRunning()) hoverAnim.stop();
        float start = hover;
        long t0 = System.currentTimeMillis();
        int dur = 140;

        hoverAnim = new Timer(15, null);
        hoverAnim.addActionListener(e -> {
            float t = Math.min(1f, (System.currentTimeMillis() - t0) / (float) dur);
            float k = smoothstep(t);
            hover = start + (target - start) * k;
            repaint();
            if (t >= 1f) hoverAnim.stop();
        });
        hoverAnim.start();
    }

    private void animatePressTo(float target) {
        if (pressAnim != null && pressAnim.isRunning()) pressAnim.stop();
        float start = press;
        long t0 = System.currentTimeMillis();
        int dur = 110;

        pressAnim = new Timer(15, null);
        pressAnim.addActionListener(e -> {
            float t = Math.min(1f, (System.currentTimeMillis() - t0) / (float) dur);
            float k = smoothstep(t);
            press = start + (target - start) * k;
            repaint();
            if (t >= 1f) pressAnim.stop();
        });
        pressAnim.start();
    }

    private void startRipple() {
        ripple = 0f;
        if (rippleAnim != null && rippleAnim.isRunning()) rippleAnim.stop();

        long t0 = System.currentTimeMillis();
        int dur = 420;

        rippleAnim = new Timer(15, null);
        rippleAnim.addActionListener(e -> {
            float t = Math.min(1f, (System.currentTimeMillis() - t0) / (float) dur);
            ripple = t;
            repaint();
            if (t >= 1f) rippleAnim.stop();
        });
        rippleAnim.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        ThemeManager.Theme t = ThemeManager.get().theme();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int arc = 14;

        // Press scale (micro interaction)
        float s = 1f - 0.04f * press;
        int sw = (int) (w * s);
        int sh = (int) (h * s);
        int sx = (w - sw) / 2;
        int sy = (h - sh) / 2;

        Shape rr = new RoundRectangle2D.Float(sx, sy, sw, sh, arc, arc);

        // base colors from theme + variant kind
        Color base = switch (kind) {
            case PRIMARY -> t.accent;
            case SUCCESS -> t.success;
            case WARNING -> t.warning;
            case DANGER  -> t.danger;
            default      -> t.surface2;
        };

        Color bg = (kind == Kind.NEUTRAL)
                ? UIEffects.blend(t.surface2, t.border, 0.10f + 0.12f * hover)
                : UIEffects.blend(base, Color.WHITE, (t.mode == ThemeManager.Mode.DARK ? 0.10f : 0.16f) * hover);

        Color bg2 = (kind == Kind.NEUTRAL)
                ? UIEffects.blend(bg, t.bg, 0.08f)
                : UIEffects.blend(bg, Color.BLACK, (t.mode == ThemeManager.Mode.DARK ? 0.10f : 0.06f));

        // shadow
        g2.setColor(new Color(0, 0, 0, t.mode == ThemeManager.Mode.DARK ? (int)(40 + 35*hover) : (int)(26 + 30*hover)));
        g2.fillRoundRect(sx + 2, sy + 3, sw, sh, arc, arc);

        // background gradient-ish
        g2.setPaint(new GradientPaint(0, sy, bg, 0, sy + sh, bg2));
        g2.fill(rr);

        // border
        g2.setColor(UIEffects.blend(t.border, base, kind == Kind.NEUTRAL ? 0.0f : 0.15f));
        g2.draw(rr);

        // ripple
        if (rippleAnim != null && rippleAnim.isRunning()) {
            float a = (1f - ripple) * 0.22f;
            g2.setClip(rr);
            g2.setComposite(AlphaComposite.SrcOver.derive(a));
            int max = (int) Math.hypot(w, h);
            int r = (int) (max * ripple);
            int cx = rippleAt.x;
            int cy = rippleAt.y;
            g2.setColor(Color.WHITE);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setComposite(AlphaComposite.SrcOver);
            g2.setClip(null);
        }

        // text
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();

        String text = getText();
        int tx = (w - fm.stringWidth(text)) / 2;
        int ty = (h + fm.getAscent() - fm.getDescent()) / 2;

        Color fg = (kind == Kind.NEUTRAL) ? t.text : new Color(255, 255, 255);
        // in light mode, warning button needs darker text sometimes
        if (kind == Kind.WARNING && t.mode == ThemeManager.Mode.LIGHT) fg = new Color(17, 24, 39);

        g2.setColor(new Color(0, 0, 0, (int) (55 * hover))); // subtle shadow
        g2.drawString(text, tx, ty + 1);

        g2.setColor(fg);
        g2.drawString(text, tx, ty);

        g2.dispose();
    }

    private static float smoothstep(float t) {
        return t * t * (3f - 2f * t);
    }
}
