package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;

public class ShadowedLabel extends JComponent {

    protected String text;
    protected int offset;
    protected int verticalAlignment = JLabel.CENTER;
    protected int horizontalAlignment = JLabel.CENTER;

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
    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
        repaint();
    }
    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
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
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = getWidth();
        int labelHeight = getHeight();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        float x = switch (horizontalAlignment) {
            case JLabel.CENTER -> (labelWidth - textWidth) / 2f;
            case JLabel.RIGHT -> labelWidth - textWidth;
            default -> 0;
        };
        float y = switch (verticalAlignment) {
            case JLabel.CENTER -> (labelHeight - textHeight) / 2f + fm.getAscent();
            case JLabel.TOP -> fm.getAscent();
            default -> labelHeight - fm.getDescent();
        };
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if(isOpaque()) {
            g2d.setColor(getBackground());
            g2d.fillRect(0, 0, labelWidth - 1, labelHeight - 1);
        }
        g2d.setColor(Color.black);
        g2d.drawString(text, x + offset, y + offset);
        g2d.setColor(getForeground());
        g2d.drawString(text, x, y);
    }

}