import java.sql.*;
import java.util.ArrayList;

public class Authorization implements AuthorizationInteface {
    private final DatabaseConnection dbConnector;
    private final ShoppingCartManager shoppingCartManager;

    public Authorization(DatabaseConnection dbConnector, ShoppingCartManager shoppingCartManager) {
        this.dbConnector = dbConnector;
        this.shoppingCartManager = shoppingCartManager;
    }
    public boolean isStaff(int customer_id) {
        try (Connection conn = (Connection) dbConnector.connect();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Staff WHERE customer_id = " + customer_id)) {

            return resultSet.next();

        } catch (SQLException e) {
            System.out.println("Error checking if customer is staff: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        try (Connection conn = (Connection) dbConnector.connect()){
            String loginSql = "SELECT * FROM Customer WHERE username = ? AND password = ?";
            try (PreparedStatement loginStatement = conn.prepareStatement(loginSql)) {
                loginStatement.setString(1, username);
                loginStatement.setString(2, password);

                try (ResultSet resultSet = loginStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("customer_id");
                        String email = resultSet.getString("email");
                        if (isStaff(userId)) {
                            Staff staff = new Staff(username, password, userId, email, shoppingCartManager.getUserProducts(userId));
                            return staff;
                        } else {
                            Customer customer = new Customer(username, password, userId, email, shoppingCartManager.getUserProducts(userId));
                            return customer;
                        }
                    } else {
                        throw new IllegalArgumentException("Incorrect username or password");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("Error during login:" + e.getMessage());
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
            return null;
        }
    }


    public Customer registerCustomer(Customer customer) {
        try (Connection conn = (Connection) dbConnector) {
            String customerSql = "INSERT INTO Customer(username, password, email) VALUES (?, ?, ?) RETURNING customer_id";

            try (PreparedStatement customerStatement = conn.prepareStatement(customerSql, Statement.RETURN_GENERATED_KEYS)) {
                customerStatement.setString(1, customer.getUsername());
                customerStatement.setString(2, customer.getPassword());
                customerStatement.setString(3, customer.getEmail());

                int affectedRows = customerStatement.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet resultSet = customerStatement.getGeneratedKeys()) {
                        if (resultSet.next()) {
                            int customerId = resultSet.getInt("customer_id");
                            customer.setUserId(customerId);
                            return customer;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating customer: " + e.getMessage());
        }

        return null;
    }

    public Staff registerStaff(Staff staff) {
        try (Connection conn = (Connection) dbConnector) {
            String staffSql = "INSERT INTO Staff(customer_id, discount) VALUES (?, ?) RETURNING staff_id";

            try (PreparedStatement staffStatement = conn.prepareStatement(staffSql, Statement.RETURN_GENERATED_KEYS)) {
                Customer tempCustomer = new Customer(staff.getUsername(), staff.getPassword(), staff.getUserId(), staff.getEmail(), new ArrayList<>());
                Customer registeredCustomer = registerCustomer(tempCustomer);

                if (registeredCustomer != null) {
                    staffStatement.setInt(1, registeredCustomer.getUserId());
                    staffStatement.setDouble(2, staff.getDiscount());

                    int affectedRows = staffStatement.executeUpdate();

                    if (affectedRows > 0) {
                        try (ResultSet resultSet = staffStatement.getGeneratedKeys()) {
                            if (resultSet.next()) {
                                int staffId = resultSet.getInt("staff_id");
                                return new Staff(staff.getUsername(), staff.getPassword(), registeredCustomer.getUserId(), staff.getEmail(), new ArrayList<>());
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating staff: " + e.getMessage());
        }

        return null;
    }

}
