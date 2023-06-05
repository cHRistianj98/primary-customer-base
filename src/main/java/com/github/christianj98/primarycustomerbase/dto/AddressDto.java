package com.github.christianj98.primarycustomerbase.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class AddressDto {
    @NotBlank
    private String street;
    @NotBlank
    private String city;
}
