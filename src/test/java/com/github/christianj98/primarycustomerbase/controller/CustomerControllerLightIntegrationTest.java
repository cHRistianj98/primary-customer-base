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

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.CUSTOMERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.asJsonString;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Test
    public void findAllCustomer_expectAllCustomersFromDb() throws Exception {
        // given
        final Customer customer = createCustomer(FIRST_NAME, LAST_NAME);
        final CustomerDto customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);

        when(customerService.findAll()).thenReturn(List.of(customer));
        when(customerMapperService.mapFrom(anyList())).thenReturn(List.of(customerDto));

        // when
        mockMvc.perform(get(CUSTOMERS_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$[0].firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$[0].lastName").value(LAST_NAME));
    }

    @Test
    public void findById_expectOneCustomer() throws Exception {
        // given
        int id = 1;
        final CustomerDto customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);

        when(customerService.findById(anyInt())).thenReturn(customerDto);

        // when
        mockMvc.perform(get(CUSTOMERS_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(LAST_NAME));
    }

    @Test
    public void findById_expectEntityNotFoundException() throws Exception {
        // given
        int id = 1;

        when(customerService.findById(anyInt())).thenThrow(EntityNotFoundException.class);

        // when
        final Exception resolvedException = mockMvc.perform(get(CUSTOMERS_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException();
        assertThat(resolvedException).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    public void update_expectUpdatedCustomer() throws Exception {
        // given
        int id = 1;
        final CustomerDto customerDtoToUpdate = createCustomerDto("Andrzej", "Nowak");
        when(customerService.update(any(), anyInt())).thenReturn(customerDtoToUpdate);

        // when
        mockMvc.perform(put(CUSTOMERS_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDtoToUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(customerDtoToUpdate.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(customerDtoToUpdate.getLastName()));
    }

    @Test
    public void update_expectCustomerNotFound() throws Exception {
        // given
        int id = 1;
        final CustomerDto customerDtoToUpdate = createCustomerDto("Andrzej", "Nowak");
        when(customerService.update(any(), anyInt())).thenThrow(EntityNotFoundException.class);

        // when + then
        var exception = mockMvc.perform(put(CUSTOMERS_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(customerDtoToUpdate)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResolvedException();
        assertThat(exception).isInstanceOf(EntityNotFoundException.class);
    }
}
