import java.util.List;
interface UserFactory {
    User createUser(String username, String password, int userId, String email, List<Product> cart);
}

class CustomerFactory implements UserFactory {
    @Override
    public User createUser(String username, String password, int userId, String email, List<Product> cart) {
        return new Customer(username, password, userId, email, cart);
    }
}

class StaffFactory implements UserFactory {
    @Override
    public User createUser(String username, String password, int userId, String email, List<Product> cart) {
        return new Staff(username, password, userId, email, cart);
    }
}
