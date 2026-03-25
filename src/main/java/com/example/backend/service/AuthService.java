package com.example.backend.service;

import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.dto.AuthRequest;
import com.example.backend.dto.AuthResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.dto.UserResponse;
import com.example.backend.exception.AuthenticationException;
import com.example.backend.model.AppUser;
import com.example.backend.model.Doctor;
import com.example.backend.model.Patient;
import com.example.backend.model.UserRole;
import com.example.backend.repository.DoctorRepository;
import com.example.backend.repository.PatientRepository;
import com.example.backend.repository.AppUserRepository;

@Service
public class AuthService {

    private static final int DEFAULT_CONSULTATION_FEE = 800;

    private final AppUserRepository appUserRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(
            AppUserRepository appUserRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {
        this.appUserRepository = appUserRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public AuthResponse register(RegisterRequest request) {
        if (appUserRepository.existsByUsername(request.getUsername())) {
            throw new AuthenticationException("Username already exists");
        }

        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new AuthenticationException("Email already exists");
        }

        AppUser user = new AppUser();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setReferenceId(createReferenceRecord(request));

        AppUser savedUser = appUserRepository.save(user);
        return new AuthResponse(generateToken(), toUserResponse(savedUser));
    }

    public AuthResponse login(AuthRequest request) {
        AppUser user = appUserRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthenticationException("Invalid username or password"));

        if (user.getRole() != request.getRole()) {
            throw new AuthenticationException("Selected role does not match this account");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid username or password");
        }

        return new AuthResponse(generateToken(), toUserResponse(user));
    }

    private UserResponse toUserResponse(AppUser user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                user.getRole(),
                user.getReferenceId());
    }

    private Long createReferenceRecord(RegisterRequest request) {
        if (request.getRole() == UserRole.PATIENT) {
            Patient patient = new Patient();
            patient.setName(request.getName());
            patient.setAge(request.getAge() == null ? 0 : request.getAge());
            patient.setGender(defaultIfBlank(request.getGender(), "Not specified"));
            patient.setPhone(defaultIfBlank(request.getPhone(), "Not provided"));
            patient.setDisease(defaultIfBlank(request.getDisease(), "General Medicine"));
            return patientRepository.save(patient).getId();
        }

        if (request.getRole() == UserRole.DOCTOR) {
            validateDoctorRegistration(request);
            Doctor doctor = new Doctor();
            doctor.setName(request.getName());
            doctor.setSpecialization(request.getSpecialization());
            doctor.setPhone(request.getPhone());
            doctor.setEmail(request.getEmail());
            doctor.setConsultationFee(DEFAULT_CONSULTATION_FEE);
            return doctorRepository.save(doctor).getId();
        }

        return null;
    }

    private void validateDoctorRegistration(RegisterRequest request) {
        if (isBlank(request.getPhone()) || isBlank(request.getSpecialization())) {
            throw new AuthenticationException("Doctor registration requires phone and specialization");
        }
    }

    private String defaultIfBlank(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
