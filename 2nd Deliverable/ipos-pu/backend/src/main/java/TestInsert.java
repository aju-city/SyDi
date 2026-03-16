import dao.NonCommercialMemberDAO;
import model.NonCommercialMember;

public class TestInsert {
    public static void main(String[] args) {
        NonCommercialMember member = new NonCommercialMember(
                "test@example.com",
                "1234567890",
                true,
                0
        );

        NonCommercialMemberDAO dao = new NonCommercialMemberDAO();
        dao.addMember(member);
    }
}