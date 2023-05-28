package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.mapper.CustomerMapperService;
import com.github.christianj98.primarycustomerbase.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.CUSTOMER_ALREADY_EXIST_ERROR;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapperService customerMapperService;

    public Customer save(CustomerDto customerDto) {
        if (customerRepository.existsByFirstNameAndLastName(customerDto.getFirstName(), customerDto.getLastName())) {
            throw new ResourceAlreadyExistsException(
                    String.format(CUSTOMER_ALREADY_EXIST_ERROR.getMessage(),
                            customerDto.getFirstName(),
                            customerDto.getLastName()));
        }

        return customerRepository.save(customerMapperService.mapFrom(customerDto));
    }


}
