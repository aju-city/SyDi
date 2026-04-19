package ipos_pu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 Provides JDBC connections for the local IPOS_PU database.
 @author nuhur
 */
public class DBConnection {

    private static final String URL  = "jdbc:mysql://localhost:3306/ipos_pu?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "YourPassword";

    /**
     Creates and returns a new database connection.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}