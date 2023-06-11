package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import com.github.christianj98.primarycustomerbase.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderDto;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration test class for {@link OrderController}
 */
@WebMvcTest(OrderController.class)
public class OrderControllerLightIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private OrderService orderService;
    private OrderDto orderDto;

    @BeforeEach
    public void init() {
        orderDto = createOrderDto(ORDER_DATE, AMOUNT);
    }

    @Test
    @DisplayName("Find all orders together with information about the customer and the address")
    public void findAllOrders_AllExistingOrdersFound() throws Exception {
        // given
        when(orderService.findAll()).thenReturn(List.of(orderDto));

        // when + then
        mockMvc.perform(get(ORDERS_URI).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].date").value(orderDto.getDate().toString()))
                .andExpect(jsonPath("$[0].amount").value(orderDto.getAmount().toString()))
                .andExpect(jsonPath("$[0].customerDto.firstName").value(orderDto.getCustomerDto().getFirstName()))
                .andExpect(jsonPath("$[0].customerDto.lastName").value(orderDto.getCustomerDto().getLastName()));

    }


}
