package com.github.christianj98.primarycustomerbase.utils;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddress;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;

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
        customerDto.setAddressDto(createAddressDto(STREET, CITY));
        return customerDto;
    }

    public static Customer createCustomer(final String firstName, final String lastName) {
        Customer customer = new Customer();
        customer.setId(1);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        customer.setAddress(createAddress(STREET, CITY));
        return customer;
    }

}
