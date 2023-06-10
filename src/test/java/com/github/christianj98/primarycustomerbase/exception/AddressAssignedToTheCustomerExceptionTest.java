package com.github.christianj98.primarycustomerbase.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for {@link AddressAssignedToTheCustomerException}
 */
public class AddressAssignedToTheCustomerExceptionTest {
    @Test
    public void shouldCreateCorrectExceptionMessage() {
        final String expectedMessage = "Address cannot be deleted because is already assigned to the customer";
        AddressAssignedToTheCustomerException exception = assertThrows(
                AddressAssignedToTheCustomerException.class,
                () -> {
                    throw new AddressAssignedToTheCustomerException(expectedMessage);
                });

        final String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}
