package com.github.christianj98.primarycustomerbase.mapper;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressMapperService {
    public Address mapFrom(AddressDto addressDto) {
        Address address = new Address();
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        return address;
    }

    public AddressDto mapFrom(Address address) {
        AddressDto addressDto = new AddressDto();
        addressDto.setId(address.getId());
        addressDto.setStreet(address.getStreet());
        addressDto.setCity(address.getCity());
        return addressDto;
    }

    public List<AddressDto> mapFrom(List<Address> addresses) {
        return addresses.stream()
                .map(this::mapFrom)
                .collect(Collectors.toList());
    }

}
