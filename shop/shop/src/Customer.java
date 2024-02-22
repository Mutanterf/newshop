import java.util.ArrayList;
import java.util.List;

abstract class User implements InfoDisplay, ProductManagement{
    private String username;
    private String password;
    private int userId;
    private String email;

    public User(String username, String password, int userId, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}


class Customer extends User {
    private final ShoppingCart shoppingCart;

    public Customer(String username, String password, int userId, String email, List<Product> cart) {
        super(username, password, userId, email);
        this.shoppingCart = new ShoppingCart(cart);
    }

    @Override
    public void displayInfo() {
        System.out.println("Customer Information:");
        System.out.println("Username: " + getUsername());
        this.shoppingCart.displayCart();
    }

    @Override
    public Product getProductById(int productId) {
        return null;
    }
}


class Staff extends User implements ProductCreator {
    private final ShoppingCart shoppingCart;

    private final double discount = 10;

    public Staff(String username, String password, int userId, String email, List<Product> cart) {
        super(username, password, userId, email);
        this.shoppingCart = new ShoppingCart(cart);
    }

    public double getDiscount() {
        return discount;
    }


    public void createDevice(int productId, String productName, double price, int quantity, double screenSize, String os) {
        Device device = new Device(productId, productName, price, quantity, screenSize, os);
    }

    public void createDomestic(int productId, String productName, double price, int quantity, boolean canConnectToWiFi) {
        Domestic domestic = new Domestic(productId, productName, price, quantity, canConnectToWiFi);
    }

    @Override
    public void displayInfo() {
        System.out.println("Staff Information:");
        System.out.println("Username: " + getUsername());
        this.shoppingCart.displayCart();
    }

    @Override
    public Product getProductById(int productId) {
        return null;
    }
}



/*abstract class User {
    protected final App app = new App();
    private String username;
    private String password;
    private int userId;
    private String email;
    protected final ShoppingCart shoppingCart;


    public User(String username, String password, int userId, String email) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.shoppingCart = new ShoppingCart(userId);
    }

    public abstract void displayInfo();


    public int getUserId() {
        return userId;
    }

    public void setCustomerId(int userId) {
        this.userId = userId;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

//    public void displayCart() {}

//    public void decreaseFromCart(int product_id, int quantity){
//        app.decreaseProductQuantity(this.getUserId(), product_id, quantity);
//        this.shoppingCart.decreaseProductQuantity(product_id, quantity);
//
//    }

//    public void addToCart(int product_id, int quantity){
//        Product product = app.getProductById(product_id);
//        this.shoppingCart.addProduct(product, quantity);
//    }

//    public void removeFromCart(int product_id) {
//        shoppingCart.removeFromCart(product_id);
//    }

//    public void calculateTotal() {
//        this.shoppingCart.calculateTotal(null);
//    }

}

class Customer extends User {
    public Customer(String username, String password, int userId, String email) {
        super(username, password, userId, email);
    }

    @Override
    public void displayInfo() {
        System.out.println("Customer Information:");
        System.out.println("Username: " + getUsername());
    }

//    @Override
//    public void displayCart() {
//        this.shoppingCart.displayCart(null);
//    }
}


class Staff extends User {

    private double discount;

    public Staff(String username, String password, int userId, String email, double discount) {
        super(username, password, userId, email);
        this.discount = discount;
    }

//    @Override
//    public void calculateTotal() {
//        this.shoppingCart.calculateTotal(this.discount);
//    }

    @Override
    public void displayInfo() {
        System.out.println("Staff Information:");
        System.out.println("Username: " + getUsername() + " " + "Discount of staff: " + getDiscount());
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

//    public void createDevice(int productId, String productName, double price, int quantity, double screenSize, String os) {
//        Device device = new Device(productId, productName, price, quantity, screenSize, os);
//        app.createProduct(device);
//    }
//
//    public void createDomestic(int productId, String productName, double price, int quantity, boolean canConnectToWiFi) {
//        Domestic domestic = new Domestic(productId, productName, price, quantity, canConnectToWiFi);
//        app.createProduct(domestic);
//    }

//    @Override
//    public void displayCart() {
//        this.shoppingCart.displayCart(this.discount);
//    }
} */