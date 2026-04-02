package service;

import dao.CommercialApplicationDAO;
import dao.NonCommercialMemberDAO;
import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Factory class for creating PU service instances.
 */
public class PUServiceFactory {

    private static IPUService instance;

    public static IPUService getService() throws SQLException {
        if (instance == null) {
            Connection connection = DatabaseConnection.getConnection();

            instance = new PUServiceImpl(
                    new CommercialApplicationDAO(connection),
                    new NonCommercialMemberDAO(connection)
            );
        }

        return instance;
    }
}