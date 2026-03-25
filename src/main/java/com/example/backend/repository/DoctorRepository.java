package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.backend.model.Doctor;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
