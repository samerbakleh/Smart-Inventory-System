package com.inventory.app.viewer.components;

import com.inventory.app.util.AppConstants;

import javax.swing.*;
import java.awt.*;

public class StyledLabel extends JComponent {

    protected String text;
    protected Font font = AppConstants.DEFAULT_FONT;
    protected int offset = 2;

    public StyledLabel(String text) {
        this.text = text;
    }
    public StyledLabel(String text, Color color) {
        this.text = text;
        setForeground(color);
    }
    public StyledLabel(String text, Color color, float fontSize) {
        this.text = text;
        setForeground(color);
        this.font = this.font.deriveFont(fontSize);
    }

    public void setText(String text) {
        this.text = text;
        revalidate();
        repaint();
    }
    public void setFontSize(float fontSize) {
        this.font = this.font.deriveFont(fontSize);
        revalidate();
        repaint();
    }
    public void setFontStyle(int style) {
        this.font = this.font.deriveFont(style);
        repaint();
    }
    public void setOffset(int offset) {
        this.offset = offset;
        revalidate();
        repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(font);
        return new Dimension(fm.stringWidth(text) + offset, fm.getHeight() + offset);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setFont(font);

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        float x = (getWidth() - textWidth)/2f;
        float y = (getHeight() - textHeight)/2f + fm.getAscent();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        g2d.drawString(text, x + offset, y + offset);
        g2d.setColor(getForeground());
        g2d.drawString(text, x, y);
    }

}
