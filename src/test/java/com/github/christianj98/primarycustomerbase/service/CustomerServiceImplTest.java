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

import javax.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.CUSTOMER_ALREADY_EXIST_ERROR;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;
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
        customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);
        customer = createCustomer(FIRST_NAME, LAST_NAME);
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
    @DisplayName("Find all customers from the table")
    public void findAll_UseRepository() {
        // given
        when(customerRepository.findAll()).thenReturn(Collections.singletonList(customer));

        // when
        final List<Customer> customers = customerService.findAll();

        // then
        assertThat(customers).containsOnly(customer);
        verify(customerRepository).findAll();
    }

    @Test
    @DisplayName("Find one existing customer by id")
    public void findById_CustomerFound() {
        // given
        int id = 1;

        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
        when(customerMapperService.mapFrom(any(Customer.class))).thenReturn(customerDto);

        // when
        final CustomerDto foundCustomerDto = customerService.findById(id);

        // then
        assertThat(foundCustomerDto.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(foundCustomerDto.getLastName()).isEqualTo(LAST_NAME);
    }

    @Test
    @DisplayName("Find one customer but customer does not exist")
    public void findById_CustomerNotFound() {
        // given
        int id = 2137;

        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> customerService.findById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(Integer.toString(id));
    }

    @Test
    @DisplayName("Update one customer but customer does not exist")
    public void update_customerNotFound() {
        // given
        int id = 2137;

        when(customerRepository.getReferenceById(id)).thenThrow(EntityNotFoundException.class);

        // when + then
        assertThatThrownBy(() -> customerService.update(customerDto, id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Update one customer successfully")
    public void update_customerUpdated() {
        // given
        customerDto.setFirstName("Andrzej");
        customerDto.setLastName("Nowak");
        int id = 1;

        when(customerRepository.getReferenceById(id)).thenReturn(customer);
        when(customerMapperService.mapFrom(any(Customer.class))).thenReturn(customerDto);

        // when
        final CustomerDto updatedCustomerDto = customerService.update(customerDto, id);

        // then
        assertThat(updatedCustomerDto.getFirstName()).isEqualTo(customerDto.getFirstName());
        assertThat(updatedCustomerDto.getLastName()).isEqualTo(customerDto.getLastName());
    }

    @Test
    @DisplayName("Delete one customer with given id")
    public void delete_customerDeleted() {
        // given
        int id = 1;

        when(customerRepository.existsById(id)).thenReturn(true);

        // when
        customerService.delete(id);

        // then
        verify(customerRepository).deleteById(id);
    }

    @Test
    @DisplayName("Delete one customer with given id but entity not found")
    public void delete_customerNotFound() {
        // given
        int id = 1;

        when(customerRepository.existsById(id)).thenReturn(false);

        // when + then
        assertThatThrownBy(() -> customerService.delete(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(Integer.toString(id));
    }
}
