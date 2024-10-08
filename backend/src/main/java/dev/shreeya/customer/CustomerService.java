package dev.shreeya.customer;

import dev.shreeya.execption.DuplicateResourceException;
import dev.shreeya.execption.RequestValidationException;
import dev.shreeya.execption.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService(@Qualifier("jdbc") CustomerDAO customerDAO) {
        this.customerDAO = customerDAO;
    }

    public List<Customer> getAllCustomers() {

        return customerDAO.selectAllCustomer();
    }

    public Customer getCustomer(Long id) {
        //   return customerDAO.selectCustomerByID(Long.valueOf(id)).orElseThrow
        //          (()->new ResourceNotFoundException("Customer with id [%s] not found".formatted(id)));
        return customerDAO.selectCustomerByID(id).orElseThrow(() ->
                new ResourceNotFoundException("Customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {

        //check if email exists
        String email = customerRegistrationRequest.email();
        if (customerDAO.existsCustomerWithEmail(email)) {
            throw new DuplicateResourceException(
                    "email already taken"
            );

        }
        //add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age(),
                customerRegistrationRequest.gender());
        customerDAO.insertCustomer(customer);
    }

    public void deleteCustomerById(Long customerId) {
        if (!customerDAO.existsCustomerWithId(customerId)) {
            throw new ResourceNotFoundException("Customer with id [%s] not found".formatted(customerId));

        }

        customerDAO.deleteCustomerById(customerId);
    }

    public void updateCustomer(Long customerId,CustomerUpdateRequest customerUpdateRequest) {

        Customer customer= getCustomer(customerId);
        boolean changes=false;

        if(customerUpdateRequest.name() != null && !customerUpdateRequest.name().equals(customer.getName())) {
            customer.setName(customerUpdateRequest.name());
            changes=true;
        }

        if(customerUpdateRequest.age() != null && !customerUpdateRequest.age().equals(customer.getAge())) {
            customer.setAge(customerUpdateRequest.age());
            changes=true;
        }

        if(customerUpdateRequest.email() != null && !customerUpdateRequest.email().equals(customer.getEmail())) {
            if(customerDAO.existsCustomerWithEmail(customerUpdateRequest.email())) {
                throw new DuplicateResourceException("Email is" +
                        " already taken");
            }

            customer.setEmail(customerUpdateRequest.email());
            changes=true;
        }

        if(!changes) {
            throw new RequestValidationException("No data changes found");

        }

        customerDAO.updateCustomer(customer);
    }


}
