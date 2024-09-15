package dev.shreeya;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import dev.shreeya.customer.Customer;
import dev.shreeya.customer.CustomerRepository;
import dev.shreeya.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        // CustomerService customerService= new CustomerService(new CustomerListDataAccessService());
        // CustomerController customerControllers = new CustomerController(customerService);
        //Should never do like this



    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository) {
        return args -> {
            var faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            int age = random.nextInt(18, 75);
            Gender gender= age%2==0?Gender.MALE:Gender.FEMALE;
            Customer customer = new Customer(

                    firstName + " " + lastName,
                    firstName.toLowerCase() + random.nextInt(20, 45) + "." + lastName.toLowerCase() + "@gmail.com",
                    age,
                    gender);

            customerRepository.save(customer);
        };

    }


}
