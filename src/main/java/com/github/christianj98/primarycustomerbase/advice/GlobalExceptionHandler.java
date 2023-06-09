package com.github.christianj98.primarycustomerbase.advice;

import com.github.christianj98.primarycustomerbase.exception.AddressAssignedToTheCustomerException;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Void> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(AddressAssignedToTheCustomerException.class)
    public ResponseEntity<String> handleAddressAssignedToTheCustomerException(AddressAssignedToTheCustomerException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
        log.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }
}
