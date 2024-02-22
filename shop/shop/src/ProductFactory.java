interface ProductFactory {
    Product createProduct(int productId, String productName, double price, int quantity);
}

class DeviceFactory implements ProductFactory {
    @Override
    public Product createProduct(int productId, String productName, double price, int quantity) {
        return new Device(productId, productName, price, quantity, 0.0, "");
    }
}

class DomesticFactory implements ProductFactory {
    @Override
    public Product createProduct(int productId, String productName, double price, int quantity) {
        return new Domestic(productId, productName, price, quantity, false);
    }
}