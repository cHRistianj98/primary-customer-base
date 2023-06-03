package com.github.christianj98.primarycustomerbase.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ResourceAlreadyExistsExceptionTest {
    @Test
    public void shouldCreateCorrectExceptionMessage() {
        final String expectedMessage = "Resource already exists";
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> {
                    throw new ResourceAlreadyExistsException(expectedMessage);
                });

        final String actualMessage = exception.getMessage();
        assertThat(actualMessage).isEqualTo(expectedMessage);
    }
}
