package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.mapper.CustomerMapperService;
import com.github.christianj98.primarycustomerbase.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.CUSTOMER_ALREADY_EXIST_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link CustomerServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
public class CustomerServiceImplTest {
    private static final String FIRST_NAME = "Jan";
    private static final String LAST_NAME = "Kowalski";

    @InjectMocks
    private CustomerServiceImpl customerService;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapperService customerMapperService;

    private CustomerDto customerDto;

    private Customer customer;

    @BeforeEach
    public void init() {
        customerDto = createCustomerDto();
        customer = createCustomer();
    }
    
    @Test
    @DisplayName("check if exception will be thrown when customer exist")
    public void save_existingCustomerDto_ThrowsResourceAlreadyExistsException() {
        // given
        when(customerRepository.existsByFirstNameAndLastName(any(), any())).thenReturn(true);

        // when + then
        assertThatThrownBy(() -> customerService.save(customerDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage(String.format(CUSTOMER_ALREADY_EXIST_ERROR.getMessage(),
                        customerDto.getFirstName(),
                        customerDto.getLastName()));
    }

    @Test
    @DisplayName("try to save Customer")
    public void save_validCustomerDto_ReturnsCreatedCustomer() {
        // given
        when(customerRepository.existsByFirstNameAndLastName(any(), any())).thenReturn(false);
        when(customerMapperService.mapFrom(any(CustomerDto.class))).thenReturn(customer);
        when(customerRepository.save(any())).thenReturn(customer);

        // when
        final Customer createdCustomer = customerService.save(customerDto);

        // then
        assertThat(createdCustomer.getFirstName()).isEqualTo(customerDto.getFirstName());
        assertThat(createdCustomer.getLastName()).isEqualTo(customerDto.getLastName());
        verify(customerRepository).existsByFirstNameAndLastName(FIRST_NAME, LAST_NAME);
        verify(customerMapperService).mapFrom(customerDto);
        verify(customerRepository).save(customer);
    }

    @Test
    public void findAll_UseRepository() {
        // given
        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        // when
        final List<Customer> customers = customerService.findAll();

        // then
        assertThat(customers).containsOnly(customer);
        verify(customerRepository).findAll();
    }

    private CustomerDto createCustomerDto() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName(FIRST_NAME);
        customerDto.setLastName(LAST_NAME);
        return customerDto;
    }

    private Customer createCustomer() {
        Customer customer = new Customer();
        customer.setFirstName(FIRST_NAME);
        customer.setLastName(LAST_NAME);
        return customer;
    }
}
