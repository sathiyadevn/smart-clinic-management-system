CREATE DATABASE IF NOT EXISTS smart_clinic_db;
USE smart_clinic_db;

-- 1. Patients Table
CREATE TABLE IF NOT EXISTS patients (
                                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone_number VARCHAR(15) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 2. Doctors Table
CREATE TABLE IF NOT EXISTS doctors (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    speciality VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- 3. Doctor Available Times (Element Collection mapping)
CREATE TABLE IF NOT EXISTS doctor_available_times (
                                                      doctor_id BIGINT NOT NULL,
                                                      available_time VARCHAR(20) NOT NULL,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
    );

-- 4. Appointments Table
CREATE TABLE IF NOT EXISTS appointments (
                                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                            doctor_id BIGINT NOT NULL,
                                            patient_id BIGINT NOT NULL,
                                            appointment_time DATETIME NOT NULL,
                                            status VARCHAR(20) DEFAULT 'SCHEDULED',
    FOREIGN KEY (doctor_id) REFERENCES doctors(id),
    FOREIGN KEY (patient_id) REFERENCES patients(id)
    );

-- 5. Prescriptions Table
CREATE TABLE IF NOT EXISTS prescriptions (
                                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                             appointment_id BIGINT NOT NULL,
                                             medication TEXT NOT NULL,
                                             notes TEXT,
                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                             FOREIGN KEY (appointment_id) REFERENCES appointments(id)
    );

-- Insert Sample Seed Records
INSERT INTO patients (name, email, phone_number, password) VALUES
                                                               ('Karthik Raja', 'karthik@example.com', '9876543210', 'pass123'),
                                                               ('Priya Sharma', 'priya@example.com', '9876543211', 'pass123'),
                                                               ('Anand Kumar', 'anand@example.com', '9876543212', 'pass123'),
                                                               ('Sneha Patel', 'sneha@example.com', '9876543213', 'pass123'),
                                                               ('Vikram Seth', 'vikram@example.com', '9876543214', 'pass123');

INSERT INTO doctors (name, email, password, speciality) VALUES
                                                            ('Dr. Ramesh Babu', 'ramesh@clinic.com', 'doc123', 'Cardiology'),
                                                            ('Dr. Shalini Rao', 'shalini@clinic.com', 'doc123', 'Dermatology');

INSERT INTO doctor_available_times (doctor_id, available_time) VALUES
                                                                   (1, '09:00'), (1, '10:00'), (1, '11:00'),
                                                                   (2, '14:00'), (2, '15:00');

-- 6. Stored Procedures
DROP PROCEDURE IF EXISTS GetDailyAppointmentReportByDoctor;
DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByMonth;
DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByYear;

DELIMITER //

CREATE PROCEDURE GetDailyAppointmentReportByDoctor(IN docId BIGINT, IN reportDate DATE)
BEGIN
SELECT a.id AS appointment_id, p.name AS patient_name, a.appointment_time, a.status
FROM appointments a
         JOIN patients p ON a.patient_id = p.id
WHERE a.doctor_id = docId AND DATE(a.appointment_time) = reportDate;
END //

CREATE PROCEDURE GetDoctorWithMostPatientsByMonth(IN reqMonth INT, IN reqYear INT)
BEGIN
SELECT d.id AS doctor_id, d.name AS doctor_name, COUNT(a.id) AS patient_count
FROM appointments a
         JOIN doctors d ON a.doctor_id = d.id
WHERE MONTH(a.appointment_time) = reqMonth AND YEAR(a.appointment_time) = reqYear
GROUP BY d.id, d.name
ORDER BY patient_count DESC
    LIMIT 1;
END //

CREATE PROCEDURE GetDoctorWithMostPatientsByYear(IN reqYear INT)
BEGIN
SELECT d.id AS doctor_id, d.name AS doctor_name, COUNT(a.id) AS patient_count
FROM appointments a
         JOIN doctors d ON a.doctor_id = d.id
WHERE YEAR(a.appointment_time) = reqYear
GROUP BY d.id, d.name
ORDER BY patient_count DESC
    LIMIT 1;
END //

DELIMITER ;