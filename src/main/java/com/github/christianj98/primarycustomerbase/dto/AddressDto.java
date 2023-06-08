package com.github.christianj98.primarycustomerbase.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class AddressDto {
    @JsonIgnore
    private int id;
    @NotBlank
    private String street;
    @NotBlank
    private String city;
}
