package com.github.christianj98.primarycustomerbase.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;

public class CustomerTestUtils {
    public static final String FIRST_NAME = "Jan";
    public static final String LAST_NAME = "Kowalski";
    public static final String HOST = "localhost";
    public static final String CUSTOMERS_URI = "/customers";

    private CustomerTestUtils() {
        // private
    }

    public static CustomerDto createCustomerDto(final String firstName, final String lastName) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName(firstName);
        customerDto.setLastName(lastName);
        return customerDto;
    }

    public static Customer createCustomer(final String firstName, final String lastName) {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        return customer;
    }

    public static String asJsonString(Object object) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
