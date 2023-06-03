package com.github.christianj98.primarycustomerbase.repository;

import com.github.christianj98.primarycustomerbase.entity.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;
    private Customer customer;

    @BeforeEach
    public void init() {
        customer = customerRepository.save(createCustomer(FIRST_NAME, LAST_NAME));
    }

    @Test
    public void whenFindAll_ThenReturnAllUsers() {
        // when
        final List<Customer> customers = customerRepository.findAll();

        // then
        assertThat(customers).hasSize(1);
        assertThat(customers).extracting(Customer::getFirstName).containsOnly(FIRST_NAME);
        assertThat(customers).extracting(Customer::getLastName).containsOnly(LAST_NAME);
    }

    @Test
    public void whenSaveCustomer_ThenReturnCustomer() {
        // when
        final Customer createdCustomer = customerRepository.save(customer);

        // then
        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(createdCustomer.getLastName()).isEqualTo(LAST_NAME);
    }

    @Test
    public void whenFindById_ThenReturnOptionalCustomer() {
        // when
        final Optional<Customer> foundCustomer = customerRepository.findById(customer.getId());

        // then
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(foundCustomer.get().getLastName()).isEqualTo(LAST_NAME);
    }

    @Test
    public void whenExistsById_ThenReturnTrue() {
        // when
        final boolean customerExists = customerRepository.existsById(customer.getId());

        // then
        assertThat(customerExists).isTrue();
    }

    @Test
    public void whenExistsByFirstNameAndLastName_ThenReturnTrue() {
        // when
        final boolean customerExists = customerRepository.existsByFirstNameAndLastName(FIRST_NAME, LAST_NAME);

        // then
        assertThat(customerExists).isTrue();
    }
}
