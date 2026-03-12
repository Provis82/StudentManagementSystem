package sms.gui;

import sms.database.DatabaseConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.prefs.Preferences;

public class LoginFrame extends JFrame {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JCheckBox rememberMe;
    private JLabel messageLabel;
    private JProgressBar progressBar;
    private Timer progressTimer;
    private int progressValue = 0;
    private JPanel logoPanel;
    
    private Preferences prefs;
    
    public LoginFrame() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
        
        setTitle("Student Management System - Login");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Logo Panel with icon
        logoPanel = new JPanel();
        logoPanel.setBackground(new Color(70, 130, 180));
        logoPanel.setPreferredSize(new Dimension(450, 100));
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        
        // Create icon label with graduation cap
        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 48));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Title label
        JLabel titleLabel = new JLabel("STUDENT MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        
        logoPanel.add(iconLabel);
        logoPanel.add(titleLabel);
        
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Login Credentials"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        formPanel.add(userLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(15);
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 12));
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        formPanel.add(passLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 12));
        formPanel.add(passwordField, gbc);
        
        // Remember Me
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        rememberMe = new JCheckBox("Remember Me");
        rememberMe.setFont(new Font("Dialog", Font.PLAIN, 12));
        formPanel.add(rememberMe, gbc);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        
        // LOGIN BUTTON - Text weight changed to BOLD and color to BLACK
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 35));
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);  // Changed from WHITE to BLACK
        loginButton.setFont(new Font("Dialog", Font.BOLD, 12));  // Added BOLD font
        
        // RESET BUTTON - Unchanged
        JButton resetButton = new JButton();
        resetButton.setText("Reset");
        resetButton.setPreferredSize(new Dimension(100, 35));
        resetButton.setBackground(new Color(220, 220, 220));
        resetButton.setFont(new Font("Dialog", Font.BOLD, 12));
        resetButton.setBorder(BorderFactory.createLoweredBevelBorder());
        
        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(buttonPanel, gbc);
        
        // Message Label
        gbc.gridx = 1; gbc.gridy = 4;
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(messageLabel, gbc);
        
        // Progress Bar
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        progressBar.setForeground(new Color(70, 130, 180));
        progressBar.setFont(new Font("Dialog", Font.PLAIN, 10));
        formPanel.add(progressBar, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        // Load saved username
        String savedUsername = prefs.get("username", "");
        if (!savedUsername.isEmpty()) {
            usernameField.setText(savedUsername);
            rememberMe.setSelected(true);
            passwordField.requestFocus();
        }
        
        // Actions
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetFields();
            }
        });
        
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Make sure window is visible
        setVisible(true);
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("❌ Please enter username and password");
            return;
        }
        
        // Disable input during login
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);
        rememberMe.setEnabled(false);
        
        // Show and start progress bar
        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressValue = 0;
        
        progressTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressValue += 2;
                progressBar.setValue(progressValue);
                
                if (progressValue >= 100) {
                    progressTimer.stop();
                    
                    boolean validLogin = false;
                    String fullName = "";
                    
                    // Check against database
                    String sql = "SELECT * FROM users WHERE username=? AND password=?";
                    
                    try (Connection conn = DatabaseConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(sql)) {
                        
                        ps.setString(1, username);
                        ps.setString(2, password);
                        ResultSet rs = ps.executeQuery();
                        
                        if (rs.next()) {
                            validLogin = true;
                            fullName = rs.getString("full_name");
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Fallback only if database fails
                        if (username.equals("admin") && password.equals("admin123")) {
                            validLogin = true;
                            fullName = "Administrator";
                        }
                    }
                    
                    if (validLogin) {
                        if (rememberMe.isSelected()) {
                            prefs.put("username", username);
                        } else {
                            prefs.remove("username");
                        }
                        
                        messageLabel.setText("✅ Welcome, " + (fullName.isEmpty() ? username : fullName) + "!");
                        messageLabel.setForeground(new Color(0, 150, 0));
                        
                        // Open Main Frame
                        MainFrame mainFrame = new MainFrame();
                        mainFrame.setVisible(true);
                        dispose();
                    } else {
                        messageLabel.setText("❌ Invalid username or password");
                        messageLabel.setForeground(Color.RED);
                        
                        // Re-enable input
                        usernameField.setEnabled(true);
                        passwordField.setEnabled(true);
                        rememberMe.setEnabled(true);
                        progressBar.setVisible(false);
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                }
            }
        });
        
        progressTimer.start();
    }
    
    private void resetFields() {
        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText(" ");
        progressBar.setVisible(false);
        if (progressTimer != null && progressTimer.isRunning()) {
            progressTimer.stop();
        }
        usernameField.setEnabled(true);
        passwordField.setEnabled(true);
        rememberMe.setEnabled(true);
        usernameField.requestFocus();
    }
}