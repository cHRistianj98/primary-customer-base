package com.github.christianj98.primarycustomerbase.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class CustomerDtoTest {

    @Autowired
    private LocalValidatorFactoryBean validator;

    @Test
    public void shouldValidateCustomerDto() {
        // when
        final CustomerDto customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);

        // then
        assertThat(validate(customerDto)).isEmpty();
    }

    @Test
    public void shouldInvalidCustomerDto() {
        // when
        final CustomerDto customerDto = createCustomerDto("", "");
        final Optional<String> invalidCustomer = validate(customerDto).stream().findAny();

        // then
        assertThat(invalidCustomer).isPresent();
        assertThat(invalidCustomer.get()).contains("must not be blank");
    }

    private <T> Set<String> validate(T object) {
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        return violations.stream()
                .map(violation -> violation.getPropertyPath().toString() + " " + violation.getMessage())
                .collect(Collectors.toSet());
    }

}
