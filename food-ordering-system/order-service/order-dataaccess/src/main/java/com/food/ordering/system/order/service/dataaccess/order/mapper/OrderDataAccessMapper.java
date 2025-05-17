package com.food.ordering.system.order.service.dataaccess.order.mapper;

import com.food.ordering.system.domain.valueObject.*;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntityId;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.valueObject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueObject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueObject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILUREDELIMITTER;

@Component
public class OrderDataAccessMapper {
    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .address(deliveryAddressToAddress(order.getDeliveryAddress()))
                .price(order.getPrice().getAmount())
                .items(orderItemstoOrderItemEntities(order.getItems()))
                .orderStatus(order.getOrderStatus())
                .failureMessages(
                        order.getFailureMessages()!=null?
                                String.join(FAILUREDELIMITTER, order.getFailureMessages()) : ""
                )
                .build();
        orderEntity.getItems().forEach( orderItem -> orderItem.getId().setOrder(orderEntity));
        orderEntity.getAddress().setOrder(orderEntity);

        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder()
                .OrderId(new OrderId(orderEntity.getId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
                .price(new Money(orderEntity.getPrice()))
                .items(orderItemEntitiesToOrderItem(orderEntity.getItems()))
                .orderStatus(orderEntity.getOrderStatus())
                .failureMessages(orderEntity.getFailureMessages().isEmpty() ?
                        new ArrayList<>(List.of(orderEntity.getFailureMessages().split(FAILUREDELIMITTER))) : new ArrayList<>())
                .build();
    }

    private List<OrderItem> orderItemEntitiesToOrderItem(List<OrderItemEntity> items) {
        return items.stream()
                .map(
                        orderItemEntity -> OrderItem.builder()
                                .orderItemId(new OrderItemId(orderItemEntity.getId().getId()))
                                .quantity(orderItemEntity.getQuantity())
                                .price(new Money(orderItemEntity.getPrice()))
                                .subTotal(new Money(orderItemEntity.getSubTotal()))
                                .product(new Product(new ProductId(orderItemEntity.getProductId())))
                                .build()
                )
                .collect(Collectors.toList());
    }

    private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
        return StreetAddress.builder()
                .id(address.getId())
                .street(address.getStreet())
                .city(address.getCity())
                .postalCode(address.getPostalCode())
                .build();
    }

    private List<OrderItemEntity> orderItemstoOrderItemEntities(List<OrderItem> items) {
        return items.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(OrderItemEntityId.builder()
                                .id(orderItem.getId().getValue())
                                .build())
                        .productId(orderItem.getProduct().getId().getValue())
                        .price(orderItem.getPrice().getAmount())
                        .quantity(orderItem.getQuantity())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .build())
                .collect(Collectors.toList());
    }

    private OrderAddressEntity deliveryAddressToAddress(StreetAddress deliveryAddress) {
        return OrderAddressEntity.builder()
                .city(deliveryAddress.getCity())
                .street(deliveryAddress.getStreet())
                .postalCode(deliveryAddress.getPostalCode())
                .id(deliveryAddress.getId())
                .build();
    }
}
