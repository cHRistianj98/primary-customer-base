package com.github.christianj98.primarycustomerbase.mapper;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddress;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link AddressMapperService}
 */
@ExtendWith(MockitoExtension.class)
public class AddressMapperServiceTest {

    @InjectMocks
    private AddressMapperService addressMapperService;

    @Test
    @DisplayName("Map address entity from address dto")
    public void shouldMapFromAddressDto() {
        // given
        final AddressDto addressDto = createAddressDto(STREET, CITY);

        // when
        final Address address = addressMapperService.mapFrom(addressDto);

        // then
        assertThat(address.getStreet()).isEqualTo(addressDto.getStreet());
        assertThat(address.getCity()).isEqualTo(addressDto.getCity());
    }

    @Test
    @DisplayName("Map address dto from address entity")
    public void shouldMapFromAddress() {
        // given
        final Address address = createAddress(STREET, CITY);

        // when
        final AddressDto addressDto = addressMapperService.mapFrom(address);

        // then
        assertThat(addressDto.getStreet()).isEqualTo(address.getStreet());
        assertThat(addressDto.getCity()).isEqualTo(address.getCity());
    }

    @Test
    @DisplayName("Map address dtos from address entities")
    public void shouldMapFromAddressList() {
        // given
        final Address address = createAddress(STREET, CITY);
        final List<Address> addresses = List.of(address);

        // when
        final List<AddressDto> addressDtos = addressMapperService.mapFrom(addresses);

        // then
        assertThat(addressDtos).extracting(AddressDto::getStreet).containsOnly(address.getStreet());
        assertThat(addressDtos).extracting(AddressDto::getCity).containsOnly(address.getCity());
    }
}
