package com.dev.smartclinic.controller;

import com.dev.smartclinic.entity.Patient;
import com.dev.smartclinic.repository.PatientRepository;
import com.dev.smartclinic.service.AppointmentService;
import com.dev.smartclinic.service.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientRepository patientRepository;
    private final AppointmentService appointmentService;
    private final TokenService tokenService;

    public PatientController(PatientRepository patientRepository,
                             AppointmentService appointmentService,
                             TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentService = appointmentService;
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> creds) {
        Optional<Patient> p = patientRepository.findByEmail(creds.get("email"));
        if (p.isPresent() && p.get().getPassword().equals(creds.get("password"))) {
            String token = tokenService.generateToken(p.get().getEmail());
            return ResponseEntity.ok(Map.of("success", true, "token", token, "patientId", p.get().getId(), "name", p.get().getName()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid patient credentials"));
    }

    /**
     * Retrieves all appointments booked by a patient using their JWT token.
     * Required for Question 25 criteria.
     */
    @GetMapping("/appointments")
    public ResponseEntity<?> getMyAppointments(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ") || !tokenService.validateToken(authHeader.substring(7))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized access"));
        }
        String email = tokenService.extractEmail(authHeader.substring(7));
        Patient patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patient.getId()));
    }
}