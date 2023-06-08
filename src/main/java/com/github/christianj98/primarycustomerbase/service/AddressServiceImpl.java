package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.mapper.AddressMapperService;
import com.github.christianj98.primarycustomerbase.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.CUSTOMER_ALREADY_EXIST_ERROR;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapperService addressMapperService;

    public List<AddressDto> findAll() {
        return addressMapperService.mapFrom(addressRepository.findAll());
    }

    public AddressDto createAddress(final AddressDto addressDto) {
        if (addressRepository.existsByStreetAndCity(addressDto.getStreet(), addressDto.getCity())) {
            throw new ResourceAlreadyExistsException(
                    String.format(CUSTOMER_ALREADY_EXIST_ERROR.getMessage(),
                            addressDto.getStreet(),
                            addressDto.getCity()));
        }
        final Address address = addressRepository.save(addressMapperService.mapFrom(addressDto));
        return addressMapperService.mapFrom(address);
    }

    public AddressDto findById(final int id) {
        return addressRepository.findById(id)
                .map(addressMapperService::mapFrom)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("Address not found with given id: %s", id)));
    }
}
