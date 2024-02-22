public interface ShoppingCartInterface {
    private void calculateTotal(Double discount) {};
    private int indexOfProductInCart(int product_id) {return 0;};

    public  void removeFromCart(int product_id);
    public  void addProduct(Product product, int quantity);
    public  void displayCart(Double discount);
}