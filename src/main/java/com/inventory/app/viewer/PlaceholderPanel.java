package com.inventory.app.viewer;

import javax.swing.*;
import java.awt.*;

public class PlaceholderPanel extends JPanel {
    public PlaceholderPanel(String text) {
        setLayout(new GridBagLayout());
        add(new JLabel(text));
    }
}
