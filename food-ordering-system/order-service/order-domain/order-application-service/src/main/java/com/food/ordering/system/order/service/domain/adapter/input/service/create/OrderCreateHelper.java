package com.food.ordering.system.order.service.domain.adapter.input.service.create;

import com.food.ordering.system.order.service.domain.OrderDomainService;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class OrderCreateHelper {
    private final OrderDomainService orderDomainService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final RestaurantRepository restaurantRepository;
    private final OrderDataMapper orderDataMapper;

    public OrderCreateHelper(OrderDomainService orderDomainService,
                             OrderRepository orderRepository,
                             CustomerRepository customerRepository,
                             RestaurantRepository restaurantRepository,
                             OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.restaurantRepository = restaurantRepository;
        this.orderDataMapper = orderDataMapper;
    }

    @Transactional
    public OrderCreatedEvent persistOrder(CreateOrderCommand createOrderCommand){
        checkCustomer(createOrderCommand.getCustomerId());
        Restaurant restaurant = checkRestaurant(createOrderCommand);
        Order order = orderDataMapper.createOrderCommandToOrder(createOrderCommand);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order, restaurant);
        saveOrder(order);
        log.info("Order created with id: {}", orderCreatedEvent.getOrder().getId().getValue());
        return orderCreatedEvent;
    }
    private Order saveOrder(Order order){
        Order orderResult =  orderRepository.save(order);
        if(orderResult==null){
            log.error("Could not save Order!");
            throw new OrderDomainException("Could not save Order!");
        }
        log.info("Order is saved with id: {}", orderResult.getId());
        return orderResult;
    }

    private Restaurant checkRestaurant(CreateOrderCommand createOrderCommand) {
        Restaurant restaurant = orderDataMapper.createOrderCommandToRestaurant(createOrderCommand);
        Optional<Restaurant> restaurantOptional = restaurantRepository.findRestaurantInformation(restaurant);
        if(restaurantOptional.isEmpty()){
            log.warn("Could not find restaurant with the given id: {}", createOrderCommand.getRestaurantId());
            //business level checks should be done in the domain core not in the application service
            throw new OrderDomainException("Could not find restaurant with the given id: "+createOrderCommand.getRestaurantId()+".");
        }
        return restaurantOptional.get();
    }

    private void checkCustomer(@NotNull UUID customerId) {
        Optional<Customer> customer = customerRepository.findCustomer(customerId);
        if(customer.isEmpty()){
            log.warn("Could not find customer with the given id: {}", customerId);
            //business level checks should be done in the domain core not in the application service
            throw new OrderDomainException("Could not find customer with the given id: "+customerId+".");
        }
    }
}
