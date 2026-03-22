package service;

import dao.CommercialApplicantDAO;
import dao.CompanyDirectorDAO;
import dao.NonCommercialMemberDAO;
import model.CommercialApplicant;
import model.CompanyDirector;
import model.NonCommercialMember;
import db.DatabaseConnection;
import java.sql.Connection;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class PUServiceImpl implements IPUService {

    private final CommercialApplicantDAO applicantDAO;
    private final CompanyDirectorDAO directorDAO;
    private final NonCommercialMemberDAO memberDAO;

    public PUServiceImpl(CommercialApplicantDAO applicantDAO,
                         CompanyDirectorDAO directorDAO,
                         NonCommercialMemberDAO memberDAO) {
        this.applicantDAO = applicantDAO;
        this.directorDAO = directorDAO;
        this.memberDAO = memberDAO;
    }

    @Override
    public int submitCommercialApplication(CommercialApplicant applicant,
                                           List<CompanyDirector> directors) throws SQLException {

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // START TRANSACTION

            // Default values
            if (applicant.getApplicationStatus() == null) {
                applicant.setApplicationStatus("Pending");
            }
            if (applicant.getApplicationDate() == null) {
                applicant.setApplicationDate(new Date(System.currentTimeMillis()));
            }

            // 1. Insert applicant (NEW METHOD WITH CONNECTION)
            int applicationId = applicantDAO.addApplicant(applicant, conn);

            // 2. Insert directors
            for (CompanyDirector d : directors) {
                d.setApplicationID(applicationId);
                directorDAO.addDirector(d, conn);
            }

            conn.commit(); // SUCCESS
            return applicationId;

        } catch (Exception e) {

            if (conn != null) {
                try {
                    conn.rollback(); // rollback everything
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

            e.printStackTrace();
            throw new SQLException("Transaction failed");

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public int registerNonCommercialMember(String email, String password) throws SQLException {

        NonCommercialMember member = new NonCommercialMember();
        member.setEmail(email);
        member.setPassword(password);
        member.setMustChangePassword(false);
        member.setTotalOrders(0);

        return memberDAO.register(member);
    }

    @Override
    public String login(String email, String password) throws SQLException {

        // If email belongs to a commercial applicant, reject
        if (memberDAO.emailExistsInCommercial(email)) {
            return "Commercial applicants/members can not log in here.";
        }

        //Check non-commercial member credentials
        NonCommercialMember member = memberDAO.login(email, password);

        if (member != null) {
            return "Login successful";
        }

        return "Invalid email or password.";
    }
}
