package com.github.christianj98.primarycustomerbase.mapper;

import com.github.christianj98.primarycustomerbase.dto.CustomerDto;
import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.entity.Customer;
import com.github.christianj98.primarycustomerbase.entity.Order;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.FIRST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.LAST_NAME;
import static com.github.christianj98.primarycustomerbase.utils.CustomerTestUtils.createCustomerDto;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for {@link OrderMapperService}
 */
@ExtendWith(MockitoExtension.class)
public class OrderMapperServiceTest {
    @InjectMocks
    private OrderMapperService orderMapperService;
    @Mock
    private CustomerMapperService customerMapperService;
    @Mock
    private AddressMapperService addressMapperService;

    @Test
    @DisplayName("Maps order dto from order entity")
    public void mapFrom_mapsOrderDtoFromOrderEntity() {
        // given
        final Order order = createOrder(ORDER_DATE, AMOUNT);
        final CustomerDto customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);
        when(customerMapperService.mapFrom(any(Customer.class))).thenReturn(customerDto);

        // when
        final OrderDto orderDto = orderMapperService.mapFrom(order);

        // then
        assertThat(orderDto.getOrderId()).isEqualTo(order.getId());
        assertThat(orderDto.getDate()).isEqualTo(order.getDate());
        assertThat(orderDto.getAmount()).isEqualTo(order.getAmount());
        assertThat(orderDto.getCustomerDto()).isEqualTo(customerDto);
    }

    @Test
    @DisplayName("Maps list of order dtos from list of order entities")
    public void mapFrom_mapsOrderEntityFromOrderDto() {
        // given
        final Order order = createOrder(ORDER_DATE, AMOUNT);
        final CustomerDto customerDto = createCustomerDto(FIRST_NAME, LAST_NAME);
        when(customerMapperService.mapFrom(any(Customer.class))).thenReturn(customerDto);
        final List<Order> orders = List.of(order);

        // when
        final List<OrderDto> orderDtos = orderMapperService.mapFrom(orders);

        // then
        assertThat(orderDtos).isNotEmpty();
        assertThat(orderDtos).extracting(OrderDto::getOrderId,
                        OrderDto::getDate,
                        OrderDto::getAmount,
                        OrderDto::getCustomerDto)
                .isEqualTo(List.of(Tuple.tuple(order.getId(),
                        order.getDate(),
                        order.getAmount(),
                        customerDto)));
    }
}
