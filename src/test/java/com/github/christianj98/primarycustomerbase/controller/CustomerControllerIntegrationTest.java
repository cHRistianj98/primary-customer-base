package com.github.christianj98.primarycustomerbase.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.mapper.CustomerMapperService;
import com.github.christianj98.primarycustomerbase.service.CustomerService;
import com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
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

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerMapperService customerMapperService;

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
        when(customerService.save(any(CustomerDto.class))).thenReturn(customer);
        when(customerMapperService.mapFrom(any(Customer.class))).thenReturn(customerDto);

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

    private String asJsonString(Object object) {

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
