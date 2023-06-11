package com.github.christianj98.primarycustomerbase.controller;

import com.github.christianj98.primarycustomerbase.dto.OrderDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.AMOUNT;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDERS_URI;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.ORDER_DATE;
import static com.github.christianj98.primarycustomerbase.utils.OrderTestUtils.createOrderDto;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("e2e")
public class OrderControllerE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private OrderDto orderDto;

    @BeforeEach
    public void init() {
        orderDto = createOrderDto(ORDER_DATE, AMOUNT);
    }

    @Test
    @DisplayName("Find all orders together with information about the customer and the address")
    public void findAllOrders_AllExistingOrdersFound() {
        // when
        var response = restTemplate.getForEntity(ORDERS_URI, List.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }
}
