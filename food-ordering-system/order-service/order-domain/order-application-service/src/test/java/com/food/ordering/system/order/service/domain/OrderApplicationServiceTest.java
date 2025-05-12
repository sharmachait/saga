package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueObject.CustomerId;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.create.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.input.service.OrderApplicationService;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SpringBootTest(classes = OrderTestConfiguration.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderApplicationServiceTest {
    @Autowired
    private OrderDataMapper orderDataMapper;
    @Autowired
    private OrderApplicationService orderApplicationService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private CustomerRepository customerRepository;

    private CreateOrderCommand createOrderCommand;
    private CreateOrderCommand createOrderCommandWrongPrice;
    private CreateOrderCommand createOrderCommandWrongProductPrice;
    private final UUID CUSTOMER_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb41");
    private final UUID RESTAURANT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb45");
    private final UUID PRODUCT_ID = UUID.fromString("d215b5f8-0249-4dc5-89a3-51fd148cfb48");
    private final UUID ORDER_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afb");
    private final UUID SAGA_ID = UUID.fromString("15a497c1-0f4b-4eff-b9f4-c402c8c07afa");
    private final BigDecimal PRICE = new BigDecimal("200.00");

    @BeforeAll
    public void init(){
        createOrderCommand = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(PRICE)
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        createOrderCommandWrongPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("250.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("50.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        createOrderCommandWrongProductPrice = CreateOrderCommand.builder()
                .customerId(CUSTOMER_ID)
                .restaurantId(RESTAURANT_ID)
                .address(OrderAddress.builder()
                        .street("street_1")
                        .postalCode("1000AB")
                        .city("Paris")
                        .build())
                .price(new BigDecimal("210.00"))
                .items(List.of(OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(1)
                                .price(new BigDecimal("60.00"))
                                .subTotal(new BigDecimal("60.00"))
                                .build(),
                        OrderItem.builder()
                                .productId(PRODUCT_ID)
                                .quantity(3)
                                .price(new BigDecimal("50.00"))
                                .subTotal(new BigDecimal("150.00"))
                                .build()))
                .build();

        Customer customer = new Customer(new CustomerId(CUSTOMER_ID));
    }
}
