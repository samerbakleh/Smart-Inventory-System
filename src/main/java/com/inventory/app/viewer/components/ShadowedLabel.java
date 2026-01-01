package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;

public class ShadowedLabel extends JComponent {

    protected String text;
    protected int offset;

    public ShadowedLabel(String text) {
        this.text = text;
        this.offset = 2;
    }
    public ShadowedLabel(String text, int offset) {
        this.text = text;
        this.offset = offset;
    }

    public void setText(String text) {
        this.text = text;
        revalidate();
        repaint();
    }
    public void setOffset(int offset) {
        this.offset = offset;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        return new Dimension(fm.stringWidth(text) + offset, fm.getHeight() + offset);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setFont(getFont());

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        float x = (getWidth() - textWidth)/2f;
        float y = (getHeight() - textHeight)/2f + fm.getAscent();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if(isOpaque()) {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        g2d.setColor(Color.black);
        g2d.drawString(text, x + offset, y + offset);
        g2d.setColor(getForeground());
        g2d.drawString(text, x, y);
    }

}