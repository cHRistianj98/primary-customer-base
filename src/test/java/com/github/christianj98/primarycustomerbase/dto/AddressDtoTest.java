package com.github.christianj98.primarycustomerbase.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AddressDtoTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    @DisplayName("Validate addressDto successfully")
    public void addressDto_validObject() {
        // when
        final AddressDto addressDto = createAddressDto(STREET, CITY);

        // then
        assertThat(validate(addressDto)).isEmpty();
    }

    @Test
    @DisplayName("Validation of addressDto fails")
    public void addressDto_invalidObject() {
        // when
        final AddressDto addressDto = createAddressDto("", CITY);
        final Optional<String> invalidAddress = validate(addressDto).stream().findAny();

        // then
        assertThat(invalidAddress).isNotEmpty();
        assertThat(invalidAddress.get()).contains("must not be blank");
    }

    private <T> Set<String> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        return violations.stream()
                .map(violation -> violation.getPropertyPath().toString() + " " + violation.getMessage())
                .collect(Collectors.toSet());
    }
}
