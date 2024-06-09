package dev.shreeya.customer;

import org.springframework.data.jpa.repository.JpaRepository;

//manage entity for crud operation
public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    //way in which jpa will construct query
    boolean existsCustomersByEmail(String email);

    boolean existsCustomersById(Long id);

}
