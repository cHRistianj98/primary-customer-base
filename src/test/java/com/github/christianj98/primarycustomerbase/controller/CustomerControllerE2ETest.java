package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

/**
 * Test class with E2E tests for {@link CustomerController}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("e2e")
public class CustomerControllerE2ETest {
    private static final String URI = "/customers";

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private CustomerDto customerDto;

    @BeforeEach
    public void init() {
        customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);
    }

    @Test
    public void createCustomer_ResourceAlreadyExists_ExceptionThrown() {
        // given
        restTemplate.postForEntity(URI, customerDto, Void.class);

        // when
        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(URI, customerDto, CustomerDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void createCustomer_successfulAttempt() {
        // given
        final CustomerDto customerDto = createCustomerDto("Andrzej", "Nowak");

        // when
        final ResponseEntity<CustomerDto> response = restTemplate.postForEntity(
                createURL(URI),
                customerDto,
                CustomerDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final CustomerDto createdCustomerDto = response.getBody();
        assert createdCustomerDto != null;
        assertThat(createdCustomerDto.getFirstName()).isEqualTo(customerDto.getFirstName());
        assertThat(createdCustomerDto.getLastName()).isEqualTo(customerDto.getLastName());
        final URI location = response.getHeaders().getLocation();
        assert location != null;
        assertThat(location.getPort()).isEqualTo(port);
        assertThat(location.getHost()).isEqualTo(HOST);
    }

    private String createURL(final String uri) {
        return String.format("http://%s:%s%s", HOST, port, uri);
    }
}