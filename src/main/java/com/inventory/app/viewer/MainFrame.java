package com.inventory.app.viewer;
import javax.swing.*;
import javax.swing.*;
import com.inventory.app.viewer.LogInPanel;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Production & Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // إعداد النافذة لتكون ملء الشاشة
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // ensure a sensible minimum so components render nicely when not maximized
        setMinimumSize(new Dimension(900, 600));

        // Add login panel (the panel itself handles its internal layout)
        add(new LogInPanel(), BorderLayout.CENTER);
        // center on screen
        setLocationRelativeTo(null);
    }
}