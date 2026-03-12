package sms;

import sms.gui.LoginFrame;
import sms.database.DatabaseConnection;
import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Start application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                // Test database connection
                System.out.println("Attempting to connect to MySQL...");
                DatabaseConnection.getConnection();
                System.out.println("✅ Database connected!");
                
                // Start login frame
                new LoginFrame().setVisible(true);
                
            } catch (SQLException e) {
                System.err.println("❌ Database connection failed!");
                System.err.println("Error: " + e.getMessage());
                
                // Show error dialog
                JOptionPane.showMessageDialog(null,
                    "Cannot connect to MySQL database!\n" +
                    "Please ensure:\n" +
                    "1. MySQL is running\n" +
                    "2. MySQL Connector JAR is in lib folder\n" +
                    "3. Database 'SMS' exists\n" +
                    "4. Username: root, Password: 1234\n\n" +
                    "Error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                
                e.printStackTrace();
            }
        });
    }
}