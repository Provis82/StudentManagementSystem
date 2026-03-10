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
        
        // Test database connection on startup
        SwingUtilities.invokeLater(() -> {
            try {
                // Test database connection
                DatabaseConnection.getConnection();
                System.out.println("✓ Database connection successful");
                
                // Start application
                new LoginFrame().setVisible(true);
                
            } catch (SQLException e) {
                // Show error dialog if database connection fails
                JOptionPane.showMessageDialog(null,
                    "Cannot connect to database!\n" +
                    "Please ensure MySQL is running and database is created.\n" +
                    "Error: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}