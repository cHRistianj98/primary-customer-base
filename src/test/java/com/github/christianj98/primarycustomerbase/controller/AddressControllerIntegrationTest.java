package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import com.github.christianj98.primarycustomerbase.repository.AddressRepository;
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

import java.net.URI;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.ADDRESSES_URI;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.HOST;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddress;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
import static com.github.christianj98.primarycustomerbase.utils.GlobalTestUtils.asJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class AddressControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AddressRepository addressRepository;

    private Address address;

    private AddressDto addressDto;

    @BeforeEach
    public void init() {
        address = createAddress(STREET, CITY);
        addressDto = createAddressDto(STREET, CITY);
    }

    @Test
    @DisplayName("Find all addresses in table")
    public void findAll_findAllAddressesInDb() throws Exception {
        // given
        addressRepository.save(address);

        // when
        mockMvc.perform(get(ADDRESSES_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].street").value(STREET))
                .andExpect(jsonPath("$[0].city").value(CITY));
    }

    @Test
    @DisplayName("Create address in table")
    public void save_createAddressWithSuccess() throws Exception {
        // when + then
        final String location = mockMvc.perform(post(ADDRESSES_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city").value(addressDto.getCity()))
                .andExpect(jsonPath("$.street").value(addressDto.getStreet()))
                .andReturn()
                .getResponse()
                .getHeader("Location");
        assert location != null;
        final URI locationUri = URI.create(location);
        assertThat(locationUri.getHost()).isEqualTo(HOST);
        assertThat(locationUri.getPath()).isEqualTo(ADDRESSES_URI + "/" + addressDto.getId());
    }

    @Test
    @DisplayName("Create address but this address already exist")
    public void save_addressAlreadyExist() throws Exception {
        // given
        address = addressRepository.save(address);

        // when + then
        mockMvc.perform(post(ADDRESSES_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(header().doesNotExist("Location"));
    }

    @Test
    @DisplayName("Find address by id and address was found")
    public void findById_addressFound() throws Exception {
        // given
        address = addressRepository.save(address);

        // when + then
        mockMvc.perform(get(ADDRESSES_URI + "/{id}", address.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value(STREET))
                .andExpect(jsonPath("$.city").value(CITY));
    }

    @Test
    @DisplayName("Find address by id but address was not found")
    public void findById_addressNotFound() throws Exception {
        // given
        int id = 999;

        // when + then
        final String errorMessage = mockMvc.perform(get(ADDRESSES_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(errorMessage).isEqualTo("Address not found with given id: " + id);
    }

    @Test
    @DisplayName("Update address with given id")
    public void updateAddress_addressUpdated() throws Exception {
        // given
        addressRepository.save(address);
        int id = 1;
        final AddressDto addressToUpdate = createAddressDto("Perla", "Perl");

        // when + then
        mockMvc.perform(put(ADDRESSES_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressToUpdate)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value(addressToUpdate.getStreet()))
                .andExpect(jsonPath("$.city").value(addressToUpdate.getCity()));

    }

    @Test
    @DisplayName("Update address with given id but address not found")
    public void updateAddress_addressNotFound() throws Exception {
        // given
        int id = 999;

        // when + then
        mockMvc.perform(put(ADDRESSES_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
