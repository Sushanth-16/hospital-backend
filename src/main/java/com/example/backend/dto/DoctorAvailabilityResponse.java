package com.example.backend.dto;

import java.time.LocalDate;
import java.util.List;

public record DoctorAvailabilityResponse(
        Long doctorId,
        LocalDate date,
        List<String> availableSlots) {
}
