import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartManager implements ShoppingCartManagerInterface {
    private final DatabaseConnection dbConnector;

    public ShoppingCartManager(DatabaseConnection dbConnector) {
        this.dbConnector = dbConnector;
    }
    public List<Product> getUserProducts(int customer_id) {
        List<Product> product_list = new ArrayList<>();
        try (Connection conn = (Connection) dbConnector.connect();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT c.product_id AS product_id, c.quantity AS quantity, p.price AS price, p.name AS name FROM Cart c INNER JOIN Product p ON c.product_id = p.product_id WHERE customer_id = %s".formatted(customer_id))) {
            while (resultSet.next()) {
                int product_id = resultSet.getInt("product_id");
                int product_quantity = resultSet.getInt("quantity");
                double product_price = resultSet.getDouble("price");
                String product_name = resultSet.getString("name");
                Product current_product = new Product(product_id, product_name, product_price, product_quantity);
                product_list.add(current_product);            }
        } catch (SQLException e) {
            System.out.println("Error creating customer: " + e.getMessage());
        }
        return product_list;
    }
}
