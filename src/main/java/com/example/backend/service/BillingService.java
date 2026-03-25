package com.example.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.backend.model.Appointment;
import com.example.backend.model.Billing;
import com.example.backend.model.Doctor;
import com.example.backend.model.PaymentStatus;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.BillingRepository;

@Service
public class BillingService {

    private final BillingRepository billingRepository;
    private final DoctorService doctorService;

    public BillingService(BillingRepository billingRepository, DoctorService doctorService) {
        this.billingRepository = billingRepository;
        this.doctorService = doctorService;
    }

    public List<Billing> getAllBillings() {
        return billingRepository.findAll();
    }

    public List<Billing> getBillingsByPatientId(Long patientId) {
        return billingRepository.findByPatientId(patientId);
    }

    public List<Billing> getBillingsByDoctorId(Long doctorId) {
        return billingRepository.findByDoctorId(doctorId);
    }

    public Billing createBillingForAppointment(Appointment appointment) {
        return billingRepository.findByAppointmentId(appointment.getId())
                .orElseGet(() -> {
                    Doctor doctor = doctorService.getDoctorById(appointment.getDoctorId());
                    Billing billing = new Billing();
                    billing.setAppointmentId(appointment.getId());
                    billing.setPatientId(appointment.getPatientId());
                    billing.setDoctorId(appointment.getDoctorId());
                    billing.setAmount(doctor.getConsultationFee());
                    billing.setBillingDate(LocalDate.now());
                    billing.setPaymentStatus(PaymentStatus.UNPAID);
                    return billingRepository.save(billing);
                });
    }

    public Billing getBillingByAppointmentId(Long appointmentId) {
        return billingRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found for appointment id: " + appointmentId));
    }

    public Billing updatePaymentStatus(Long billingId, PaymentStatus paymentStatus) {
        Billing billing = billingRepository.findById(billingId)
                .orElseThrow(() -> new ResourceNotFoundException("Billing not found with id: " + billingId));
        billing.setPaymentStatus(paymentStatus);
        return billingRepository.save(billing);
    }

    public void deleteBillingByAppointmentId(Long appointmentId) {
        billingRepository.findByAppointmentId(appointmentId)
                .ifPresent(billingRepository::delete);
    }
}
