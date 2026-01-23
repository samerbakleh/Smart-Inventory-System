package com.inventory.app.viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

public class ThemeToggleButton extends JComponent {

    private float progress = 0f; // 0 = light (sun), 1 = dark (moon)
    private boolean selected = false;
    private Timer anim;

    public ThemeToggleButton() {
        setPreferredSize(new Dimension(64, 34));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                firePropertyChange("toggle", false, true);
            }
        });
    }

    public boolean isSelected() { return selected; }

    public void setSelectedAnimated(boolean selected) {
        boolean old = this.selected;
        this.selected = selected;
        float target = selected ? 1f : 0f;
        animateTo(target, 260);
        firePropertyChange("selected", old, selected);
    }

    public void setProgress(float p) {
        progress = Math.max(0f, Math.min(1f, p));
        repaint();
    }

    private void animateTo(float target, int durMs) {
        if (anim != null && anim.isRunning()) anim.stop();
        final float start = progress;

        anim = new Timer(15, null);
        final long t0 = System.currentTimeMillis();

        anim.addActionListener(e -> {
            float t = Math.min(1f, (System.currentTimeMillis() - t0) / (float) durMs);
            float k = easeInOut(t);
            progress = start + (target - start) * k;
            repaint();
            if (t >= 1f) anim.stop();
        });
        anim.start();
    }

    private float easeInOut(float t) {
        // smoothstep
        return t * t * (3f - 2f * t);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Track colors (lerp)
        Color trackLight = new Color(36, 54, 82);
        Color trackDark  = new Color(35, 45, 70);

        Color track = lerp(trackLight, trackDark, progress);
        Color outline = lerp(new Color(210, 216, 226), new Color(70, 85, 120), progress);

        // track
        g2.setColor(track);
        g2.fillRoundRect(0, 0, w, h, h, h);

        // outline
        g2.setColor(outline);
        g2.drawRoundRect(0, 0, w - 1, h - 1, h, h);

        // Thumb position
        int pad = 4;
        int d = h - pad * 2;
        int x = (int) (pad + (w - d - pad * 2) * progress);
        int y = pad;

        // thumb shadow
        g2.setColor(new Color(0, 0, 0, (int) (30 + 40 * progress)));
        g2.fillOval(x + 1, y + 2, d, d);

        // thumb
        Color thumb = lerp(Color.WHITE, new Color(225, 231, 244), progress);
        g2.setColor(thumb);
        g2.fillOval(x, y, d, d);

        // icon inside thumb (sun->moon morph by crossfade)
        int cx = x + d / 2;
        int cy = y + d / 2;

        paintSun(g2, cx, cy, d, 1f - progress);
        paintMoon(g2, cx, cy, d, progress);

        g2.dispose();
    }

    private void paintSun(Graphics2D g2, int cx, int cy, int d, float a) {
        if (a <= 0f) return;
        g2.setComposite(AlphaComposite.SrcOver.derive(0.95f * a));
        g2.setColor(new Color(245, 176, 65)); // sun
        int r = (int) (d * 0.22);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);

        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int ray = (int) (d * 0.38);
        for (int i = 0; i < 8; i++) {
            double ang = i * Math.PI / 4;
            int x1 = cx + (int) (Math.cos(ang) * (r + 2));
            int y1 = cy + (int) (Math.sin(ang) * (r + 2));
            int x2 = cx + (int) (Math.cos(ang) * ray);
            int y2 = cy + (int) (Math.sin(ang) * ray);
            g2.drawLine(x1, y1, x2, y2);
        }
        g2.setComposite(AlphaComposite.SrcOver);
    }

    private void paintMoon(Graphics2D g2, int cx, int cy, int d, float a) {
        if (a <= 0f) return;
        g2.setComposite(AlphaComposite.SrcOver.derive(0.95f * a));
        g2.setColor(new Color(120, 175, 255));

        int r = (int) (d * 0.28);
        Area moon = new Area(new Ellipse2D.Float(cx - r, cy - r, r * 2, r * 2));
        // cut out circle to make crescent
        int cut = (int) (r * 1.2);
        moon.subtract(new Area(new Ellipse2D.Float(cx - r + cut / 2f, cy - r - 2, r * 2, r * 2)));

        g2.fill(moon);
        g2.setComposite(AlphaComposite.SrcOver);
    }

    private Color lerp(Color a, Color b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int g = (int) (a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl= (int) (a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(r, g, bl);
    }
}
