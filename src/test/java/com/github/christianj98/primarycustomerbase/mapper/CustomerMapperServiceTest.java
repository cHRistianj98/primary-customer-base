package com.github.christianj98.primarycustomerbase.mapper;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class CustomerMapperServiceTest {

    @InjectMocks
    private CustomerMapperService customerMapperService;

    @Test
    @DisplayName("Map customer entity from customer dto")
    public void shouldMapFromCustomerDto() {
        // given
        final CustomerDto customerDto = new CustomerDto();
        customerDto.setFirstName("Jan");
        customerDto.setLastName("Kowalski");

        // when
        final Customer customer = customerMapperService.mapFrom(customerDto);

        // then
        assertThat(customer.getFirstName()).isEqualTo(customerDto.getFirstName());
        assertThat(customer.getLastName()).isEqualTo(customerDto.getLastName());
    }

    @Test
    @DisplayName("Map customer dto from customer entity")
    public void shouldMapFromCustomerEntity() {
        // given
        final Customer customer = new Customer();
        customer.setFirstName("Jan");
        customer.setLastName("Kowalski");

        // when
        final CustomerDto customerdto = customerMapperService.mapFrom(customer);

        // then
        assertThat(customerdto.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(customerdto.getLastName()).isEqualTo(customer.getLastName());
    }

}
