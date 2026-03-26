package com.example.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.backend.exception.BadRequestException;
import com.example.backend.dto.DoctorAvailabilityResponse;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Appointment;
import com.example.backend.model.AppointmentStatus;
import com.example.backend.model.Billing;
import com.example.backend.model.Doctor;
import com.example.backend.model.PaymentStatus;
import com.example.backend.repository.AppointmentRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final BillingService billingService;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientService patientService,
            DoctorService doctorService,
            BillingService billingService) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.billingService = billingService;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment createAppointment(Appointment appointment) {
        patientService.getPatientById(appointment.getPatientId());
        Doctor doctor = doctorService.getDoctorById(appointment.getDoctorId());
        validateAppointmentSlot(doctor, appointment.getAppointmentDate(), appointment.getAppointmentTime());
        appointment.setStatus(AppointmentStatus.PENDING);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        billingService.createBillingForAppointment(savedAppointment);
        return savedAppointment;
    }

    public DoctorAvailabilityResponse getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        List<String> availableSlots = getAvailableSlots(doctor, date);
        return new DoctorAvailabilityResponse(doctorId, date, availableSlots);
    }

    public Appointment updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        Billing billing = billingService.getBillingByAppointmentId(appointment.getId());

        if (status == AppointmentStatus.APPROVED && billing.getPaymentStatus() != PaymentStatus.PAID) {
            throw new BadRequestException("Appointment cannot be approved until payment is completed");
        }

        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        billingService.deleteBillingByAppointmentId(id);
        appointmentRepository.delete(appointment);
    }

    private void validateAppointmentSlot(Doctor doctor, LocalDate appointmentDate, String appointmentTime) {
        String normalizedTime = appointmentTime == null ? "" : appointmentTime.trim();
        List<String> availableSlots = getAvailableSlots(doctor, appointmentDate);

        if (!availableSlots.contains(normalizedTime)) {
            throw new BadRequestException("Selected appointment slot is not available for this doctor");
        }
    }

    private List<String> getAvailableSlots(Doctor doctor, LocalDate appointmentDate) {
        List<String> configuredSlots = parseConfiguredSlots(doctor.getAvailabilitySlots());
        Set<String> bookedSlots = appointmentRepository.findByDoctorIdAndAppointmentDate(doctor.getId(), appointmentDate)
                .stream()
                .filter(appointment -> appointment.getStatus() != AppointmentStatus.REJECTED)
                .map(Appointment::getAppointmentTime)
                .map(String::trim)
                .collect(Collectors.toSet());

        return configuredSlots.stream()
                .map(String::trim)
                .filter(slot -> !bookedSlots.contains(slot))
                .toList();
    }

    private List<String> parseConfiguredSlots(String availabilitySlots) {
        if (availabilitySlots == null || availabilitySlots.isBlank()) {
            return List.of();
        }

        return List.of(availabilitySlots.split(","))
                .stream()
                .map(String::trim)
                .filter(slot -> !slot.isBlank())
                .toList();
    }
}
