package service;

import dao.CommercialApplicationDAO;
import dao.NonCommercialMemberDAO;
import model.CommercialApplication;
import model.NonCommercialMember;

import java.sql.SQLException;

/**
 * Service implementation for PU operations.
 */
public class PUServiceImpl implements IPUService {

    private final CommercialApplicationDAO applicationDAO;
    private final NonCommercialMemberDAO memberDAO;

    public PUServiceImpl(CommercialApplicationDAO applicationDAO,
                         NonCommercialMemberDAO memberDAO) {
        this.applicationDAO = applicationDAO;
        this.memberDAO = memberDAO;
    }

    @Override
    public int submitCommercialApplication(CommercialApplication application) throws SQLException {

        // Default status if frontend did not set it
        if (application.getStatus() == null || application.getStatus().trim().isEmpty()) {
            application.setStatus("submitted");
        }

        return applicationDAO.createApplicationAndReturnId(application);
    }

    @Override
    public int registerNonCommercialMember(String email, String password) throws SQLException {

        NonCommercialMember existingMember = memberDAO.getMemberByEmail(email);
        if (existingMember != null) {
            return -1;
        }

        NonCommercialMember member = new NonCommercialMember();
        member.setEmail(email);
        member.setPassword(password);
        member.setMustChangePassword(false);
        member.setTotalOrders(0);

        return memberDAO.register(member);
    }

    @Override
    public String login(String email, String password) throws SQLException {

        // If email belongs to a commercial application, reject login here
        if (applicationDAO.emailExists(email)) {
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