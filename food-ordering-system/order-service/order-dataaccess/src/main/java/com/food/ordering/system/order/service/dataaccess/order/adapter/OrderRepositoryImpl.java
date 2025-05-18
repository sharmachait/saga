package com.food.ordering.system.order.service.dataaccess.order.adapter;

import com.food.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper;
import com.food.ordering.system.order.service.dataaccess.order.repository.OrderJpaRepository;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.order.service.domain.valueObject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository jpaRepository;
    private final OrderDataAccessMapper orderDataAccessMapper;

    public OrderRepositoryImpl(OrderJpaRepository jpaRepository, OrderDataAccessMapper orderDataAccessMapper) {
        this.jpaRepository = jpaRepository;
        this.orderDataAccessMapper = orderDataAccessMapper;
    }

    @Override
    public Order save(Order order) {
        return orderDataAccessMapper.orderEntityToOrder(
                jpaRepository.save(orderDataAccessMapper.orderToOrderEntity(order))
        );
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingid) {
        return jpaRepository.findByTrackingId(trackingid.getValue())
                .map(orderDataAccessMapper::orderEntityToOrder);
    }
}
