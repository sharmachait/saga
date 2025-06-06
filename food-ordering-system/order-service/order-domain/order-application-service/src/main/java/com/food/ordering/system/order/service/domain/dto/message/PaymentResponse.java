package com.food.ordering.system.order.service.domain.dto.message;

import com.food.ordering.system.domain.valueObject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
public class PaymentResponse {
    private String id;
    private String sagaId; // used to track distributed transactions across services
    private String orderId;
    private String paymentId;
    private String customerId;
    private BigDecimal price;
    private Instant createdAt;
    private PaymentStatus paymentStatus;
    private List<String> failureMessages;
}
