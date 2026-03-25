package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Appointment;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
