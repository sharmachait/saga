package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueObject.ProductId;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

    private static final ZoneId zoneIdUTC = ZoneId.of("UTC");

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order, Restaurant restaturant) {
        validateRestaurant(restaturant);
        setOrderProductInformation(order, restaturant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id: {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order, ZonedDateTime.now(zoneIdUTC));
    }

    private void validateRestaurant(Restaurant restaturant) {
        if(!restaturant.isActive()){
            throw new OrderDomainException("Restaurant with id "
                    + restaturant.getId().getValue()
                    + " is not active");
        }
    }

    private void setOrderProductInformation(Order order, Restaurant restaturant) {
        Map<ProductId, Product> restaurantProducts = new HashMap<>();
        for(Product product: restaturant.getProducts()){
            restaurantProducts.put(product.getId(), product);
        }
        order.getItems()
            .forEach(orderItem ->{
                Product currentProduct = orderItem.getProduct();
                Product restaurantProduct = restaurantProducts.get(currentProduct.getId());
                if(currentProduct.equals(restaurantProduct)){
                    currentProduct.updateWithConfirmedNameAndPrice(
                        restaurantProduct.getName(),
                        restaurantProduct.getPrice()
                    );

                }
            });
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id: {} is paid.", order.getId().getValue());
        return new OrderPaidEvent(order, ZonedDateTime.now(zoneIdUTC));
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id: {} is approved.", order.getId().getValue());
        // doesnt return an event because we dont need another localized transaction now,
        // order can be tracked with the tracking id that we will return to the client
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages) {
        order.initCancel(failureMessages);
        log.info("Order payment is cancelling for order id: {}.", order.getId().getValue());
        return new OrderCancelledEvent(order, ZonedDateTime.now(zoneIdUTC));
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
        log.info("Order with id: {} is cancelled.", order.getId().getValue());
    }
}
