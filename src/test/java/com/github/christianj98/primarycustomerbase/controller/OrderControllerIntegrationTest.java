package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderDto;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
@ActiveProfiles("integration")
public class OrderControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    private OrderDto orderDto;

    @BeforeEach
    public void init() {
        orderDto = createOrderDto(ORDER_DATE, AMOUNT);
    }

    @Test
    @DisplayName("Find all orders together with information about the customer and the address")
    public void findAllOrders_AllExistingOrdersFound() throws Exception {
        // when
        mockMvc.perform(get(ORDERS_URI).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

}
