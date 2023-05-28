package com.github.christianj98.primarycustomerbase.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class CustomerDto {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
