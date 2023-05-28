package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.mapper.CustomerMapperService;
import com.github.christianj98.primarycustomerbase.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapperService customerMapperService;

    public Customer save(CustomerDto customerDto) {
        if (customerRepository.existsByFirstNameAndLastName(customerDto.getFirstName(),
                customerDto.getLastName())) {
            final String errorMessage = "Customer with given first name %s and last name %s already exist";
            throw new ResourceAlreadyExistsException(
                    String.format(errorMessage, customerDto.getFirstName(), customerDto.getLastName()));
        }

        return customerRepository.save(customerMapperService.mapFrom(customerDto));
    }


}
