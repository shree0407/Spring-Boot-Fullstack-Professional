package dev.shreeya.customer;

import dev.shreeya.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest extends AbstractTestcontainers {
    @Autowired
    private CustomerRepository underTest;

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() {
        underTest.deleteAll();
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existsCustomersByEmail() {
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.save(customer);

        //When
        var actual = underTest.existsCustomersByEmail(email);

        //Then
        assertThat(actual).isTrue();
    }
    @Test
    void existsCustomersByEmailFailsWhenEmailNotPresent() {
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        //When
        var actual = underTest.existsCustomersByEmail(email);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void existsCustomersById() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.save(customer);
        Long id = underTest.findAll().stream().
                filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();

        //When
        var actual = underTest.existsCustomersById(id);

        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsCustomersByIdFailsWhenIdNotPresent() {
        //Given
       Long id= (long) -1;

        //When
        var actual = underTest.existsCustomersById(id);

        //Then
        assertThat(actual).isFalse();
    }
}