import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductManager implements ProductManagerInterface {

    private final DatabaseConnection dbConnector;

    public ProductManager(DatabaseConnection dbConnector) {
        this.dbConnector = dbConnector;
    }

    // Implement methods for product management

    public void displayProducts() {
        try (Connection conn = (Connection) dbConnector;
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT p.product_id, p.name, p.price, i.quantity FROM Inventory i INNER JOIN Product p ON i.product_id = p.product_id")) {
            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                int product_id = rs.getInt("product_id");

                System.out.print("Id: " + product_id);
                System.out.print(", Name: " + name);
                System.out.print(", Price: " + price);
                System.out.println(", Quantity: " + quantity);
            }

        } catch (SQLException e) {
            System.out.println("Error displaying products: " + e.getMessage());
        }
    }


    public void createProduct(Product product) {
        try (Connection conn = (Connection) dbConnector) {
            String productSql = "INSERT INTO Product(name, price) VALUES (?, ?) RETURNING product_id";

            try (PreparedStatement productStatement = conn.prepareStatement(productSql)) {
                productStatement.setString(1, product.getProductName());
                productStatement.setDouble(2, product.getProductPrice());

                try (ResultSet resultSet = productStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int productId = resultSet.getInt("product_id");
                        String inventorySql = "INSERT INTO Inventory(product_id, quantity) VALUES(?, ?)";
                        try (PreparedStatement inventoryStatement = conn.prepareStatement(inventorySql)) {
                            inventoryStatement.setInt(1, productId);
                            inventoryStatement.setInt(2, product.getProductQuantity());
                            inventoryStatement.executeUpdate();
                        }
                        if (product instanceof Device) {
                            Device device = (Device) product;
                            String deviceSql = "INSERT INTO Device(product_id, os, screen_size) VALUES (?, ?, ?)";
                            try (PreparedStatement deviceStatement = conn.prepareStatement(deviceSql)) {
                                deviceStatement.setInt(1, productId);
                                deviceStatement.setString(2, device.getOs());
                                deviceStatement.setDouble(3, device.getScreenSize());
                                deviceStatement.executeUpdate();
                                System.out.println("Device created successfully.");
                            }
                        } else if (product instanceof Domestic) {
                            Domestic domestic = (Domestic) product;
                            String domesticSql = "INSERT INTO Domestic(product_id, can_connect_to_wifi) VALUES (?, ?)";
                            try (PreparedStatement domesticStatement = conn.prepareStatement(domesticSql)) {
                                domesticStatement.setInt(1, productId);
                                domesticStatement.setBoolean(2, domestic.getCanConnect());
                                domesticStatement.executeUpdate();
                                System.out.println("Domestic product created successfully.");
                            }
                        } else {
                            System.out.println("Unknown product type.");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error creating product: " + e.getMessage());
        }
    }


    public Product getProductById(int product_id) {
        try (Connection conn = (Connection) dbConnector;
             PreparedStatement statement = conn.prepareStatement("SELECT p.product_id, p.name, p.price, i.quantity FROM Product p JOIN Inventory i ON p.product_id = i.product_id WHERE p.product_id = ?")) {


            statement.setInt(1, product_id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String productName = resultSet.getString("name");
                    double productPrice = resultSet.getDouble("price");
                    int quantity = resultSet.getInt("quantity");
                    System.out.println("Name: " + productName);
                    return new Product(product_id, productName, productPrice, quantity);
                } else {
                    System.out.println("Product not found with ID: " + product_id);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving product: " + e.getMessage());
        }
        return null;
    }

    public void deleteProduct(int productId) {
        try (Connection conn = (Connection) dbConnector;
             PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM Product WHERE product_id=?")) {
            preparedStatement.setInt(1, productId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Product deleted successfully.");
            } else {
                System.out.println("Product not found or delete failed.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
    }

    @Override
    public void addToCart(int customerId, int productId, int quantity) {
        return;
    }

    @Override
    public int removeFromCart(int customerId, int productId) {
        return 0;
    }

}
