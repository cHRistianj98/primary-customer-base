package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.mapper.AddressMapperService;
import com.github.christianj98.primarycustomerbase.repository.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddress;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
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

    @Test
    public void save_addressCreated() {
        // given
        when(addressRepository.existsByStreetAndCity(anyString(), anyString())).thenReturn(false);
        when(addressRepository.save(any())).thenReturn(address);
        when(addressMapperService.mapFrom(any(Address.class))).thenReturn(addressDto);
        when(addressMapperService.mapFrom(any(AddressDto.class))).thenReturn(address);

        // when
        final AddressDto createdAddressDto = addressService.createAddress(addressDto);

        // then
        assertThat(createdAddressDto.getId()).isEqualTo(address.getId());
        assertThat(createdAddressDto.getStreet()).isEqualTo(address.getStreet());
        assertThat(createdAddressDto.getCity()).isEqualTo(address.getCity());
    }

    @Test
    public void save_addressAlreadyExist() {
        // given
        when(addressRepository.existsByStreetAndCity(anyString(), anyString())).thenReturn(true);

        // when + then
        assertThatThrownBy(() -> addressService.createAddress(addressDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContainingAll(STREET, CITY);
    }

    @Test
    public void findById_returnFoundAddress() {
        // given
        when(addressRepository.findById(anyInt())).thenReturn(Optional.of(address));
        when(addressMapperService.mapFrom(any(Address.class))).thenReturn(addressDto);

        // when
        final AddressDto foundAddress = addressService.findById(address.getId());

        // then
        assertThat(foundAddress).isNotNull();
        assertThat(foundAddress.getId()).isEqualTo(address.getId());
        assertThat(foundAddress.getStreet()).isEqualTo(STREET);
        assertThat(foundAddress.getCity()).isEqualTo(CITY);
    }

    @Test
    public void findById_throwsEntityNotFoundException() {
        // given
        when(addressRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> addressService.findById(address.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(Integer.toString(address.getId()));
    }

    @Test
    public void update_addressUpdated() {
        // given
        int id = 1;
        final AddressDto addressToUpdate = new AddressDto();
        addressToUpdate.setStreet("Polna");
        addressToUpdate.setCity("Warsaw");
        when(addressRepository.getReferenceById(anyInt())).thenReturn(address);
        when(addressMapperService.mapFrom(any(Address.class))).thenReturn(addressToUpdate);

        // when
        final AddressDto updatedAddress = addressService.update(addressToUpdate, id);

        // then
        assertThat(updatedAddress.getStreet()).isEqualTo(addressToUpdate.getStreet());
        assertThat(updatedAddress.getCity()).isEqualTo(addressToUpdate.getCity());
    }

    @Test
    public void update_addressNotFound() {
        // given
        int id = 999;
        when(addressRepository.getReferenceById(anyInt())).thenThrow(EntityNotFoundException.class);

        // when + then
        assertThatThrownBy(() -> addressService.update(addressDto, id))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
