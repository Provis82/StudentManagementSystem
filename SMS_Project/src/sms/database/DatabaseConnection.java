package sms.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String HOST     = "localhost";
    private static final String PORT     = "3306";
    private static final String DATABASE = "SMS";  // Changed from sms_db to SMS
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";

    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" +
                                      DATABASE + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Connected to database: " + DATABASE);
            } catch (ClassNotFoundException e) {
                System.err.println("❌ MySQL Driver not found!");
                throw new SQLException("MySQL Driver not found. Check JAR file in lib folder.");
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("✅ Database connection closed.");
            } catch (SQLException e) {
                System.err.println("❌ Error closing connection: " + e.getMessage());
            }
        }
    }
}