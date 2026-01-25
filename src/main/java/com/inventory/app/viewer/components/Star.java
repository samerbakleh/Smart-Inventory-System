package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;

public class Star extends JButton {
    protected int radius;
    protected boolean active;
    protected Path2D shape;

    public Star(int radius) {
        this.radius = radius;
        this.active = true;
        initStarShape(radius, radius);
        setOpaque(false);
    }

    protected void initStarShape(double x, double y) {
        shape = new Path2D.Double();
        double r = Math.sqrt(Math.pow((1 + radius * Math.sin(Math.PI/10) / Math.tan(Math.PI*17/10)), 2)
                + Math.pow(radius * Math.sin(Math.PI/10), 2));
        double a = -Math.PI/2;
        shape.moveTo(x, y - radius);
        a += Math.PI/5;
        shape.lineTo(x + r * Math.cos(a), y + r * Math.sin(a));
        for(int i = 0; i < 4; i++) {
            a += Math.PI/5;
            shape.lineTo(x + radius * Math.cos(a), y + radius * Math.sin(a));
            a += Math.PI/5;
            shape.lineTo(x + r * Math.cos(a), y + r * Math.sin(a));
        }
        shape.closePath();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(2 * radius, 2 * radius);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(getBackground());
        int width = getWidth(), height = getHeight();
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(getText()), textHeight = fm.getHeight();
        g2d.fillRect(0, 0, width, height);
        initStarShape(width/2.0, height/2.0);
        if(active) {
            g2d.setColor(Color.yellow);
            g2d.fill(shape);
        }
        g2d.setColor(Color.black);
        g2d.draw(shape);
        g2d.setColor(getForeground());
        g2d.drawString(getText(), (width - textWidth) / 2, (height - textHeight) / 2 + fm.getAscent() - 1);
    }

    @Override
    public boolean contains(int x, int y) {
        return shape.contains(x, y);
    }
}
