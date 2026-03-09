package sms.gui;

import javax.swing.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMe;
    private JLabel messageLabel;

    public LoginFrame() {

        setTitle("Student Management System - Login");
        setSize(350, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(null);

        // Username Label
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(30, 30, 80, 25);
        panel.add(userLabel);

        // Username Field
        usernameField = new JTextField();
        usernameField.setBounds(120, 30, 150, 25);
        panel.add(usernameField);

        // Password Label
        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(30, 70, 80, 25);
        panel.add(passLabel);

        // Password Field
        passwordField = new JPasswordField();
        passwordField.setBounds(120, 70, 150, 25);
        panel.add(passwordField);

        // Remember Me Checkbox
        rememberMe = new JCheckBox("Remember Me");
        rememberMe.setBounds(120, 100, 150, 25);
        panel.add(rememberMe);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(60, 140, 90, 30);
        panel.add(loginButton);

        // Reset Button
        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(170, 140, 90, 30);
        panel.add(resetButton);

        // Message Label
        messageLabel = new JLabel("");
        messageLabel.setBounds(90, 175, 200, 25);
        panel.add(messageLabel);

        add(panel);

        // Login Button Action
        loginButton.addActionListener(e -> performLogin());

        // Reset Button Action
        resetButton.addActionListener(e -> resetFields());
    }

    private void performLogin() {

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.equals("admin") && password.equals("admin123")) {

            messageLabel.setText("Login successful");

            new MainFrame().setVisible(true);
            dispose();
        } 
        else {
            messageLabel.setText("Invalid username or password");
        }
    }

    private void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText("");
    }
}