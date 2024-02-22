import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
class ShoppingCart implements ShoppingCartInterface {
    private List<Product> cart = new ArrayList<>();

    public ShoppingCart(List<Product> cart) {
        this.cart = cart;
    }

    private void calculateTotal() {
        if(cart.size() <= 0) {
            System.out.println("Yout cart is empty");
        }
        double total = 0.00;
        for(Product product: cart) {
            total += product.getProductPrice() * product.getProductQuantity();
        }
        System.out.println("Total of your cart: " + total);
    }
    private void calculateTotal(Double discount) {
        if(cart.size() <= 0) {
            System.out.println("Yout cart is empty");
        }
        double total = 0.00;
        for(Product product: cart) {
            total += product.getProductPrice() * product.getProductQuantity();
        }
        if(discount != null) {
            total *= 100 - discount;
            total /= 100;
        }
        System.out.println("Total of your cart: " + total);
    }

    private int indexOfProductInCart(int product_id) {
        for (int i = 0; i < this.cart.size(); i++) {
            if (this.cart.get(i).getProductId() == product_id) {
                return i;
            }
        }
        return -1;
    }

    public void removeFromCart(int product_id) {
        Iterator<Product> iterator = this.cart.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (product.getProductId() == product_id) {
                iterator.remove();
                return;
            }
        }
    }

    public void addProduct(Product product, int quantity) {
        try {
            int index = indexOfProductInCart(product.getProductId());
            if (index != -1) {
                Product cartProduct = cart.get(index);
                cartProduct.setProductQuantity(cartProduct.getProductQuantity() + quantity);
                System.out.println("Product quantity updated in the cart.");
            } else {
                Product newCartItem = new Product(product.getProductId(), product.getProductName(), product.getProductPrice(), quantity);
                cart.add(newCartItem);
                System.out.println("Product added to the cart.");

                product.reduceQuantity(quantity);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void displayCart() {
        System.out.println("Shopping Cart:");
        for (Product product : cart) {
            System.out.print("Product ID: " + product.getProductId());
            System.out.print(", Name: " + product.getProductName());
            System.out.print(", Quantity: " + product.getProductQuantity());
            System.out.print(", Price: " + product.getProductPrice() + "\n");
        }
        this.calculateTotal();
    }

    public void displayCart(Double discount) {
        System.out.println("Shopping Cart:");
        for (Product product : cart) {
            System.out.print("Product ID: " + product.getProductId());
            System.out.print(", Name: " + product.getProductName());
            System.out.print(", Quantity: " + product.getProductQuantity());
            System.out.print(", Price: " + product.getProductPrice() + "\n");
        }
        this.calculateTotal(discount);
    }
}





