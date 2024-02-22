public class App {
    private final ProductManager productManager;
    private final UserManager userManager;

    private final DatabaseConnection dbConnection;

//    ProductManager productManager, UserManager userManager
    public App() {

        this.dbConnection = new DatabaseConnection("jdbc:postgresql://db-postgresql-ams3-47505-do-user-15053769-0.c.db.ondigitalocean.com:25060/defaultdb", "doadmin", "AVNS_w_VlxtdGYg344M9PbZC");
        this.productManager = new ProductManager(dbConnection);
        this.userManager = new UserManager(dbConnection);
    }

    // Methods using productManager and userManager interfaces
}

/* import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class App {

    private final String url = "jdbc:postgresql://db-postgresql-ams3-47505-do-user-15053769-0.c.db.ondigitalocean.com:25060/defaultdb";
    private final String user = "doadmin";
    private final String password = "AVNS_w_VlxtdGYg344M9PbZC";

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public void displayProducts() {
        try (Connection conn = connect();
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
        try (Connection conn = connect()) {
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
        try (Connection conn = connect();
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
        try (Connection conn = connect();
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

    public List<Product> getUserProducts(int customer_id) {
        List<Product> product_list = new ArrayList<>();
        try (Connection conn = connect();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT c.product_id AS product_id, c.quantity AS quantity, p.price AS price, p.name AS name FROM Cart c INNER JOIN Product p ON c.product_id = p.product_id WHERE customer_id = %s".formatted(customer_id))) {
            while (resultSet.next()) {
                int product_id = resultSet.getInt("product_id");
                int product_quantity = resultSet.getInt("quantity");
                double product_price = resultSet.getDouble("price");
                String product_name = resultSet.getString("name");
                Product current_product = new Product(product_id, product_name, product_price, product_quantity);
                product_list.add(current_product);
            }
        } catch (SQLException e) {
            System.out.println("Error creating customer: " + e.getMessage());
        }
        return product_list;
    }

    public double getStaffDiscount(int customer_id) {
        try (Connection conn = connect();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT discount FROM Staff WHERE customer_id = " + customer_id)) {

            if (resultSet.next()) {
                return resultSet.getDouble("discount");
            } else {
                return 0;
            }

        } catch (SQLException e) {
            System.out.println("Error getting staff discount: " + e.getMessage());
            return 0;
        }
    }

    public void addToCart(int customer_id, int product_id, int quantity) {
        try (Connection conn = connect()) {
            System.out.println(product_id);
            String inventorySql = "SELECT quantity FROM Inventory WHERE product_id = ?";
            try (PreparedStatement inventoryStatement = conn.prepareStatement(inventorySql)) {
                inventoryStatement.setInt(1, product_id);
                try (ResultSet inventoryResultSet = inventoryStatement.executeQuery()) {
                    if (inventoryResultSet.next()) {
                        System.out.println("Inventory has product");
                        int availableQuantity = inventoryResultSet.getInt("quantity");
                        if (availableQuantity >= quantity) {
                            // Обновляем количество в Inventory
                            int newInventoryQuantity = availableQuantity - quantity;

                            System.out.println("Updating inventory...");
                            String updateInventorySql = "UPDATE Inventory SET quantity = ? WHERE product_id = ?";
                            try (PreparedStatement updateInventoryStatement = conn.prepareStatement(updateInventorySql)) {
                                updateInventoryStatement.setInt(1, newInventoryQuantity);
                                updateInventoryStatement.setInt(2, product_id);
                                updateInventoryStatement.executeUpdate();
                                System.out.println("Quantity updated in Inventory.");

                                String isExistsSql = "SELECT quantity FROM Cart WHERE customer_id = ? AND product_id = ?";
                                try (PreparedStatement isExistsStatement = conn.prepareStatement(isExistsSql)) {
                                    isExistsStatement.setInt(1, customer_id);
                                    isExistsStatement.setInt(2, product_id);
                                    try (ResultSet resultSet = isExistsStatement.executeQuery()) {
                                        if (resultSet.next()) {
                                            int existingQuantity = resultSet.getInt("quantity");
                                            int newQuantity = existingQuantity + quantity;

                                            // Обновляем количество в корзине
                                            String updateCartSql = "UPDATE Cart SET quantity = ? WHERE customer_id = ? AND product_id = ?";
                                            try (PreparedStatement updateCartStatement = conn.prepareStatement(updateCartSql)) {
                                                updateCartStatement.setInt(1, newQuantity);
                                                updateCartStatement.setInt(2, customer_id);
                                                updateCartStatement.setInt(3, product_id);
                                                updateCartStatement.executeUpdate();
                                                System.out.println("Quantity updated in the cart.");
                                            }
                                        } else {
                                            // Добавляем продукт в корзину
                                            String cartSql = "INSERT INTO Cart(customer_id, product_id, quantity) VALUES(?, ?, ?)";
                                            try (PreparedStatement cartStatement = conn.prepareStatement(cartSql)) {
                                                cartStatement.setInt(1, customer_id);
                                                cartStatement.setInt(2, product_id);
                                                cartStatement.setInt(3, quantity);
                                                cartStatement.executeUpdate();
                                                System.out.println("Product added to the cart.");
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            System.out.println("Not enough quantity of the product in Inventory.");
                        }
                    } else {
                        // Продукт не найден в Inventory
                        System.out.println("Product not found in Inventory.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating/inserting into the cart: " + e.getMessage());
        }
    }


    public int removeFromCart(int customer_id, int product_id) {
        try (Connection conn = connect()) {
            // Check if the product exists in the cart
            String isExistsSql = "SELECT quantity FROM Cart WHERE customer_id = ? AND product_id = ?";
            try (PreparedStatement isExistsStatement = conn.prepareStatement(isExistsSql)) {
                isExistsStatement.setInt(1, customer_id);
                isExistsStatement.setInt(2, product_id);
                try (ResultSet resultSet = isExistsStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int quantity = resultSet.getInt("quantity");
                        String removeFromCartSql = "DELETE FROM Cart WHERE customer_id = ? AND product_id = ?";
                        try (PreparedStatement removeFromCartStatement = conn.prepareStatement(removeFromCartSql)) {
                            removeFromCartStatement.setInt(1, customer_id);
                            removeFromCartStatement.setInt(2, product_id);
                            removeFromCartStatement.executeUpdate();
                            System.out.println("Product removed from the cart.");
                            return quantity;
                        }
                    } else {
                        System.out.println("Product is not in the cart.");
                        return 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error removing from the cart: " + e.getMessage());
            return 0;
        }
    }

    public void decreaseProductQuantity(int customer_id, int product_id, int quantity) {
        try (Connection conn = connect()) {
            String isExistsSql = "SELECT quantity FROM Cart WHERE customer_id = ? AND product_id = ?";
            try (PreparedStatement isExistsStatement = conn.prepareStatement(isExistsSql)) {
                isExistsStatement.setInt(1, customer_id);
                isExistsStatement.setInt(2, product_id);
                try (ResultSet resultSet = isExistsStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int currentQuantity = resultSet.getInt("quantity");

                        // Проверяем, есть ли достаточное количество продукта для уменьшения
                        if (currentQuantity >= quantity) {
                            int newQuantity = currentQuantity - quantity;
                            String updateQuantitySql = "UPDATE Cart SET quantity = ? WHERE customer_id = ? AND product_id = ?";
                            try (PreparedStatement updateQuantityStatement = conn.prepareStatement(updateQuantitySql)) {
                                updateQuantityStatement.setInt(1, newQuantity);
                                updateQuantityStatement.setInt(2, customer_id);
                                updateQuantityStatement.setInt(3, product_id);
                                updateQuantityStatement.executeUpdate();
                                System.out.println("Product quantity updated in the cart.");
                            }
                        } else {
                            throw new IllegalArgumentException("Not enough quantity in the cart.");
                        }
                    } else {
                        System.out.println("Product is not in the cart.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error updating product quantity in the cart: " + e.getMessage());
        }
    }


    public boolean isStaff(int customer_id) {
        try (Connection conn = connect();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Staff WHERE customer_id = " + customer_id)) {

            return resultSet.next();

        } catch (SQLException e) {
            System.out.println("Error checking if customer is staff: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        try (Connection conn = connect()) {
            String loginSql = "SELECT * FROM Customer WHERE username = ? AND password = ?";
            try (PreparedStatement loginStatement = conn.prepareStatement(loginSql)) {
                loginStatement.setString(1, username);
                loginStatement.setString(2, password);

                try (ResultSet resultSet = loginStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int userId = resultSet.getInt("customer_id");
                        String email = resultSet.getString("email");

                        if (isStaff(userId)) {
                            Staff staff = new Staff(username, password, userId, email, getStaffDiscount(userId));
                            return staff;
                        } else {
                            Customer customer = new Customer(username, password, userId, email);
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
        try (Connection conn = connect()) {
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
//
                            customer.setCustomerId(customerId);
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
        try (Connection conn = connect()) {
            String staffSql = "INSERT INTO Staff(customer_id, discount) VALUES (?, ?) RETURNING staff_id";

            try (PreparedStatement staffStatement = conn.prepareStatement(staffSql, Statement.RETURN_GENERATED_KEYS)) {
                Customer tempCustomer = new Customer(staff.getUsername(), staff.getPassword(), staff.getUserId(), staff.getEmail());
                Customer registeredCustomer = registerCustomer(tempCustomer);

                if (registeredCustomer != null) {
                    staffStatement.setInt(1, registeredCustomer.getUserId());
                    staffStatement.setDouble(2, staff.getDiscount());

                    int affectedRows = staffStatement.executeUpdate();

                    if (affectedRows > 0) {
                        try (ResultSet resultSet = staffStatement.getGeneratedKeys()) {
                            if (resultSet.next()) {
                                int staffId = resultSet.getInt("staff_id");
                                return new Staff(staff.getUsername(), staff.getPassword(), registeredCustomer.getUserId(), staff.getEmail(), staff.getDiscount());
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


    public void deleteUser(User user) {
        if (user == null) {
            System.out.println("Invalid user object.");
            return;
        }

        int userId = user.getUserId();

        try (Connection conn = connect()) {
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

*/