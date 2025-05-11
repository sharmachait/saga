package com.food.ordering.system.order.service.domain.dto.create;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class OrderAddress {
    @NotNull
    @NotBlank
    @NotEmpty
    @Max(value = 50)
    private final String street;
    @NotNull
    @NotBlank
    @NotEmpty
    @Max(value = 10)
    private final String postalCode;
    @NotNull
    @NotBlank
    @NotEmpty
    @Max(value = 50)
    private final String city;
}
