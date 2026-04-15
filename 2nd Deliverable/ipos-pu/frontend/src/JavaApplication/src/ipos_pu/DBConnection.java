/*
 * handles the jdbc connection to the mysql database
 * all other classes just call DBConnection.getConnection() to get a live connection
 */
package ipos_pu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author nuhur
 */
public class DBConnection {

    // connection details for the local ipos_ca database
    private static final String URL  = "jdbc:mysql://localhost:3306/ipos_ca";
    private static final String USER = "root";
    private static final String PASS = "Nurse=27";

    // returns a fresh connection each time, caller is responsible for closing it
    public static Connection getConnection() throws SQLException {
        try {
            // make sure the mysql driver is loaded before we try to connect
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // driver jar not on classpath, add mysql-connector-j to project libraries
            throw new SQLException("MySQL driver not found: " + e.getMessage());
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
