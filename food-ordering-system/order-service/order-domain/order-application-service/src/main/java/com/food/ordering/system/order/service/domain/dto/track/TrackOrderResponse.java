package com.food.ordering.system.order.service.domain.dto.track;

import com.food.ordering.system.domain.valueObject.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Getter
public class TrackOrderResponse {
    @NotNull
    private final UUID orderTrackId;
    @NotNull
    private final OrderStatus orderStatus;
    private final List<String> failureMessages;
}
