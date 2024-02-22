import java.util.Scanner;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection dbconnector = new DatabaseConnection("jdbc:postgresql://db-postgresql-ams3-47505-do-user-15053769-0.c.db.ondigitalocean.com:25060/defaultdb", "doadmin", "AVNS_w_VlxtdGYg344M9PbZC");
        ShoppingCartManager shmanger = new ShoppingCartManager(dbconnector);
        Authorization auth = new Authorization(dbconnector, shmanger);
        User user1 = auth.login("VladPerchik4", "qwerty4");
        user1.displayInfo();

        /* создание новых кастомеров через фактори*/
        UserFactory customerFactory = new CustomerFactory();
        User customer = customerFactory.createUser("customer1", "password123", 1, "customer@example.com", new ArrayList<>());

        UserFactory staffFactory = new StaffFactory();
        User staff = staffFactory.createUser("staff1", "adminPass", 2, "staff@example.com", new ArrayList<>());
        customer.displayInfo();
        staff.displayInfo();

        /* factory for product
        ProductFactory productFactory = new ProductFactory() {
            @Override
            public Product createProduct(int productId, String productName, double price, int quantity) {
                return new Product(productId, productName, price, quantity);
            }
        };

        ProductFactory deviceFactory = new DeviceFactory();
        Product device = deviceFactory.createProduct(1, "Laptop", 1000.0, 10);

        ProductFactory domesticFactory = new DomesticFactory();
        Product domestic = domesticFactory.createProduct(2, "Smart Fridge", 500.0, 5);

        System.out.println(device.getProductName() + ' ' + device.getProductId() + ' ' + device.getProductQuantity() + ' ' + device.getProductPrice()); */

    }
}