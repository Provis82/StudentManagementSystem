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
    
    // For Remember Me functionality
    private Preferences prefs;
    
    public LoginFrame() {
        // Initialize preferences
        prefs = Preferences.userRoot().node(this.getClass().getName());
        
        setTitle("Student Management System - Login");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel for logo
        logoPanel = new JPanel();
        logoPanel.setBackground(new Color(70, 130, 180));
        logoPanel.setPreferredSize(new Dimension(450, 80));
        
        JLabel logoLabel = new JLabel("📚 STUDENT MANAGEMENT SYSTEM");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoPanel.add(logoLabel);
        
        // Add a simple logo icon
        JLabel iconLabel = new JLabel("🎓");
        iconLabel.setFont(new Font("Arial", Font.BOLD, 30));
        iconLabel.setForeground(Color.WHITE);
        logoPanel.add(iconLabel);
        
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        
        // Center panel for login form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Login Credentials"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);
        
        // Remember Me
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        rememberMe = new JCheckBox("Remember Me");
        formPanel.add(rememberMe, gbc);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        JButton loginButton = new JButton("Login");
        JButton resetButton = new JButton("Reset");
        
        loginButton.setPreferredSize(new Dimension(100, 35));
        resetButton.setPreferredSize(new Dimension(100, 35));
        
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(resetButton);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        // Message Label
        gbc.gridx = 1;
        gbc.gridy = 4;
        messageLabel = new JLabel("");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        messageLabel.setForeground(Color.RED);
        formPanel.add(messageLabel, gbc);
        
        // Progress Bar
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setVisible(false);
        formPanel.add(progressBar, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Load saved username if Remember Me was checked
        String savedUsername = prefs.get("username", "");
        if (!savedUsername.isEmpty()) {
            usernameField.setText(savedUsername);
            rememberMe.setSelected(true);
            passwordField.requestFocus();
        }
        
        add(mainPanel);
        
        // Login Button Action
        loginButton.addActionListener(e -> performLogin());
        
        // Reset Button Action
        resetButton.addActionListener(e -> resetFields());
        
        // Enter key press in password field triggers login
        passwordField.addActionListener(e -> performLogin());
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        // Validation using string manipulation
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("❌ Please enter username and password");
            return;
        }
        
        // Disable input during login simulation
        usernameField.setEnabled(false);
        passwordField.setEnabled(false);
        rememberMe.setEnabled(false);
        
        // Show progress bar
        progressBar.setVisible(true);
        progressBar.setValue(0);
        progressValue = 0;
        
        // Simulate login process with progress bar
        progressTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                progressValue += 2;
                progressBar.setValue(progressValue);
                
                if (progressValue >= 100) {
                    progressTimer.stop();
                    
                    // Check credentials against database
                    boolean validLogin = false;
                    String dbPassword = "";
                    
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        String sql = "SELECT password FROM users WHERE username=?";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setString(1, username);
                        ResultSet rs = ps.executeQuery();
                        
                        if (rs.next()) {
                            dbPassword = rs.getString("password");
                            // In real app, use proper password hashing
                            validLogin = password.equals(dbPassword);
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                        // Fallback to hardcoded admin if database fails
                        validLogin = username.equals("admin") && password.equals("admin123");
                    }
                    
                    if (validLogin) {
                        // Save username if Remember Me is checked
                        if (rememberMe.isSelected()) {
                            prefs.put("username", username);
                        } else {
                            prefs.remove("username");
                        }
                        
                        messageLabel.setText("✅ Login successful!");
                        messageLabel.setForeground(new Color(0, 150, 0));
                        
                        // Open Main Frame
                        new MainFrame().setVisible(true);
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
        messageLabel.setText("");
        progressBar.setVisible(false);
        if (progressTimer != null && progressTimer.isRunning()) {
            progressTimer.stop();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}