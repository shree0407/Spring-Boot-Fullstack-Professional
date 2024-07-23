package dev.shreeya.customer;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.mockito.Mockito.verify;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;


    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();

    }

    @Test
    void selectAllCustomer() {
        //When-->we call method underTest.selectAllCustomers, we want to make sure that customerRepository.findAll() --> gets invoked
        underTest.selectAllCustomer();

        //Then
        verify(customerRepository).findAll();
    }

    @Test
    void selectCustomerByID() {
        //Given
        long id = 1;

        //When
        underTest.selectCustomerByID(id);

        //Then
        Mockito.verify(customerRepository).findById(id);
    }

    @Test
    void insertCustomer() {
        //Given
        Faker faker = new Faker();// -> no need to use this everytime if it extends abstract TestContainer
        Customer customer = new Customer(faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                25);


        //When
        underTest.insertCustomer(customer);

        //Then
        Mockito.verify(customerRepository).save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        //Given
        Faker faker = new Faker();
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();


        //When
        underTest.existsCustomerWithEmail(email);

        //Then
        Mockito.verify(customerRepository).existsCustomersByEmail(email);
    }

    @Test
    void existsPersonWithId() {
        //Given
        long id = -1;
        //When
        underTest.existsCustomerWithId(id);
        //Then
        Mockito.verify(customerRepository).existsCustomersById(id);
    }

    @Test
    void deleteCustomerById() {
        //Given
        long customerId = -1;

        //When
        underTest.deleteCustomerById(customerId);

        //Then
        Mockito.verify(customerRepository).deleteById(customerId);
    }

    @Test
    void updateCustomer() {
        //Given
        Faker faker = new Faker();
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress(),
                67
        );

        // When
        underTest.updateCustomer(customer);

        // Then
        Mockito.verify(customerRepository).save(customer);
    }
}