package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.ADDRESSES_URI;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.HOST;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
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
}
