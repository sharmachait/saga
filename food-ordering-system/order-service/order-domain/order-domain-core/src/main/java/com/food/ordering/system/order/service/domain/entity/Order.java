package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueObject.*;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueObject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueObject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueObject.TrackingId;

import java.util.List;
import java.util.UUID;

public class Order extends AggregateRoot<OrderId> {
    private final CustomerId customerId;
    private final RestaurantId restaurantId;
    private final StreetAddress deliveryAddress;
    private final Money price;
    private final List<OrderItem> items;
    public static final String FAILUREDELIMITTER = ",";
    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessages;

    public static Builder builder(){
        return new Builder();
    }

    public void initializeOrder(){
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = OrderStatus.PENDING;
        initializeOrderItems();
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for(OrderItem orderItem: items){
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    public void validateOrder(){
        validateInitialOrder();
        validateTotalPrice();
        validateItemsPrice();
    }

    private void validateInitialOrder() {
        if(orderStatus != null || getId() != null){
            throw new OrderDomainException("Order is in invalid State for initialization");
        }
    }

    private void validateTotalPrice() {
        if(price == null || !price.isGreaterThanZero()){
            throw new OrderDomainException("Total price must be greater than zero.");
        }
    }

    private void validateItemsPrice() {
        Money orderItemsTotal = items.stream().map(orderItem ->{
            validateItemPrice(orderItem);
            return orderItem.getSubTotal();
        }).reduce(Money.ZERO, Money::add);
        if(!price.equals(orderItemsTotal)){
            throw new OrderDomainException("Total price: " + price.getAmount()
                    + " is not equal to Order items total: "+ orderItemsTotal.getAmount() + ".");
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        // validate the item price against the price in the persistent store
        if(!orderItem.isPriceValid()){
            throw new OrderDomainException("Order item price: " + orderItem.getPrice().getAmount() +
                    " is not valid for product " + orderItem.getProduct().getId().getValue() +".");
        }
    }

    public void pay(){
        if(orderStatus != OrderStatus.PENDING){
            throw new OrderDomainException("Order is not in correct state for payment.");
        }
        orderStatus = OrderStatus.PAID;
    }

    public void approve(){
        if(orderStatus!=OrderStatus.PAID){
            throw new OrderDomainException("Order is not in correct state for approval.");
        }
        orderStatus = OrderStatus.APPROVED;
    }

    public void initCancel(List<String> failureMessages){
        if(orderStatus != OrderStatus.PAID){
            throw new OrderDomainException("Order is not in correct state to initialize cancellation.");
        }
        orderStatus = OrderStatus.CANCELLING;
        updateFailureMessages(failureMessages);
    }

    public void cancel(List<String> failureMessages){
        if(!(orderStatus == OrderStatus.CANCELLING || orderStatus == OrderStatus.PENDING)){
            throw new OrderDomainException("Order is not in correct state for cancellation");
        }
        orderStatus = OrderStatus.CANCELLED;
        updateFailureMessages(failureMessages);
    }

    private void updateFailureMessages(List<String> failureMessages) {
        if(this.failureMessages != null && failureMessages!=null){
            this.failureMessages.addAll(
                    failureMessages.stream()
                            .filter(s->!s.isEmpty())
                            .toList()
            );
        }
        if(this.failureMessages == null){
            this.failureMessages = failureMessages;
        }
    }

    private Order(Builder builder) {
        super.setId(builder.OrderId);
        customerId = builder.customerId;
        restaurantId = builder.restaurantId;
        deliveryAddress = builder.deliveryAddress;
        price = builder.price;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessages = builder.failureMessages;
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurantId getRestaurantId() {
        return restaurantId;
    }

    public StreetAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }

    public static final class Builder {
        private OrderId OrderId;
        private CustomerId customerId;
        private RestaurantId restaurantId;
        private StreetAddress deliveryAddress;
        private Money price;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessages;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder OrderId(OrderId val) {
            OrderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
        }

        public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
