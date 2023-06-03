package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class with integration tests for {@link CustomerController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class CustomerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerDto customerDto;

    private Customer customer;

    @BeforeEach
    public void init() {
        customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);
        customer = createCustomer(FIRST_NAME, LAST_NAME);
    }

    @Test
    @DisplayName("Create customer - integration test")
    public void shouldCreateCustomer() throws Exception {
        // given
        final String expectedLocation = "http://localhost/customers/" + customer.getId();

        // when + then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(customerDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(customerDto.getLastName()))
                .andExpect(header().string("Location", expectedLocation));
    }

    @Test
    @DisplayName("Try to create the same customer what end as a conflict")
    public void createCustomer_expectResourceNotFoundException() throws Exception {
        // given
        customer = customerRepository.save(customer);

        // when + then
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDto)))
                .andExpect(status().isConflict())
                .andExpect(header().doesNotExist("Location"));
    }
}
