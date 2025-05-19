package com.food.ordering.system.order.service.dataaccess.restaurant.mapper;

import com.food.ordering.system.domain.valueObject.Money;
import com.food.ordering.system.domain.valueObject.ProductId;
import com.food.ordering.system.domain.valueObject.RestaurantId;
import com.food.ordering.system.order.service.dataaccess.restaurant.entity.RestaurantEntity;
import com.food.ordering.system.order.service.dataaccess.restaurant.exception.RestaurantDataAcessException;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RestaurantDataAccessMapper {
    public List<UUID> restaurantToRestaurantProducts(Restaurant restaurant) {
        return restaurant.getProducts().stream()
                .map(x->x.getId().getValue())
                .collect(Collectors.toList());
    }

    public Restaurant restaurantEntityToRestaurant(List<RestaurantEntity> restaurantEntities) throws RestaurantDataAcessException {
        if(restaurantEntities == null || restaurantEntities.isEmpty()){
            throw new RestaurantDataAcessException("Restaurant not found!");
        }

        List<Product> products = restaurantEntities.stream()
                .map(
        entity ->
                        new Product(
                                new ProductId(entity.getId().getProductId()),
                                new Money(entity.getProductPrice()))
                ).collect(Collectors.toList());

        RestaurantEntity restaurantEntity = restaurantEntities.getFirst();

        return Restaurant.builder()
                .restaurantId(new RestaurantId(restaurantEntity.getId().getRestaurantId()))
                .active(restaurantEntity.getRestaurantActive())
                .products(products)
                .build();
    }
}
