package dev.shreeya.journey;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import dev.shreeya.customer.Customer;
import dev.shreeya.customer.CustomerRegistrationRequest;
import dev.shreeya.customer.CustomerUpdateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final Random random = new Random();

    private static final  String CUSTOMER_URI = "/api/v1/customers";


    @Test
    void canRegisterCustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerNAme= faker.name();
        String firstName = fakerNAme.firstName();
        String email= fakerNAme.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        int age= random.nextInt(1,100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(firstName, email, age);
        //send post request


        webTestClient.post().
                uri(CUSTOMER_URI)
        .accept(MediaType.APPLICATION_JSON)
        .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();

        //make sure the customer is present
        Customer expectedCustomer=new Customer(
                firstName,
                email,
                age
        );

        assertThat(allCustomers)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expectedCustomer);


        //get the customer by id

        Long id=allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                        .map(Customer::getId)
                                .findFirst()
                                        .orElseThrow();

        expectedCustomer.setId(id);
        webTestClient.get()
                .uri(CUSTOMER_URI+ "/{id}" ,id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Customer>() {})
                .isEqualTo(expectedCustomer);

    }

    @Test
    void canDeleteCustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerNAme= faker.name();
        String firstName = fakerNAme.firstName();
        String email= fakerNAme.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        int age= random.nextInt(1,100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(firstName, email, age);
        //send post request


        webTestClient.post().
                uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();



        //get the customer by id

        Long id=allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


       //delete the customer
        webTestClient.delete()
                .uri(CUSTOMER_URI+ "/{id}" ,id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();


        webTestClient.get()
                .uri(CUSTOMER_URI+ "/{id}" ,id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void canUpdateCustomer() {
        //create registration request
        Faker faker = new Faker();
        Name fakerNAme= faker.name();
        String firstName = fakerNAme.firstName();
        String email= fakerNAme.lastName() + "-" + UUID.randomUUID() + "@gmail.com";
        int age= random.nextInt(1,100);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(firstName, email, age);
        //send post request


        webTestClient.post().
                uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        //get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri(CUSTOMER_URI)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {
                })
                .returnResult()
                .getResponseBody();



        //get the customer by id

        Long id=allCustomers.stream()
                .filter(customer -> customer.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();


        //update customer
        String name = "Shreeya";
        CustomerUpdateRequest updateRequest = new CustomerUpdateRequest(
                name, null ,null
        );
        webTestClient.put()
                .uri(CUSTOMER_URI+ "/{id}" ,id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(updateRequest), CustomerUpdateRequest.class)
                .exchange()
                .expectStatus()
                .isOk();


        Customer updatedCustomer = webTestClient.get()
                .uri(CUSTOMER_URI + "/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult()
                .getResponseBody();

        Customer expectedCustomer = new Customer(
                id,
                name,
                email,
                age
        );

        assertThat(updatedCustomer).isEqualTo(expectedCustomer);
    }
}
