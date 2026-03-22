import dao.NonCommercialMemberDAO;
import model.NonCommercialMember;

public class TestInsert {
    public static void main(String[] args) {
        // 1. Create member
        NonCommercialMember member = new NonCommercialMember();
        member.setEmail("test@example.com");
        member.setPassword("1234567890");
        member.setMustChangePassword(true);
        member.setTotalOrders(0);

        // 2. Create DAO
        NonCommercialMemberDAO dao = new NonCommercialMemberDAO();

        try {
            // 3. Insert member
            int memberId = dao.register(member);
            System.out.println("Member inserted successfully. ID: " + memberId);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to insert member.");
        }
    }
}