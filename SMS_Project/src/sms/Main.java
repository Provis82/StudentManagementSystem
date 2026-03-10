package sms;

import sms.gui.LoginFrame;
import sms.database.DatabaseConnection;
import javax.swing.*;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                DatabaseConnection.getConnection();
                System.out.println("✓ Database connected!");
                new LoginFrame().setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                    "Database Error!\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}