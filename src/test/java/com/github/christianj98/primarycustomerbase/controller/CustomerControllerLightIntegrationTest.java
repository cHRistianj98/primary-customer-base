package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.mapper.CustomerMapperService;
import com.github.christianj98.primarycustomerbase.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class with light integration tests for {@link CustomerController}
 */
@WebMvcTest(CustomerController.class)
public class CustomerControllerLightIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private CustomerMapperService customerMapperService;

    @Test
    public void createCustomer_successfulAttempt() throws Exception {
        // given
        final CustomerDto customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);

        when(customerService.save(any(CustomerDto.class))).thenReturn(createCustomer(FIRST_NAME, LAST_NAME));
        when(customerMapperService.mapFrom(any(Customer.class))).thenReturn(customerDto);

        // when + then
        mockMvc.perform(post(CUSTOMERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void createCustomer_expectResourceNotFoundException() throws Exception {
        // given
        final CustomerDto customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);

        when(customerService.save(any(CustomerDto.class))).thenThrow(ResourceAlreadyExistsException.class);

        // when + then
        mockMvc.perform(post(CUSTOMERS_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDto)))
                .andExpect(status().isConflict())
                .andExpect(header().doesNotExist("Location"));
    }
}
