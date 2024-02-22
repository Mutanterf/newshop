public interface AuthorizationInteface {
    public boolean isStaff(int customer_id);
    public User login(String username, String password);

    public Customer registerCustomer(Customer customer);

    public Staff registerStaff(Staff staff);
}
