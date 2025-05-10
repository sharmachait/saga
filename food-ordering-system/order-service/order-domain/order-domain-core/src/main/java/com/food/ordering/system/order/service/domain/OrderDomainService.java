package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {
    // takes one or more aggregate roots to trigger business logic
    // the application service will push these events to the other microservices
    // events should be created in the domain core layer so that the business operations
    // in that service are persisted before pushing the events to the other services
    // the repository calls should be done in the application service for single responsibility principle
    // the domain core updates the entities and performs business logics as required
    // the external dependency is then abstracted away from the  business logic
    // if creating an event requires persistence across multiple aggregate roots
    // only then should we create the event in the order domain service
    // otherwise the entities should create the events
    // but its a personal choice for all communication with domain entities to happen through a domain service
    OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaturant);
    OrderPaidEvent payOrder(Order order);
    void approveOrder(Order order);
    OrderCancelledEvent cancelOrderPayment(Order order, List<String>failureMessages);
    void cancelOrder(Order order, List<String> failureMessages);
}
