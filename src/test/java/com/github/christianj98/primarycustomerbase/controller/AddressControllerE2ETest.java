package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.ADDRESS_ASSIGNED_TO_THR_CUSTOMER_ERROR;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.ADDRESSES_URI;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.CUSTOMERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;
import static com.github.christianj98.primarycustomerbase.utils.GlobalTestUtils.HOST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

/**
 * Test class with E2E tests for {@link AddressController}
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("e2e")
@ExtendWith(OutputCaptureExtension.class)
public class AddressControllerE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private AddressDto addressDto;

    @BeforeEach
    public void init() {
        addressDto = createAddressDto(STREET, CITY);
    }

    @Test
    @DisplayName("Find list of addresses")
    public void findAll_findListOfAddresses() {
        // given
        restTemplate.postForEntity(ADDRESSES_URI, addressDto, AddressDto.class);

        // when
        var response = restTemplate.getForEntity(ADDRESSES_URI, List.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    @DisplayName("Create address")
    public void save_shouldAddressBeCreated() {
        // when
        var response = restTemplate.postForEntity(ADDRESSES_URI, addressDto, AddressDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        final AddressDto body = response.getBody();
        assert body != null;
        assertThat(body.getStreet()).isEqualTo(addressDto.getStreet());
        assertThat(body.getCity()).isEqualTo(addressDto.getCity());
        final URI location = response.getHeaders().getLocation();
        assert location != null;
        assertThat(location.getHost()).isEqualTo(HOST);
        assertThat(location.getPort()).isEqualTo(port);
    }

    @Test
    @DisplayName("Create address, but address already exist")
    public void save_exceptionIsThrownBecauseAddressAlreadyExist(CapturedOutput output) {
        // given
        restTemplate.postForEntity(ADDRESSES_URI, addressDto, AddressDto.class);

        // when
        var createdAddress = restTemplate.postForEntity(ADDRESSES_URI, addressDto, AddressDto.class);

        // then
        assertThat(createdAddress.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(output).contains(addressDto.getStreet());
        assertThat(output).contains(addressDto.getCity());
    }

    @Test
    @DisplayName("Find address by id and address is found")
    public void findById_shouldFindAddress() {
        // given
        restTemplate.postForEntity(ADDRESSES_URI, addressDto, AddressDto.class);

        // when
        var response = restTemplate.getForEntity(ADDRESSES_URI + "/{id}", AddressDto.class, addressDto.getId());

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStreet()).isEqualTo(addressDto.getStreet());
        assertThat(response.getBody().getCity()).isEqualTo(addressDto.getCity());
    }

    @Test
    @DisplayName("Find address by id but address was not found")
    public void findById_returnNotFound(CapturedOutput output) {
        // given
        int id = 999;

        // when
        var response = restTemplate.getForEntity(ADDRESSES_URI + "/{id}", String.class, id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(output).contains(Integer.toString(id));
    }

    @Test
    @DisplayName("Update address with given id")
    public void updateAddress_addressUpdated() {
        // given
        restTemplate.postForEntity(ADDRESSES_URI, addressDto, AddressDto.class);
        int id = 1;
        final AddressDto addressToUpdate = createAddressDto("Perla", "Perl");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddressDto> requestEntity = new HttpEntity<>(addressToUpdate, headers);

        // when
        var response = restTemplate.exchange(
                ADDRESSES_URI + "/{id}",
                HttpMethod.PUT,
                requestEntity,
                AddressDto.class,
                id
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        final AddressDto updatedAddress = response.getBody();
        assertThat(updatedAddress).isNotNull();
        assertThat(updatedAddress.getStreet()).isEqualTo(addressToUpdate.getStreet());
        assertThat(updatedAddress.getCity()).isEqualTo(addressToUpdate.getCity());
    }

    @Test
    @DisplayName("Update address with given id but address not found")
    public void updateAddress_addressNotFound() {
        // given
        restTemplate.postForEntity(ADDRESSES_URI, addressDto, AddressDto.class);
        int id = 999;
        final AddressDto addressToUpdate = createAddressDto("Perla", "Perl");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AddressDto> requestEntity = new HttpEntity<>(addressToUpdate, headers);

        // when
        var response = restTemplate.exchange(
                ADDRESSES_URI + "/{id}",
                HttpMethod.PUT,
                requestEntity,
                String.class,
                id
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Address not found during deletion of address")
    public void deleteAddress_addressNotFound(CapturedOutput output) {
        // given
        int id = 999;

        // when
        var response = restTemplate.exchange(ADDRESSES_URI + "/{id}",
                HttpMethod.DELETE,
                null,
                String.class,
                id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(output).contains(Integer.toString(id));
    }

    @Test
    @DisplayName("Address is already assigned to the customer and cannot be deleted")
    public void deleteAddress_addressAlreadyAssignedToTheCustomer(CapturedOutput output) {
        // given
        restTemplate.postForEntity(CUSTOMERS_URI, createCustomerDto(FIRST_NAME, LAST_NAME), CustomerDto.class);
        int addressId = 1;

        // when
        var response = restTemplate.exchange(ADDRESSES_URI + "/{id}",
                HttpMethod.DELETE,
                null,
                String.class,
                addressId);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(output).contains(ADDRESS_ASSIGNED_TO_THR_CUSTOMER_ERROR.getMessage());
    }

    @Test
    @DisplayName("Address is deleted successfully")
    public void deleteAddress_addressDeleted() {
        // given
        restTemplate.postForEntity(ADDRESSES_URI, addressDto, AddressDto.class);
        int id = 1;

        // when
        var response = restTemplate.exchange(ADDRESSES_URI + "/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                id);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
