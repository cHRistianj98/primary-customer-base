package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.mapper.CustomerMapperService;
import com.github.christianj98.primarycustomerbase.service.CustomerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping(path = "customers")
@RequiredArgsConstructor
@Api(tags = "Customer Controller")
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
        URI location = fromCurrentRequest()
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

    @DeleteMapping("/{id}")
    @ApiOperation("Delete customer wit given id")
    public ResponseEntity<Void> deleteCustomer(@PathVariable int id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
