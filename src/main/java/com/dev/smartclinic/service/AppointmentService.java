package com.dev.smartclinic.service;

import com.dev.smartclinic.entity.Appointment;
import com.dev.smartclinic.entity.Doctor;
import com.dev.smartclinic.entity.Patient;
import com.dev.smartclinic.repository.AppointmentRepository;
import com.dev.smartclinic.repository.DoctorRepository;
import com.dev.smartclinic.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    /**
     * Implements booking method that persists an appointment.
     * Required for Question 6 criteria.
     */
    @Transactional
    public Appointment bookAppointment(Long doctorId, Long patientId, LocalDateTime appointmentTime) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found with ID: " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Patient not found with ID: " + patientId));

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus("CONFIRMED");

        return appointmentRepository.save(appointment);
    }

    /**
     * Retrieves all appointments for a specific doctor on a target date.
     * Required for Question 6 criteria.
     */
    public List<Appointment> getAppointmentsForDoctorOnDate(Long doctorId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }
}