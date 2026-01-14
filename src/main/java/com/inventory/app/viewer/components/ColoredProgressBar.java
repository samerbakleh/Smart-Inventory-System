package com.inventory.app.viewer.components;

import javax.swing.*;
import java.awt.*;

public class ColoredProgressBar extends JProgressBar {

    private Color color;

    public ColoredProgressBar(int min, int max, Color color) {
        super(min, max);
        this.color = color;
        Painter<JProgressBar> painter = new Painter<JProgressBar>() {
            @Override
            public void paint(Graphics2D g, JProgressBar object, int width, int height) {
                g.setColor(color);
                g.fillRect(0, 0, width, height);
            }
        };
        UIDefaults defaults = new UIDefaults();
        defaults.put("ProgressBar[Enabled].background", painter);
        putClientProperty("Nimbus.Overrides", defaults);
        putClientProperty("Nimbus.Overrides.InheritDefaults", true);
    }

}
