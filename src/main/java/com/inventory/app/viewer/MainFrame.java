package com.inventory.app.viewer;
import javax.swing.*;
import javax.swing.*;
import com.inventory.app.viewer.LogInPanel;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Production & Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));

        add(new LogInPanel(), BorderLayout.CENTER);
        setLocationRelativeTo(null);
    }
    public static void startMainFrame()
    {
        SwingUtilities.invokeLater(() -> {
            try {
                // محاولة تفعيل الستايل الخاص بالنظام
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}