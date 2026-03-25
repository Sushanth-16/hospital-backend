package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Patient;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
