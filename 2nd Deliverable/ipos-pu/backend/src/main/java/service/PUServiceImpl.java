package service;

import dao.CommercialApplicantDAO;
import dao.CompanyDirectorDAO;
import dao.NonCommercialMemberDAO;
import model.CommercialApplicant;
import model.CompanyDirector;
import model.NonCommercialMember;

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

        // Default values if frontend didn't set them
        if (applicant.getApplicationStatus() == null) {
            applicant.setApplicationStatus("Pending");
        }
        if (applicant.getApplicationDate() == null) {
            applicant.setApplicationDate(new Date(System.currentTimeMillis()));
        }

        // 1. Insert the commercial applicant
        int applicationId = applicantDAO.create(applicant);

        // 2. Insert each director linked to this application
        for (CompanyDirector d : directors) {
            d.setApplicationID(applicationId);   // matches your model naming
            directorDAO.addDirector(d);
        }

        return applicationId;
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

        // If email belongs to a commercial applicant → reject
        if (memberDAO.emailExistsInCommercial(email)) {
            return "Commercial applicants/members can not log in here.";
        }

        // Check non-commercial member credentials
        NonCommercialMember member = memberDAO.login(email, password);

        if (member != null) {
            return "Login successful";
        }

        return "Invalid email or password.";
    }
}
