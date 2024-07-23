package dev.shreeya.customer;

import dev.shreeya.AbstractTestcontainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


class CustomerJDBCDataAccessServiceTest extends AbstractTestcontainers {

    private CustomerJDBCDataAccessService underTest;
    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();

    @BeforeEach
    void setUp() {
        // Before every test we get fresh new object of CustomerJDBCDataAccessService
        underTest = new CustomerJDBCDataAccessService(
                getjdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomer() {
        //Given
        Customer customer = new Customer(
                faker.name().fullName(),
                faker.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                18
        );
        underTest.insertCustomer(customer);

        //When
        List<Customer> actual = underTest.selectAllCustomer();

        //Then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void selectCustomerByID() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.insertCustomer(customer);
        Long id = underTest.selectAllCustomer().stream().
                filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();

        //When
        Optional<Customer> actual = underTest.selectCustomerByID(id);

        //Then
        assertThat(actual).isPresent().hasValueSatisfying(
                c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                });
    }

    @Test
    void willReturnEmptyWhenSelectCustomerById() {
        //Given
        long id = -1;

        //When
        var actual = underTest.selectCustomerByID(id);

        //Then
        assertThat(actual).isEmpty();
    }

    @Test
    void insertCustomer() {
        //Given


        //When

        //Then
    }

    @Test
    void existsPersonWithEmail() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.insertCustomer(customer);

        //When
        boolean actual = underTest.existsCustomerWithEmail(email);
        //Then
        assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        //When
        boolean actual = underTest.existsCustomerWithEmail(email);


        //Then
        assertThat(actual).isFalse();

    }


    @Test
    void existsPersonWithId() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer().stream().
                filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();


        //When
         var actual=underTest.existsCustomerWithId(id);

        //Then
         assertThat(actual).isTrue();
    }

    @Test
    void existsPersonWithIdWillReturnFalseWhenIdNotPresent() {
        //Given
       Long id= (long) -1;

        //When
        var actual=underTest.existsCustomerWithId(id);

        //Then
        assertThat(actual).isFalse();
    }

    @Test
    void deleteCustomerById() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer().stream().
                filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();


        //When
        underTest.deleteCustomerById(id);

        //Then
        Optional<Customer> actual = underTest.selectCustomerByID(id);
        assertThat(actual).isNotPresent();

    }

    @Test
    void updateCustomerName() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer().stream().
                filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();

        var newName="Foo";

        //When
        Customer update=new Customer();
        update.setId(id);
        update.setName(newName);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerByID(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getEmail().equals(customer.getEmail()));
            assertThat(c.getAge().equals(customer.getAge()));
        });

    }

    @Test
    void updateCustomerEmail() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer().stream().
                filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();
        var newEmail=faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();


        //When

        Customer update=new Customer();
        update.setId(id);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerByID(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getName().equals(customer.getName()));
            assertThat(c.getEmail().equals(newEmail));
            assertThat(c.getAge().equals(customer.getAge()));
        });

    }

    @Test
    void updateCustomerAge() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer().stream().
                filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();
        var newAge= 20;


        //When

        Customer update=new Customer();
        update.setId(id);
        update.setAge(newAge);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerByID(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getName().equals(customer.getName()));
            assertThat(c.getEmail().equals(customer.getEmail()));
            assertThat(c.getAge().equals(newAge));
        });

    }

    @Test
    void updateCustomer() {
        //Given
        String email = faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                faker.name().fullName(),
                email,
                18
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer().stream().
                filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();
        var newName="Boo";
        var newEmail=faker.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        var newAge= 20;



        //When
        // We can have separate test for each property of customer
        Customer update=new Customer();
        update.setId(id);
        update.setAge(newAge);
        update.setName(newName);
        update.setEmail(newEmail);

        underTest.updateCustomer(update);

        //Then
        Optional<Customer> actual = underTest.selectCustomerByID(id);
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId().equals(id));
            assertThat(c.getName().equals(newName));
            assertThat(c.getEmail().equals(newEmail));
            assertThat(c.getAge().equals(newAge));
        });
    }
}