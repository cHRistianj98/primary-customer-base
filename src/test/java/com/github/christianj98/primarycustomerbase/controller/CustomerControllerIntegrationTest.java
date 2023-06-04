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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.CUSTOMERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.asJsonString;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class with integration tests for {@link CustomerController}
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
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
        mockMvc.perform(post(CUSTOMERS_URI)
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
        mockMvc.perform(post(CUSTOMERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDto)))
                .andExpect(status().isConflict())
                .andExpect(header().doesNotExist("Location"));
    }

    @Test
    @DisplayName("Find all customers in DB")
    public void findAll_expectAllCustomersFromDb() throws Exception {
        // given
        customer = customerRepository.save(customer);

        // when + then
        mockMvc.perform(get(CUSTOMERS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$[0].lastName").value(LAST_NAME));
    }

    @Test
    @DisplayName("Find one customer by id")
    public void findById_expectOneCustomerFromDb() throws Exception {
        // given
        customer = customerRepository.save(customer);

        // when + then
        mockMvc.perform(get(CUSTOMERS_URI + "/{id}", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME));
    }

    @Test
    @DisplayName("Find one customer by id, but customer with given id was not found")
    public void findById_resourceNotFound() throws Exception {
        // given
        int id = 999;

        // when + then
        final String responseBody = mockMvc.perform(get(CUSTOMERS_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(responseBody).isEqualTo("Customer not found with given id: " + id);
    }

    @Test
    @DisplayName("Update one customer by id")
    public void update_customerUpdated() throws Exception {
        // given
        customer = customerRepository.save(customer);
        final CustomerDto customerDtoToUpdate = createCustomerDto("Andrzej", "Nowak");

        // when + then
        mockMvc.perform(put(CUSTOMERS_URI + "/{id}", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDtoToUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(customerDtoToUpdate.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(customerDtoToUpdate.getLastName()));
    }

    @Test
    @DisplayName("Update one customer but customer does not exist")
    public void update_customerNotFound() throws Exception {
        // given
        int id = 999;
        final CustomerDto customerDtoToUpdate = createCustomerDto("Andrzej", "Nowak");

        // when + then
        final String responseBody = mockMvc.perform(put(CUSTOMERS_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDtoToUpdate)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(responseBody).contains(Integer.toString(id));
    }

    @Test
    @DisplayName("Delete one customer by id but customer not found")
    public void deleteById_customerNotFound() throws Exception {
        // given
        int id = 999;

        // when + then
        final String responseBody = mockMvc.perform(delete(CUSTOMERS_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(responseBody).contains(Integer.toString(id));
    }

    @Test
    @DisplayName("Delete one customer by id")
    public void delete_customerDeleted() throws Exception {
        // given
        customer = customerRepository.save(customer);

        // when + then
        mockMvc.perform(delete(CUSTOMERS_URI + "/{id}", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
