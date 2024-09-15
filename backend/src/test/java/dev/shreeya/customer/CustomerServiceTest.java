package dev.shreeya.customer;

import dev.shreeya.execption.DuplicateResourceException;
import dev.shreeya.execption.RequestValidationException;
import dev.shreeya.execption.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDAO customerDAO;
    private CustomerService underTest;

    @BeforeEach
    void setup() {
        // MockitoAnnotations.openMocks(this);
        underTest = new CustomerService(customerDAO);
    }


    @Test
    void getAllCustomers() {


        //When
        underTest.getAllCustomers();

        //Then
        verify(customerDAO).selectAllCustomer();
    }

    @Test
    void canGetCustomer() {
        //Given
        long id = 20;
        Customer customer = new Customer(
                id, "Shreeya", "shreeya@gmail.com", 23,
                Gender.MALE);
        Mockito.when(customerDAO.selectCustomerByID(id)).
                thenReturn(Optional.of(customer));

        //When
        Customer actual = underTest.getCustomer(20L);

        //Then
        assertThat(actual).isEqualTo(customer);
    }


    @Test
    void willThrowWhenGetCustomerReturnEmptyOptional() {
        //Given
        long id = 20;

        Mockito.when(customerDAO.selectCustomerByID(id)).
                thenReturn(Optional.empty());

        //When
        //Then
        assertThatThrownBy(() -> underTest.getCustomer(id)).
                isInstanceOf(ResourceNotFoundException.class).
                hasMessage("Customer with id [%s] not found"
                        .formatted(id));
    }

    @Test
    void addCustomer() {
        //Given
        String email = "shreeya@gmail.com";
        Mockito.when(customerDAO.existsCustomerWithEmail(email)).
                thenReturn(false);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Shreeya", email, 23 , Gender.FEMALE
        );

        //When
        underTest.addCustomer(request);
        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).insertCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getName()).isEqualTo(request.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(request.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(request.email());
    }


    @Test
    void willThrowWhenEmailExistsWhileAddingCustomer() {
        //Given
        String email = "shreeya@gmail.com";
        Mockito.when(customerDAO.existsCustomerWithEmail(email)).
                thenReturn(true);
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Shreeya", email, 23 , Gender.FEMALE
        );

        //When
        assertThatThrownBy(() -> underTest.addCustomer(request)).
                isInstanceOf(DuplicateResourceException.class).
                hasMessage("email already taken");
        //Then

        verify(customerDAO, never()).insertCustomer(any());

    }


    @Test
    void deleteCustomerById() {
        //Given
        Long id= 10L;
        Mockito.when(customerDAO.existsCustomerWithId(id)).
                thenReturn(true);



        //When
        underTest.deleteCustomerById(id);

        //Then
        verify(customerDAO).deleteCustomerById(id);
    }

    @Test
    void willThrowExceptionWhenDeleteCustomerByIdDoesNotExists() {
        //Given
        Long id=10L;
        Mockito.when(customerDAO.existsCustomerWithId(id)).
                thenReturn(false);

        //When
        assertThatThrownBy(() -> underTest.deleteCustomerById(id)).
                isInstanceOf(ResourceNotFoundException.class).
                hasMessage("Customer with id [%s] not found".formatted(id));
        //Then

        verify(customerDAO, never()).deleteCustomerById(id);
    }




    @Test
    void willUpdateCustomerByAllProperties() {
        //Given
        long id = 20;
        Customer customer = new Customer(
                id, "Shreeya", "shreeya@gmail.com", 23,
                Gender.MALE);
        Mockito.when(customerDAO.selectCustomerByID(id)).
                thenReturn(Optional.of(customer));

        String newEmail="shreeya29@gmail.com";
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest("Shree", newEmail, 25);

        Mockito.when(customerDAO.existsCustomerWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id,customerUpdateRequest);


        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerUpdateRequest.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerUpdateRequest.email());

    }

    @Test
    void willUpdateCustomerByOnlyName() {
        //Given
        long id = 20L;
        Customer customer = new Customer(
                id, "Shreeya", "shreeya@gmail.com", 23,
                Gender.MALE);
        Mockito.when(customerDAO.selectCustomerByID(id)).
                thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest("Shree", null, null);


        //When
        underTest.updateCustomer(id,customerUpdateRequest);


        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(customerUpdateRequest.name());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());

    }

    @Test
    void willUpdateCustomerByOnlyEmail() {
        //Given
        long id = 20L;
        Customer customer = new Customer(
                id, "Shreeya", "shreeya@gmail.com", 23,
                Gender.MALE);
        String newEmail="shree@gmail.com";
        Mockito.when(customerDAO.selectCustomerByID(id)).
                thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(null, newEmail, null);
        Mockito.when(customerDAO.existsCustomerWithEmail(newEmail)).thenReturn(false);

        //When
        underTest.updateCustomer(id,customerUpdateRequest);


        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(customer.getAge());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerUpdateRequest.email());

    }

    @Test
    void willUpdateCustomerByOnlyAge() {
        //Given
        long id = 20L;
        Customer customer = new Customer(
                id, "Shreeya", "shreeya@gmail.com", 23,
                Gender.MALE);
        Mockito.when(customerDAO.selectCustomerByID(id)).
                thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(null, null, 30);

        //When
        underTest.updateCustomer(id,customerUpdateRequest);


        //Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(
                Customer.class
        );
        verify(customerDAO).updateCustomer(customerArgumentCaptor.capture());
        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
        assertThat(capturedCustomer.getAge()).isEqualTo(customerUpdateRequest.age());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());

    }

    @Test
    void willThrowExceptionEmailAlreadyExistsWhileUpdateCustomer() {
        //Given
        long id = 20L;
        Customer customer = new Customer(
                id, "Shreeya", "shreeya@gmail.com", 23,
                Gender.MALE);
        String newEmail="shree@gmail.com";
        Mockito.when(customerDAO.selectCustomerByID(id)).
                thenReturn(Optional.of(customer));

        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(null, newEmail, null);
        Mockito.when(customerDAO.existsCustomerWithEmail(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(() -> underTest.updateCustomer(id, customerUpdateRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email is already taken");

        // Then
        verify(customerDAO, never()).updateCustomer(any());


    }

    @Test
    void willThrowExceptionWhenNoChangesWhileUpdateCustomer() {
        //Given
        long id = 20;
        Customer customer = new Customer(
                id, "Shreeya", "shreeya@gmail.com", 23,
                Gender.MALE);
        Mockito.when(customerDAO.selectCustomerByID(id)).
                thenReturn(Optional.of(customer));
        CustomerUpdateRequest customerUpdateRequest = new CustomerUpdateRequest(customer.getName(), customer.getEmail(), customer.getAge());


        //When
        assertThatThrownBy(() -> underTest.updateCustomer(id, customerUpdateRequest))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found");


        //Then
        verify(customerDAO, never()).updateCustomer(any());


    }
}