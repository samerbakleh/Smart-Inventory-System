package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GradientPanel extends JPanel {

    public static final int VERTICAL = 0;
    public static final int HORIZONTAL = 1;
    public static final int DIAGONAL1 = 2;
    public static final int DIAGONAL2 = 3;

    protected Color color1, color2;
    protected int axis;

    public GradientPanel(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
        this.axis = DIAGONAL2;

    }
    public GradientPanel(Color color1, Color color2, int axis) {
        this.color1 = color1;
        this.color2 = color2;
        this.axis = axis;
    }

    public void setColor1(Color color1) {
        this.color1 = color1;
    }
    public void setColor2(Color color2) {
        this.color2 = color2;
    }
    public void setAxis(int axis) {
        this.axis = axis;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        int width = getWidth();
        int height = getHeight();
        int x1 = 0, y1 = 0;
        int x2 = width - 1, y2 = height - 1;
        switch (axis) {
            case VERTICAL:
                x2 = 0;
                break;
            case HORIZONTAL:
                y2 = 0;
                break;
            case DIAGONAL1:
                x1 = width - 1;
                x2 = 0;
                break;
        }
        GradientPaint gp = new GradientPaint(x1, y1, color1, x2, y2, color2);
        g2d.setPaint(gp);
        Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, width, height);
        g2d.fill(rect);
    }

}
