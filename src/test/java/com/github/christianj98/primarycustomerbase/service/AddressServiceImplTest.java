package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import com.github.christianj98.primarycustomerbase.mapper.AddressMapperService;
import com.github.christianj98.primarycustomerbase.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddress;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link AddressServiceImpl}
 */
@ExtendWith(MockitoExtension.class)
public class AddressServiceImplTest {
    @InjectMocks
    private AddressServiceImpl addressService;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private AddressMapperService addressMapperService;

    private Address address;

    private AddressDto addressDto;

    @BeforeEach
    public void init() {
        address = createAddress(STREET, CITY);
        addressDto = createAddressDto(STREET, CITY);
    }

    @Test
    public void findAll_UseRepository() {
        // given
        when(addressRepository.findAll()).thenReturn(List.of(address));
        when(addressMapperService.mapFrom(anyList())).thenReturn(List.of(addressDto));

        // when
        final List<AddressDto> addressDtos = addressService.findAll();

        // then
        assertThat(addressDtos).extracting(AddressDto::getStreet).containsOnly(address.getStreet());
        assertThat(addressDtos).extracting(AddressDto::getCity).containsOnly(address.getCity());
    }


}
