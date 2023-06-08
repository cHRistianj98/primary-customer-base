package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
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

import java.net.URI;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.ADDRESSES_URI;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.HOST;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
import static com.github.christianj98.primarycustomerbase.utils.GlobalTestUtils.asJsonString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

}
