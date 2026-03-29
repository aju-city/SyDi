import model.CommercialApplicant;
import model.CompanyDirector;
import service.IPUService;
import service.PUServiceFactory;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TestCommercialApplicantInsert {

    public static void main(String[] args) {

        // Get the service instance
        IPUService service = PUServiceFactory.getService();

        //Create a commercial applicant
        CommercialApplicant applicant = new CommercialApplicant();
        applicant.setCompanyName("Test Company Ltd");
        applicant.setCompanyRegistrationNumber("12345678");
        applicant.setBusinessType("Retail");
        applicant.setEmail("co_test@example.com");
        applicant.setNotificationPreference("Email");
        applicant.setCompanyAddress("123 Test Street, London");

        // Optional: leave ApplicationDate and ApplicationStatus null to test defaults
        // applicant.setApplicationDate(null);
        // applicant.setApplicationStatus(null);

        // Create directors
        List<CompanyDirector> directors = new ArrayList<>();

        CompanyDirector d1 = new CompanyDirector();
        d1.setFirstName("John");
        d1.setLastName("Doe");
        d1.setPhoneNumber("111111111");

        CompanyDirector d2 = new CompanyDirector();
        d2.setFirstName("Jane");
        d2.setLastName("Smith");
        d2.setPhoneNumber("222222222");

        directors.add(d1);
        directors.add(d2);

        try {
            // Submit commercial application
            int applicationId = service.submitCommercialApplication(applicant, directors);

            System.out.println("Commercial application submitted successfully.");
            System.out.println("Generated ApplicationID: " + applicationId);

            // Instructions for verifying in MySQL:
            System.out.println("\nCheck in MySQL:");
            System.out.println("SELECT * FROM CommercialApplicant WHERE ApplicationID = " + applicationId + ";");
            System.out.println("SELECT * FROM CompanyDirector WHERE ApplicationID = " + applicationId + ";");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to submit commercial application.");
        }
    }
}