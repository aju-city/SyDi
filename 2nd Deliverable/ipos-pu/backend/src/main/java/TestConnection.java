import db.DatabaseConnection;
import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {

        try (Connection conn = DatabaseConnection.getConnection()) {

            if (conn != null) {
                System.out.println("Connected to ipos_pu");
            } else {
                System.out.println("Not connected");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}