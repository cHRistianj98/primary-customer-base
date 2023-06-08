package com.github.christianj98.primarycustomerbase.message;

import org.junit.jupiter.api.Test;

import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.ADDRESS_ALREADY_EXIST_ERROR;
import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.CUSTOMER_ALREADY_EXIST_ERROR;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static org.assertj.core.api.Assertions.assertThat;

public class ErrorMessagesTest {

    @Test
    public void customerExist_checkErrorMessage() {
        String expectedMessage = "Customer with given first name Jan and last name Kowalski already exist";
        String actualMessage = String.format(CUSTOMER_ALREADY_EXIST_ERROR.getMessage(), FIRST_NAME, LAST_NAME);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }

    @Test
    public void shouldGetMessage() {
        String expectedMessage = "Address with given street " + STREET + " and city " + CITY + " already exist";
        String actualMessage = String.format(ADDRESS_ALREADY_EXIST_ERROR.getMessage(), STREET, CITY);
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}
