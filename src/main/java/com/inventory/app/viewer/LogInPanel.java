package com.inventory.app.viewer;

import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class LogInPanel extends JPanel {

    private MainFrame mainFrame;

    public LogInPanel(MainFrame mainFrame) {
        super(new GridLayout(1, 2));
        this.mainFrame = mainFrame;
        setBackground(new Color(245, 245, 245));

        GradientPanel leftPanel = new GradientPanel(AppConstants.PRIMARY_COLOR, AppConstants.SECONDARY_COLOR) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                Ellipse2D.Double circle1 = new Ellipse2D.Double(-150, -100, 450, 450);
                Ellipse2D.Double circle2 = new Ellipse2D.Double(getWidth() - 400, getHeight() - 400, 600, 600);
                Color c1 = color1.brighter();
                c1 = new Color(c1.getRed(), c1.getGreen(), c1.getBlue(), 50);
                Color c2 = color2.brighter();
                c2 = new Color(c2.getRed(), c2.getGreen(), c2.getBlue(), 50);
                g2d.setPaint(new GradientPaint(0, 0, c1, getWidth() - 1, getHeight() - 1, c2));
                g2d.fill(circle1);
                g2d.fill(circle2);
            }
        };
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbConstraints = new GridBagConstraints();
        gbConstraints.gridx = 0;
        gbConstraints.gridy = GridBagConstraints.RELATIVE;
        JLabel icon = new JLabel(new ImageIcon("src/main/resources/icons/factory.png"));
        StyledLabel label1 = new StyledLabel("Production System", Color.white, 40);
        label1.setFontStyle(Font.BOLD);
        label1.setOffset(4);
        StyledLabel label2 = new StyledLabel("Manage Production Lines", Color.white, 20);
        StyledLabel label3 = new StyledLabel("And Inventory", Color.white, 20);
        gbConstraints.insets = new Insets(0, 0, 10, 0);
        leftPanel.add(icon, gbConstraints);
        gbConstraints.insets = new Insets(0, 0, 9, 0);
        leftPanel.add(label1, gbConstraints);
        gbConstraints.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(label2, gbConstraints);
        leftPanel.add(label3, gbConstraints);
        add(leftPanel);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        RoundedPanel logInBox = new RoundedPanel(10);
        logInBox.setLayout(new BoxLayout(logInBox, BoxLayout.Y_AXIS));
        logInBox.setBackground(Color.white);
        JLabel welcomeLabel = new JLabel("Welcome Back!");
        JLabel signInLabel = new JLabel("sign in to continue to your account");
        JLabel roleLabel = new JLabel("Select Role");
        JLabel usernameLabel = new JLabel("User name");
        JLabel passwordLabel = new JLabel("Password");
        welcomeLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 35f));
        signInLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.ITALIC, 15f));
        roleLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 17f));
        usernameLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 17f));
        passwordLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 17f));
        JComboBox<String> roleBox = new JComboBox<>();
        JTextField usernameField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);
        usernameField.setFont(AppConstants.DEFAULT_FONT.deriveFont(15f));
        passwordField.setFont(AppConstants.DEFAULT_FONT.deriveFont(15f));
        roleBox.setMaximumSize(new Dimension(620, 60));
        usernameField.setMaximumSize(new Dimension(620, 60));
        passwordField.setMaximumSize(new Dimension(620, 60));
        Border border = BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(0, 30, 15, 0),
                                                           BorderFactory.createEtchedBorder());
        roleBox.setBorder(border);
        usernameField.setBorder(border);
        passwordField.setBorder(border);
        logInBox.add(Box.createRigidArea(new Dimension(50, 50)));
        logInBox.add(welcomeLabel);
        logInBox.add(signInLabel);
        logInBox.add(Box.createRigidArea(new Dimension(20, 40)));
        logInBox.add(roleLabel);
        logInBox.add(roleBox);
        logInBox.add(usernameLabel);
        logInBox.add(usernameField);
        logInBox.add(passwordLabel);
        logInBox.add(passwordField);
        gbConstraints.weightx = 1;
        gbConstraints.weighty = 1;
        gbConstraints.fill = GridBagConstraints.BOTH;
        gbConstraints.insets = new Insets(100, 150, 100, 150);
        rightPanel.add(logInBox, gbConstraints);
        add(rightPanel);
    }

}