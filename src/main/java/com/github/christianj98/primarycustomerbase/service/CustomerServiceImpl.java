package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.mapper.AddressMapperService;
import com.github.christianj98.primarycustomerbase.mapper.CustomerMapperService;
import com.github.christianj98.primarycustomerbase.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.CUSTOMER_ALREADY_EXIST_ERROR;
import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapperService customerMapperService;
    private final AddressMapperService addressMapperService;

    public Customer save(CustomerDto customerDto) {
        if (customerRepository.existsByFirstNameAndLastName(customerDto.getFirstName(), customerDto.getLastName())) {
            throw new ResourceAlreadyExistsException(
                    String.format(CUSTOMER_ALREADY_EXIST_ERROR.getMessage(),
                            customerDto.getFirstName(),
                            customerDto.getLastName()));
        }
        final Customer customer = customerMapperService.mapFrom(customerDto);
        customer.setAddress(addressMapperService.mapFrom(customerDto.getAddressDto()));
        return customerRepository.save(customer);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public CustomerDto findById(final int id) {
        return customerRepository.findById(id)
                .map(customerMapperService::mapFrom)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Customer not found with given id: %s", id)));
    }

    public CustomerDto update(final CustomerDto customerDto, final int id) {
        Customer customer = customerRepository.getReferenceById(id);
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        ofNullable(customer.getAddress())
                .map(relatedAddress -> mapAddress(customerDto, relatedAddress))
                .orElseGet(() -> addressMapperService.mapFrom(customerDto.getAddressDto()));
        return customerMapperService.mapFrom(customer);
    }

    private Address mapAddress(final CustomerDto customerDto, final Address relatedAddress) {
        relatedAddress.setStreet(customerDto.getAddressDto().getStreet());
        relatedAddress.setCity(customerDto.getAddressDto().getCity());
        return relatedAddress;
    }

    public void delete(final int id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException(String.format("Customer not found with given id: %s", id));
        }
        customerRepository.deleteById(id);
    }
}
