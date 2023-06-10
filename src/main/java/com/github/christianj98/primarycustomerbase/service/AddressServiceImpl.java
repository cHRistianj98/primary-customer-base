package com.github.christianj98.primarycustomerbase.service;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import com.github.christianj98.primarycustomerbase.exception.AddressAssignedToTheCustomerException;
import com.github.christianj98.primarycustomerbase.exception.ResourceAlreadyExistsException;
import com.github.christianj98.primarycustomerbase.mapper.AddressMapperService;
import com.github.christianj98.primarycustomerbase.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.ADDRESS_ASSIGNED_TO_THR_CUSTOMER_ERROR;
import static com.github.christianj98.primarycustomerbase.message.ErrorMessages.CUSTOMER_ALREADY_EXIST_ERROR;
import static java.util.Objects.nonNull;

@Service
@RequiredArgsConstructor
@Transactional
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

    public AddressDto update(final AddressDto addressDto, final int id) {
        Address address = addressRepository.getReferenceById(id);
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        return addressMapperService.mapFrom(address);
    }

    public void delete(final int id) {
        final Address address = addressRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("Address not found with given id: %s", id)));

        if (nonNull(address.getCustomer())) {
            throw new AddressAssignedToTheCustomerException(ADDRESS_ASSIGNED_TO_THR_CUSTOMER_ERROR.getMessage());
        }
        addressRepository.delete(address);
    }
}
