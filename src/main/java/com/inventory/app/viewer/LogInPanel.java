package com.inventory.app.viewer;

import com.inventory.app.util.AppConstants;
import com.inventory.app.viewer.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;

public class LogInPanel extends JPanel {

    private final String name = "Log in";
    private final MainFrame mainFrame;

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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;

        JLabel icon = new JLabel(new ImageIcon("src/main/resources/icons/factory.png"));
        ShadowedLabel label1 = new ShadowedLabel("Production System", 3);
        ShadowedLabel label2 = new ShadowedLabel("Manage Production Lines");
        ShadowedLabel label3 = new ShadowedLabel("And Inventory");
        label1.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 40f));
        label2.setFont(AppConstants.DEFAULT_FONT.deriveFont(20f));
        label3.setFont(AppConstants.DEFAULT_FONT.deriveFont(20f));
        label1.setForeground(Color.white);
        label2.setForeground(Color.white);
        label3.setForeground(Color.white);
        gbc.insets = new Insets(0, 0, 10, 0);
        leftPanel.add(icon, gbc);
        gbc.insets = new Insets(0, 0, 9, 0);
        leftPanel.add(label1, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(label2, gbc);
        leftPanel.add(label3, gbc);
        add(leftPanel);



        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setOpaque(false);
        RoundedPanel logInBox = new RoundedPanel(20);
        logInBox.setLayout(new GridBagLayout());
        logInBox.setBackground(Color.white);
        JLabel welcomeLabel = new JLabel("Welcome Back!");
        JLabel signInLabel = new JLabel("sign in to continue to your account");
        JLabel roleLabel = new JLabel("Select Role");
        JLabel usernameLabel = new JLabel("Username");
        JLabel passwordLabel = new JLabel("Password");
        JLabel errorLabel = new JLabel("");
        signInLabel.setForeground(Color.gray);
        errorLabel.setForeground(Color.red);
        welcomeLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 35f));
        signInLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.ITALIC, 15f));
        roleLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 17f));
        usernameLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 17f));
        passwordLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(Font.BOLD, 17f));
        errorLabel.setFont(AppConstants.DEFAULT_FONT.deriveFont(15f));

        JComboBox<String> roleBox = new JComboBox<>(new String[] {"Manager", "Supervisor"});
        JTextField usernameField = new JTextField(10);
        JPasswordField passwordField = new JPasswordField(10);
        usernameField.setFont(AppConstants.DEFAULT_FONT.deriveFont(15f));
        passwordField.setFont(AppConstants.DEFAULT_FONT.deriveFont(15f));
        roleBox.setPreferredSize(new Dimension(620, 60));
        usernameField.setPreferredSize(new Dimension(620, 60));
        passwordField.setPreferredSize(new Dimension(620, 60));

        JButton logInButton = new JButton("Log in");
        logInButton.setBackground(AppConstants.PRIMARY_COLOR);
        logInButton.setForeground(Color.white);
        logInButton.setFont(AppConstants.DEFAULT_FONT.deriveFont(17f));
        logInButton.setPreferredSize(new Dimension(520, 60));
        logInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                /*

                log in logic

                 */
            }
        });

        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(20, 30, 0, 30);
        logInBox.add(welcomeLabel, gbc);
        gbc.insets = new Insets(0, 30, 20, 30);
        logInBox.add(signInLabel, gbc);
        gbc.insets = new Insets(0, 30, 0, 30);
        logInBox.add(roleLabel, gbc);
        gbc.insets = new Insets(0, 30, 10, 30);
        logInBox.add(roleBox, gbc);
        gbc.insets = new Insets(0, 30, 0, 30);
        logInBox.add(usernameLabel, gbc);
        gbc.insets = new Insets(0, 30, 10, 30);
        logInBox.add(usernameField, gbc);
        gbc.insets = new Insets(0, 30, 0, 30);
        logInBox.add(passwordLabel, gbc);
        gbc.insets = new Insets(0, 30, 10, 30);
        logInBox.add(passwordField, gbc);
        gbc.insets = new Insets(0, 30, 10, 30);
        logInBox.add(errorLabel, gbc);
        gbc.insets = new Insets(0, 30, 10, 30);
        logInBox.add(logInButton, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(100, 150, 100, 150);
        rightPanel.add(logInBox, gbc);
        add(rightPanel);
    }

    public String getName() {
        return name;
    }

}