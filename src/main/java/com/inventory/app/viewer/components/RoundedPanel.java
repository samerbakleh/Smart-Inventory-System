package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;

public class RoundedPanel extends JPanel {

    protected int arcRadius;

    public RoundedPanel() {
        this.arcRadius = 5;
        setOpaque(false);
    }
    public RoundedPanel(int arcRadius) {
        this.arcRadius = arcRadius;
        setOpaque(false);
    }

    @Override
    protected void paintBorder(Graphics g) {
        super.paintBorder(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(getBackground());
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arcRadius, arcRadius);
    }

}