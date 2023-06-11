package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.CUSTOMERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;
import static com.github.christianj98.primarycustomerbase.utils.GlobalTestUtils.HOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/**
 * Test class with E2E tests for {@link CustomerController}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("e2e")
@ExtendWith(OutputCaptureExtension.class)
public class CustomerControllerE2ETest {
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
    public void createCustomer_ResourceAlreadyExists_ExceptionThrown(CapturedOutput output) {
        // given
        restTemplate.postForEntity(CUSTOMERS_URI, customerDto, CustomerDto.class);

        // when
        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(CUSTOMERS_URI, customerDto, CustomerDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNull();
        assertThat(output).contains(LAST_NAME);
    }

    @Test
    public void createCustomer_successfulAttempt() {
        // given
        final CustomerDto customerDto = createCustomerDto("Andrzej", "Nowak");

        // when
        final ResponseEntity<CustomerDto> response = restTemplate.postForEntity(
                createURL(CUSTOMERS_URI),
                customerDto,
                CustomerDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final CustomerDto createdCustomerDto = response.getBody();
        assert createdCustomerDto != null;
        assertThat(createdCustomerDto.getFirstName()).isEqualTo(customerDto.getFirstName());
        assertThat(createdCustomerDto.getLastName()).isEqualTo(customerDto.getLastName());
        assertThat(createdCustomerDto.getAddressDto().getStreet()).isEqualTo(customerDto.getAddressDto().getStreet());
        assertThat(createdCustomerDto.getAddressDto().getCity()).isEqualTo(customerDto.getAddressDto().getCity());
        final URI location = response.getHeaders().getLocation();
        assert location != null;
        assertThat(location.getPort()).isEqualTo(port);
        assertThat(location.getHost()).isEqualTo(HOST);
    }

    @Test
    public void findCustomers_findAllCustomersInDb() {
        // given
        restTemplate.postForEntity(CUSTOMERS_URI, customerDto, CustomerDto.class);

        // when
        var response = restTemplate.exchange(
                CUSTOMERS_URI,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<CustomerDto>>() {
                });

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<CustomerDto> customers = response.getBody();
        assertThat(customers).hasSize(1);
        assertThat(customers).extracting(CustomerDto::getFirstName).containsOnly(FIRST_NAME);
        assertThat(customers).extracting(CustomerDto::getLastName).containsOnly(LAST_NAME);
        assertThat(customers).extracting(CustomerDto::getAddressDto).extracting(AddressDto::getStreet)
                .containsOnly(STREET);
        assertThat(customers).extracting(CustomerDto::getAddressDto).extracting(AddressDto::getCity)
                .containsOnly(CITY);
    }

    @Test
    public void findById_CustomerFound() {
        // given
        int id = 1;
        restTemplate.postForEntity(CUSTOMERS_URI, customerDto, CustomerDto.class);

        // when
        var response = restTemplate.getForEntity(CUSTOMERS_URI + "/{id}", CustomerDto.class, id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CustomerDto foundCustomer = response.getBody();
        assertThat(foundCustomer).isNotNull();
        assertThat(foundCustomer.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(foundCustomer.getLastName()).isEqualTo(LAST_NAME);
        assertThat(foundCustomer.getAddressDto().getStreet()).isEqualTo(STREET);
        assertThat(foundCustomer.getAddressDto().getCity()).isEqualTo(CITY);

    }

    @Test
    public void findById_CustomerNotFound(CapturedOutput output) {
        // given
        Integer id = 999;

        // when
        var response = restTemplate.getForEntity(CUSTOMERS_URI + "/{id}", String.class, id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains(id.toString());
        assertThat(output).contains(id.toString());
    }

    @Test
    public void update_customerUpdated() {
        // given
        restTemplate.postForEntity(CUSTOMERS_URI, customerDto, CustomerDto.class);
        final CustomerDto customerDtoToUpdate = createCustomerDto("Andrzej", "Nowak");
        int id = 1;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerDto> requestEntity = new HttpEntity<>(customerDtoToUpdate, headers);

        // when
        var response = restTemplate.exchange(
                createURL(CUSTOMERS_URI + "/{id}"),
                HttpMethod.PUT,
                requestEntity,
                CustomerDto.class,
                id
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CustomerDto updatedCustomer = response.getBody();
        assertThat(updatedCustomer).isNotNull();
        assertThat(updatedCustomer.getFirstName()).isEqualTo(customerDtoToUpdate.getFirstName());
        assertThat(updatedCustomer.getLastName()).isEqualTo(customerDtoToUpdate.getLastName());
        assertThat(updatedCustomer.getAddressDto().getStreet()).isEqualTo(customerDtoToUpdate.getAddressDto().getStreet());
        assertThat(updatedCustomer.getAddressDto().getCity()).isEqualTo(customerDtoToUpdate.getAddressDto().getCity());
    }

    @Test
    public void update_customerNotFound() {
        // given
        final CustomerDto customerDtoToUpdate = createCustomerDto("Andrzej", "Nowak");
        int id = 999;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CustomerDto> requestEntity = new HttpEntity<>(customerDtoToUpdate, headers);

        // when
        var response = restTemplate.exchange(
                createURL(CUSTOMERS_URI + "/{id}"),
                HttpMethod.PUT,
                requestEntity,
                String.class,
                id
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).contains(Integer.toString(id));
    }

    @Test
    public void deleteById_customerDeleted() {
        // given
        restTemplate.postForEntity(CUSTOMERS_URI, customerDto, CustomerDto.class);
        int id = 1;

        // when
        var deleteResponse = restTemplate.exchange(
                CUSTOMERS_URI + "/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                id);

        // then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    }

    @Test
    public void deleteById_customerNotFound(CapturedOutput output) {
        // given
        int id = 999;

        // when
        var deleteResponse = restTemplate.exchange(
                CUSTOMERS_URI + "/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                id);

        // then
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(output).contains(Integer.toString(id));
    }

    private String createURL(final String uri) {
        return String.format("http://%s:%s%s", HOST, port, uri);
    }
}
