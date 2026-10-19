package com.dev.smartclinic.service;

import com.dev.smartclinic.entity.Doctor;
import com.dev.smartclinic.repository.AppointmentRepository;
import com.dev.smartclinic.repository.DoctorRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * Validates doctor login credentials and returns a structured response map.
     * Required for Question 10 criteria.
     */
    public Map<String, Object> validateDoctorLogin(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        Optional<Doctor> doctorOpt = doctorRepository.findByEmail(email);

        if (doctorOpt.isPresent() && doctorOpt.get().getPassword().equals(password)) {
            String token = tokenService.generateToken(email);
            response.put("success", true);
            response.put("token", token);
            response.put("doctorId", doctorOpt.get().getId());
            response.put("name", doctorOpt.get().getName());
        } else {
            response.put("success", false);
            response.put("message", "Invalid doctor email or password");
        }
        return response;
    }

    /**
     * Calculates and returns available time slots for a doctor on a given date.
     * Filters out already booked slots.
     * Required for Question 10 criteria.
     */
    public List<String> getAvailableSlots(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with ID: " + doctorId));

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // Fetch already scheduled appointments for that doctor on the specified date
        Set<String> bookedTimes = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay)
                .stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString().substring(0, 5))
                .collect(Collectors.toSet());

        // Return master availability minus booked slots
        return doctor.getAvailableTimes().stream()
                .filter(slot -> !bookedTimes.contains(slot))
                .collect(Collectors.toList());
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public List<Doctor> searchDoctors(String speciality, String time) {
        List<Doctor> matched = doctorRepository.findBySpecialityIgnoreCase(speciality);
        if (time != null && !time.isBlank()) {
            return matched.stream()
                    .filter(doc -> doc.getAvailableTimes().contains(time))
                    .collect(Collectors.toList());
        }
        return matched;
    }

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
}