public interface ProductInterface {
    public String getProductName();

    public double getProductPrice();

    public int getProductQuantity();
    public void setProductQuantity(int newQuantity);

    public void reduceQuantity(int newValue);
}
