package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.exception.AddressAssignedToTheCustomerException;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.EntityNotFoundException;
import java.net.URI;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.ADDRESSES_URI;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.ADDRESSES_WITH_ID_URI;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.HOST;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
import static com.github.christianj98.primarycustomerbase.utils.GlobalTestUtils.asJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class with light integration tests for {@link AddressController}
 */
@WebMvcTest(AddressController.class)
public class AddressControllerLightIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    private AddressDto addressDto;

    @BeforeEach
    public void init() {
        addressDto = createAddressDto(STREET, CITY);
    }

    @Test
    public void findAll_findAllAddressesInDb() throws Exception {
        // given
        when(addressService.findAll()).thenReturn(List.of(addressDto));

        // when + then
        mockMvc.perform(get(ADDRESSES_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].street").value(addressDto.getStreet()))
                .andExpect(jsonPath("$[0].city").value(addressDto.getCity()));
    }

    @Test
    @DisplayName("Create address in table")
    public void save_createAddressWithSuccess() throws Exception {
        // given
        when(addressService.createAddress(any())).thenReturn(addressDto);

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
        when(addressService.createAddress(any())).thenThrow(ResourceAlreadyExistsException.class);

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
        when(addressService.findById(anyInt())).thenReturn(addressDto);

        // when + then
        mockMvc.perform(get(ADDRESSES_URI + "/{id}", addressDto.getId())
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
        when(addressService.findById(anyInt())).thenThrow(EntityNotFoundException.class);

        // when + then
        mockMvc.perform(get(ADDRESSES_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update address with given id")
    public void updateAddress_addressUpdated() throws Exception {
        // given
        int id = 1;
        when(addressService.update(any(), anyInt())).thenReturn(addressDto);

        // when + then
        mockMvc.perform(put(ADDRESSES_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.street").value(addressDto.getStreet()))
                .andExpect(jsonPath("$.city").value(addressDto.getCity()));
    }

    @Test
    @DisplayName("Update address with given id but address not found")
    public void updateAddress_addressNotFound() throws Exception {
        // given
        int id = 1;
        when(addressService.update(any(), anyInt())).thenThrow(EntityNotFoundException.class);

        // when + then
        mockMvc.perform(put(ADDRESSES_URI + "/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(addressDto)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Address not found during deletion of address")
    public void deleteAddress_addressNotFound() throws Exception {
        // given
        int id = 999;
        doThrow(EntityNotFoundException.class).when(addressService).delete(anyInt());


        // when + then
        mockMvc.perform(delete(ADDRESSES_WITH_ID_URI, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Address is already assigned to the customer and cannot be deleted")
    public void deleteAddress_addressAlreadyAssignedToTheCustomer() throws Exception {
        // given
        doThrow(AddressAssignedToTheCustomerException.class).when(addressService).delete(anyInt());
        int addressId = 1;

        // when
        mockMvc.perform(delete(ADDRESSES_WITH_ID_URI, addressId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Address is deleted successfully")
    public void deleteAddress_addressDeleted() throws Exception {
        // when + then
        mockMvc.perform(delete(ADDRESSES_WITH_ID_URI, addressDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}
