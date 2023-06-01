package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("e2e")
public class CustomerControllerE2ETest {
    private static final String FIRST_NAME = "Jan";
    private static final String LAST_NAME = "Kowalski";

    @Autowired
    private TestRestTemplate restTemplate;

    private CustomerDto customerDto;

    @BeforeEach
    public void init() {
        customerDto = createCustomerDto();
    }

    @Test
    public void createCustomer_ResourceAlreadyExists_ExceptionThrown() {
        // given
        restTemplate.postForEntity("/customers", customerDto, Void.class);

        // when
        ResponseEntity<Void> response = restTemplate.postForEntity("/customers", customerDto, Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    private CustomerDto createCustomerDto() {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName(FIRST_NAME);
        customerDto.setLastName(LAST_NAME);
        return customerDto;
    }
}