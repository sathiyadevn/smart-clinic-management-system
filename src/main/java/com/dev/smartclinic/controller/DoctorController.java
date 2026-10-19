package com.dev.smartclinic.controller;

import com.dev.smartclinic.entity.Doctor;
import com.dev.smartclinic.service.DoctorService;
import com.dev.smartclinic.service.TokenService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService doctorService;
    private final TokenService tokenService;

    public DoctorController(DoctorService doctorService, TokenService tokenService) {
        this.doctorService = doctorService;
        this.tokenService = tokenService;
    }

    private boolean isAuthorized(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;
        return tokenService.validateToken(authHeader.substring(7));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        Map<String, Object> result = doctorService.validateDoctorLogin(
                credentials.get("email"), credentials.get("password"));
        return (Boolean) result.get("success") ? ResponseEntity.ok(result)
                : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }

    @PostMapping
    public ResponseEntity<?> addDoctor(@RequestBody Doctor doctor,
                                       @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized access"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.saveDoctor(doctor));
    }

    /**
     * Exposes GET endpoint to fetch all doctors.
     * Required for Question 24 criteria.
     */
    @GetMapping
    public ResponseEntity<?> getAllDoctors(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized token"));
        }
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    /**
     * Exposes dynamic GET endpoint for doctor availability on a given date with token validation.
     * Required for Question 5 criteria.
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<?> getDoctorAvailability(
            @PathVariable("id") Long doctorId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid or missing token"));
        }

        List<String> availableSlots = doctorService.getAvailableSlots(doctorId, date);
        Map<String, Object> response = new HashMap<>();
        response.put("doctorId", doctorId);
        response.put("date", date);
        response.put("availableSlots", availableSlots);

        return ResponseEntity.ok(response);
    }

    /**
     * Exposes GET endpoint filtering doctors by speciality and time.
     * Required for Question 26 criteria.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchDoctors(
            @RequestParam("speciality") String speciality,
            @RequestParam(value = "time", required = false) String time,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (!isAuthorized(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Unauthorized token"));
        }
        return ResponseEntity.ok(doctorService.searchDoctors(speciality, time));
    }
}