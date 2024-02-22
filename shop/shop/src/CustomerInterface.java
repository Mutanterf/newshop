
interface InfoDisplay {
    void displayInfo();

}
interface ProductManagement {
    Product getProductById(int productId);


}

interface ProductCreator {
    void createDevice(int productId, String productName, double price, int quantity, double screenSize, String os);

    void createDomestic(int productId, String productName, double price, int quantity, boolean canConnectToWiFi);
}