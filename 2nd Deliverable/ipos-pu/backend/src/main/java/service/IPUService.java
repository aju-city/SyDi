package service;

import model.CommercialApplication;

import java.sql.SQLException;

/**
 * Service interface for PU operations.
 */
public interface IPUService {

    int submitCommercialApplication(CommercialApplication application) throws SQLException;

    String registerNonCommercialMember(String email) throws SQLException;

    String login(String email, String password) throws SQLException;
}