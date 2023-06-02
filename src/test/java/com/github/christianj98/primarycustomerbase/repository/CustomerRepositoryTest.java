package com.github.christianj98.primarycustomerbase.repository;

import com.github.christianj98.primarycustomerbase.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CustomerRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    public void whenFindAll_ThenReturnAllUsers() {
        // given
        Customer customer = createCustomer(FIRST_NAME, LAST_NAME);
        entityManager.merge(customer);

        // when
        final List<Customer> customers = customerRepository.findAll();

        // then
        assertThat(customers).hasSize(1);
        assertThat(customers).containsOnly(customer);
    }

}
