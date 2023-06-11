package com.github.christianj98.primarycustomerbase.advice;

import com.github.christianj98.primarycustomerbase.exception.AddressAssignedToTheCustomerException;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.http.HttpStatus;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.ADDRESS_ASSIGNED_TO_THR_CUSTOMER_ERROR;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link GlobalExceptionHandler}
 */
@ExtendWith({
        MockitoExtension.class,
        OutputCaptureExtension.class
})
public class GlobalExceptionHandlerTest {
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    public void handleResourceAlreadyExistsException_returnsConflictStatus(CapturedOutput output) {
        // given
        final String errorMessage = "Resource already exist";
        final ResourceAlreadyExistsException exception = new ResourceAlreadyExistsException(errorMessage);

        // when
        var response = globalExceptionHandler.handleResourceAlreadyExistsException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(output).contains(errorMessage);
    }

    @Test
    public void handleEntityNotFoundException_returnsNotFoundStatus(CapturedOutput output) {
        // given
        final String errorMessage = "Entity not found";
        final EntityNotFoundException exception = new EntityNotFoundException(errorMessage);

        // when
        var response = globalExceptionHandler.handleEntityNotFoundException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(output).contains(errorMessage);
    }

    @Test
    public void handleAddressAssignedToTheCustomerException_returnsConflictStatus(CapturedOutput output) {
        // given
        final String errorMessage = ADDRESS_ASSIGNED_TO_THR_CUSTOMER_ERROR.getMessage();
        final AddressAssignedToTheCustomerException exception =
                new AddressAssignedToTheCustomerException(errorMessage);

        // when
        var response = globalExceptionHandler.handleAddressAssignedToTheCustomerException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(output).contains(errorMessage);
    }

    @Test
    public void handleConstraintViolationException_returnsBadRequestStatus(CapturedOutput output) {
        // given
        final ConstraintViolationException exception = new ConstraintViolationException(null);

        // when
        var response = globalExceptionHandler.handleConstraintViolationException(exception);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(output).contains("null");
    }
}
