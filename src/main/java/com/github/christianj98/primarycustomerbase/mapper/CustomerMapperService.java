package com.github.christianj98.primarycustomerbase.mapper;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerMapperService {
    private final AddressMapperService addressMapperService;

    public Customer mapFrom(CustomerDto customerDto) {
        Customer customer = new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        return customer;
    }

    public List<CustomerDto> mapFrom(@NotNull List<Customer> customers) {
        return customers.stream()
                .map(this::mapFrom)
                .collect(Collectors.toList());
    }

    public CustomerDto mapFrom(Customer customer) {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setAddressDto(addressMapperService.mapFrom(customer.getAddress()));
        return customerDto;
    }
}
