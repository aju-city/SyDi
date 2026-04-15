package db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * DatabaseConnection
 *
 * Provides a reusable method for establishing a connection to the database.
 *
 * Configuration details (URL, username, password) are loaded from a
 * config.properties file located in the resources folder.
 *
 * This class is used by all DAO classes to obtain a database connection.
 */
public class DatabaseConnection {

    /**
     * Creates and returns a database connection.
     *
     * Steps:
     * 1. Loads database configuration from config.properties
     * 2. Reads connection details (URL, user, password)
     * 3. Uses DriverManager to establish the connection
     *
     * @return Connection object if successful, otherwise null
     */
    public static Connection getConnection() {

        try {

            // Load properties file from resources folder
            Properties props = new Properties();

            InputStream input = DatabaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream("config.properties");

            if (input == null) {
                System.err.println(
                        "config.properties not found on the classpath. "
                                + "Add it under src/main/resources/ and rebuild the project.");
                return null;
            }

            // Load values from config file
            props.load(input);

            // Retrieve database credentials
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");

            // Establish and return connection
            return DriverManager.getConnection(url, user, password);

        } catch (Exception e) {

            // Print error if connection fails
            System.out.println("Database connection failed.");
            e.printStackTrace();

            return null;
        }
    }
}