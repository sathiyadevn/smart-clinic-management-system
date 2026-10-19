# Smart Clinic Management System - Database Schema Design

This document details the relational MySQL database schema designed for the Smart Clinic Management System. The design includes primary keys, foreign key constraints, and specific data types.

## 1. Patients Table (`patients`)
Stores registered patient profile information and login credentials.
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT): Unique patient identifier.
- `name` (VARCHAR(100), NOT NULL): Full name of the patient.
- `email` (VARCHAR(100), UNIQUE, NOT NULL): Primary contact and login email.
- `phone_number` (VARCHAR(15), NOT NULL): Contact phone number.
- `password` (VARCHAR(255), NOT NULL): Encrypted user authentication password.
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Registration timestamp.

## 2. Doctors Table (`doctors`)
Stores medical practitioner profiles and specialities.
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT): Unique doctor identifier.
- `name` (VARCHAR(100), NOT NULL): Full doctor name with title.
- `email` (VARCHAR(100), UNIQUE, NOT NULL): Professional email address.
- `password` (VARCHAR(255), NOT NULL): Doctor portal authentication password.
- `speciality` (VARCHAR(100), NOT NULL): Clinical speciality (e.g., Cardiology, Dermatology).
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Profile creation timestamp.

## 3. Doctor Available Times Table (`doctor_available_times`)
Maps available consultation time slots to doctors.
- `doctor_id` (BIGINT, NOT NULL): Foreign Key referencing `doctors(id)` with ON DELETE CASCADE.
- `available_time` (VARCHAR(20), NOT NULL): Scheduled time slot (e.g., '09:00', '10:00').

## 4. Appointments Table (`appointments`)
Captures consultation bookings linking patients and doctors.
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT): Unique appointment tracking ID.
- `doctor_id` (BIGINT, NOT NULL): Foreign Key referencing `doctors(id)`.
- `patient_id` (BIGINT, NOT NULL): Foreign Key referencing `patients(id)`.
- `appointment_time` (DATETIME, NOT NULL): Scheduled date and time of the visit.
- `status` (VARCHAR(20), DEFAULT 'SCHEDULED'): Appointment lifecycle state (e.g., 'SCHEDULED', 'CONFIRMED', 'COMPLETED').

## 5. Prescriptions Table (`prescriptions`)
Stores digital medical prescriptions associated with completed consultations.
- `id` (BIGINT, PRIMARY KEY, AUTO_INCREMENT): Unique prescription ID.
- `appointment_id` (BIGINT, NOT NULL): Foreign Key referencing `appointments(id)`.
- `medication` (TEXT, NOT NULL): Prescribed medicines, dosages, and schedules.
- `notes` (TEXT): Clinical advice or follow-up instructions.
- `created_at` (TIMESTAMP, DEFAULT CURRENT_TIMESTAMP): Issuance timestamp.