package com.github.christianj98.primarycustomerbase.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class CustomerDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @Valid
    @NotNull
    private AddressDto addressDto;
}
