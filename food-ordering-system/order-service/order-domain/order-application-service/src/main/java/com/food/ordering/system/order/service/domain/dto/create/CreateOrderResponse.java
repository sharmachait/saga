package com.food.ordering.system.order.service.domain.dto.create;

import com.food.ordering.system.domain.valueObject.OrderStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
public class CreateOrderResponse {
    @NotNull
    private final UUID orderTrackingId;
    @NotNull
    private final OrderStatus orderStatus;
    @NotNull
    @NotBlank
    @NotEmpty
    private final String message;
}
