package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Billing;

public interface BillingRepository extends JpaRepository<Billing, Long> {

    Optional<Billing> findByAppointmentId(Long appointmentId);

    List<Billing> findByPatientId(Long patientId);

    List<Billing> findByDoctorId(Long doctorId);
}
