package dev.shreeya.customer;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("List")
public class CustomerListDataAccessService implements CustomerDAO{


    private static List<Customer> customers;
    static {
        customers=new ArrayList<>();
        Customer shreyas=new Customer(
                7L,
                "Shreyas",
                "shreyas@gmail.com",
                20
        );
        Customer shreeya = new Customer(
                45L,
                "Shreeya",
                "shreeya@gmail.com",
                20

        );
        customers.add(shreeya);
        customers.add(shreyas);


    }

    public List<Customer> selectAllCustomer() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerByID(Long id) {
        return Optional.empty();
    }

    @Override
    public void insertCustomer(Customer customer) {
         customers.add(customer);
    }

    @Override
    public boolean existsCustomerWithEmail(String email) {
        return customers.stream().anyMatch(c->c.getEmail().equals(email));
    }

    @Override
    public boolean existsCustomerWithId(Long id) {
        return customers.stream().anyMatch(c->c.getId().equals(id));
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        customers.stream().filter(c -> c.getId().equals(customerId)).findFirst().ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Customer customer) {
        customers.add(customer);
    }


}



