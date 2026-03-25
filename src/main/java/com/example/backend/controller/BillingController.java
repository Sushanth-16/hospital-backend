package com.example.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.dto.BillingPaymentStatusUpdateRequest;
import com.example.backend.model.Billing;
import com.example.backend.service.BillingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/billings")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping
    public List<Billing> getBillings(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId) {
        if (patientId != null) {
            return billingService.getBillingsByPatientId(patientId);
        }

        if (doctorId != null) {
            return billingService.getBillingsByDoctorId(doctorId);
        }

        return billingService.getAllBillings();
    }

    @PutMapping("/{id}/payment-status")
    public Billing updatePaymentStatus(
            @PathVariable Long id,
            @Valid @RequestBody BillingPaymentStatusUpdateRequest request) {
        return billingService.updatePaymentStatus(id, request.getPaymentStatus());
    }
}
