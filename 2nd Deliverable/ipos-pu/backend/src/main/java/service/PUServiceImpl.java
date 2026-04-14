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

    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10)); // 0–9
        }
        return sb.toString();
    }

    @Override
    public int submitCommercialApplication(CommercialApplication application) throws SQLException {

        // 1. Basic validation
        if (application.getCompanyName() == null || application.getCompanyName().isEmpty()) {
            throw new IllegalArgumentException("Company name is required.");
        }
        if (application.getEmail() == null || application.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required.");
        }

        // 2. Prevent duplicate commercial applications by email
        if (applicationDAO.emailExists(application.getEmail())) {
            throw new IllegalArgumentException("An application with this email already exists.");
        }

        // 3. Default status
        if (application.getStatus() == null || application.getStatus().trim().isEmpty()) {
            application.setStatus("submitted");
        }

        // 4. Default reviewed_by and notes
        application.setReviewedBy(null);
        application.setNotes(null);

        // 5. Director formatting (Functionality 2)
        if (application.getDirectorDetails() == null || application.getDirectorDetails().trim().isEmpty()) {
            throw new IllegalArgumentException("Director details are required.");
        }

        // 6. Insert and return generated ID
        return applicationDAO.createApplicationAndReturnId(application);
    }

    @Override
    public String registerNonCommercialMember(String email) throws SQLException {

        // 1. Check if email already exists
        NonCommercialMember existingMember = memberDAO.getMemberByEmail(email);
        if (existingMember != null) {
            return null; // frontend will show "email already exists"
        }

        // 2. Generate random 10-digit password
        String generatedPassword = generateRandomPassword();

        // 3. Create member object
        NonCommercialMember member = new NonCommercialMember();
        member.setEmail(email);
        member.setPassword(generatedPassword);
        member.setMustChangePassword(true);
        member.setTotalOrders(0);

        // 4. Insert and get generated member_id
        int memberId = memberDAO.register(member);

        // 5. Generate MemberAccountNo (PU0001)
        String accountNo = String.format("PU%04d", memberId);

        // 6. Update the row with the account number
        memberDAO.updateAccountNumber(memberId, accountNo);

        // 7. Return the generated password so frontend can show it
        return generatedPassword;
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