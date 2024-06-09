package dev.shreeya.customer;

public record CustomerRegistrationRequest (
        String name,
        String email,
        Integer age
){


}
