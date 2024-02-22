import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class UserManager {
    private final DatabaseConnection dbConnector;

    public UserManager(DatabaseConnection dbConnector) {
        this.dbConnector = dbConnector;
    }

    public void deleteUser(User user) {
        if (user == null) {
            System.out.println("Invalid user object.");
            return;
        }

        int userId = user.getUserId();

        try (Connection conn = (Connection) dbConnector) {
            String customerSql = "DELETE FROM Customer WHERE customer_id = ?";
            try (PreparedStatement customerStatement = conn.prepareStatement(customerSql)) {
                customerStatement.setInt(1, userId);
                int customerResult = customerStatement.executeUpdate();

                if (customerResult > 0) {
                    String staffSql = "DELETE FROM Staff WHERE customer_id = ?";
                    try (PreparedStatement staffStatement = conn.prepareStatement(staffSql)) {
                        staffStatement.setInt(1, userId);
                        staffStatement.executeUpdate();
                    }

                    System.out.println("User deleted successfully.");
                } else {
                    System.out.println("User not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }
}


