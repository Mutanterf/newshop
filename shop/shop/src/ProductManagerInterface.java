import java.util.List;
public interface ProductManagerInterface {
    void displayProducts();
    void createProduct(Product product);
    Product getProductById(int productId);
    void deleteProduct(int productId);

    void addToCart(int customerId, int productId, int quantity);
    int removeFromCart(int customerId, int productId);

}
