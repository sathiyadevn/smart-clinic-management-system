package com.dev.smartclinic.controller;

import com.dev.smartclinic.entity.Appointment;
import com.dev.smartclinic.entity.Prescription;
import com.dev.smartclinic.repository.AppointmentRepository;
import com.dev.smartclinic.repository.PrescriptionRepository;
import com.dev.smartclinic.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    private final PrescriptionRepository prescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PrescriptionController(PrescriptionRepository prescriptionRepository,
                                  AppointmentRepository appointmentRepository,
                                  TokenService tokenService) {
        this.prescriptionRepository = prescriptionRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    /**
     * POST endpoint to save prescription with token and request body validation.
     * Returns structured success or error messages using ResponseEntity.
     * Required for Question 7 criteria.
     */
    @PostMapping
    public ResponseEntity<?> savePrescription(
            @Valid @RequestBody Map<String, Object> payload,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        Map<String, Object> response = new HashMap<>();

        if (authHeader == null || !authHeader.startsWith("Bearer ") ||
                !tokenService.validateToken(authHeader.substring(7))) {
            response.put("success", false);
            response.put("message", "Invalid or expired JWT token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            if (!payload.containsKey("appointmentId") || !payload.containsKey("medication")) {
                response.put("success", false);
                response.put("message", "Missing required payload fields: appointmentId or medication");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Long appointmentId = Long.valueOf(payload.get("appointmentId").toString());
            String medication = (String) payload.get("medication");
            String notes = payload.containsKey("notes") ? (String) payload.get("notes") : "";

            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Appointment not found with ID: " + appointmentId));

            Prescription prescription = new Prescription();
            prescription.setAppointment(appointment);
            prescription.setMedication(medication);
            prescription.setNotes(notes);

            Prescription saved = prescriptionRepository.save(prescription);

            response.put("success", true);
            response.put("prescriptionId", saved.getId());
            response.put("message", "Prescription saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception ex) {
            response.put("success", false);
            response.put("message", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}