package com.inventory.app;

import com.inventory.app.util.ErrorLog;
import com.inventory.app.viewer.ErrorLogViewerPanel;

import javax.swing.*;

public class TestErrorLogUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            ErrorLog.installGlobalHandlers();

            JFrame f = new JFrame("Error Logs - Test");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(1100, 650);
            f.setLocationRelativeTo(null);

            f.setContentPane(new ErrorLogViewerPanel());

            f.setVisible(true);

            ErrorLog.info("TestErrorLogUI", "Viewer opened");
            ErrorLog.warn("TestErrorLogUI", "This is a warning test");

            try {
                throw new RuntimeException("Boom! test exception");
            } catch (Exception ex) {
                ErrorLog.error("TestErrorLogUI.manualCatch", ex);
            }
        });
    }
}
