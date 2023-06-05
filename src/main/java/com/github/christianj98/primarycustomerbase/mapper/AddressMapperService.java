package com.github.christianj98.primarycustomerbase.mapper;

import com.github.christianj98.primarycustomerbase.dto.AddressDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import org.springframework.stereotype.Service;

@Service
public class AddressMapperService {
    public Address mapFrom(AddressDto customerDto) {
        Address address = new Address();
        address.setStreet(customerDto.getStreet());
        address.setCity(customerDto.getCity());
        return address;
    }

    public AddressDto mapFrom(Address address) {
        AddressDto addressDto = new AddressDto();
        addressDto.setStreet(address.getStreet());
        addressDto.setCity(address.getCity());
        return addressDto;
    }

}
