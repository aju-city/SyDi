package service;

import model.CommercialApplicant;
import model.CompanyDirector;

import java.sql.SQLException;
import java.util.List;

public interface IPUService {

    int submitCommercialApplication(CommercialApplicant applicant,
                                    List<CompanyDirector> directors) throws SQLException;

    int registerNonCommercialMember(String email, String password) throws SQLException;

    String login(String email, String password) throws SQLException;
}
