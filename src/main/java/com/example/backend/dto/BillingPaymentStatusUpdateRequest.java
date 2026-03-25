package com.example.backend.dto;

import com.example.backend.model.PaymentStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingPaymentStatusUpdateRequest {

    @NotNull(message = "Payment status is required")
    private PaymentStatus paymentStatus;
}
