package com.example.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.exception.BadRequestException;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.model.Appointment;
import com.example.backend.model.AppointmentStatus;
import com.example.backend.model.Billing;
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
        doctorService.getDoctorById(appointment.getDoctorId());
        appointment.setStatus(AppointmentStatus.PENDING);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        billingService.createBillingForAppointment(savedAppointment);
        return savedAppointment;
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
}
