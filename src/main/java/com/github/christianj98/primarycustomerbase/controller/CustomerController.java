package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.mapper.CustomerMapperService;
import com.github.christianj98.primarycustomerbase.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "customers")
@RequiredArgsConstructor
@Api(tags = "Customer Controller")
@Slf4j
public class CustomerController {
    private final CustomerService customerService;
    private final CustomerMapperService customerMapperService;

    @GetMapping
    @ApiOperation("Find all customers")
    public ResponseEntity<List<CustomerDto>> findAllCustomers() {
        return ResponseEntity.ok(customerMapperService.mapFrom(customerService.findAll()));
    }

    @PostMapping
    @ApiOperation("Create customer")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody @Valid CustomerDto customerDto) {
        Customer createdCustomer = customerService.save(customerDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCustomer.getId())
                .toUri();
        return ResponseEntity.created(location).body(customerMapperService.mapFrom(createdCustomer));
    }

    @GetMapping("/{id}")
    @ApiOperation("Find customer with specific id")
    public ResponseEntity<CustomerDto> findById(@PathVariable final int id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PutMapping("/{id}")
    @ApiOperation("Update customer with specific id")
    public ResponseEntity<CustomerDto> updateCustomer(@RequestBody @Valid CustomerDto customerDto,
                                                      @PathVariable int id) {
        return ResponseEntity.ok(customerService.update(customerDto, id));
    }

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
}
