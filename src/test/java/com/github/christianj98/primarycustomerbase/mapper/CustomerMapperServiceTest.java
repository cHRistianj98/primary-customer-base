package com.github.christianj98.primarycustomerbase.mapper;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.entity.Address;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.CITY;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.STREET;
import static com.github.christianj98.primarycustomerbase.utils.AddressTestUtils.createAddressDto;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomer;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link CustomerMapperService}
 */
@ExtendWith(MockitoExtension.class)
public class CustomerMapperServiceTest {

    @InjectMocks
    private CustomerMapperService customerMapperService;

    @Mock
    private AddressMapperService addressMapperService;

    @Test
    @DisplayName("Map customer entity from customer dto")
    public void shouldMapFromCustomerDto() {
        // given
        final CustomerDto customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);

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
        final Customer customer = createCustomer(FIRST_NAME, LAST_NAME);
        when(addressMapperService.mapFrom(any(Address.class))).thenReturn(createAddressDto(STREET, CITY));

        // when
        final CustomerDto customerDto = customerMapperService.mapFrom(customer);

        // then
        assertThat(customerDto.getFirstName()).isEqualTo(customer.getFirstName());
        assertThat(customerDto.getLastName()).isEqualTo(customer.getLastName());
        assertThat(customerDto.getAddressDto().getStreet()).isEqualTo(customer.getAddress().getStreet());
        assertThat(customerDto.getAddressDto().getCity()).isEqualTo(customer.getAddress().getCity());
    }

    @Test
    @DisplayName("Map list of customer dtos from list of customer entities")
    public void shouldMapFromCustomerEntities() {
        // given
        final List<Customer> customers = List.of(createCustomer(FIRST_NAME, LAST_NAME),
                createCustomer("Andrzej", "Nowak"));
        when(addressMapperService.mapFrom(any(Address.class))).thenReturn(createAddressDto(STREET, CITY));

        // when
        final List<CustomerDto> customerDtos = customerMapperService.mapFrom(customers);

        // then
        assertThat(customerDtos).extracting(CustomerDto::getFirstName).hasSameElementsAs(
                customers.stream()
                        .map(Customer::getFirstName)
                        .collect(Collectors.toList()));
        assertThat(customerDtos).extracting(CustomerDto::getLastName).hasSameElementsAs(customers.stream()
                .map(Customer::getLastName)
                .collect(Collectors.toList()));
    }

}
