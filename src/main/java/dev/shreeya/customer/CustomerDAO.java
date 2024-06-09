package dev.shreeya.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDAO {
    List<Customer> selectAllCustomer();

    Optional<Customer> selectCustomerByID(Long id);

    void insertCustomer(Customer customer);

    boolean existsCustomerWithEmail(String email);

    boolean existsCustomerWithId(Long id);

    void deleteCustomerById(Long customerId);

    void updateCustomer(Customer update);

}
