-- Sunrise Dental Clinic Management System
-- Database schema (MySQL 8)
-- Run this file first, then seed_data.sql

DROP DATABASE IF EXISTS sunrise_clinic;
CREATE DATABASE sunrise_clinic CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE sunrise_clinic;


-- staff members who can log in to the system
CREATE TABLE users (
    user_id         INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password_hash   VARCHAR(64)  NOT NULL,           -- SHA-256 hex value
    salt            VARCHAR(32)  NOT NULL,           -- random salt for the hash
    full_name       VARCHAR(100) NOT NULL,
    role            ENUM('ADMIN','RECEPTIONIST') NOT NULL DEFAULT 'RECEPTIONIST',
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    failed_attempts INT          NOT NULL DEFAULT 0, -- account locks after 3
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- patient details, entered when a new patient comes to the clinic
CREATE TABLE patients (
    patient_id     INT AUTO_INCREMENT PRIMARY KEY,
    patient_name   VARCHAR(100) NOT NULL,
    address        VARCHAR(150) NOT NULL,
    contact_number VARCHAR(10)  NOT NULL,
    nic            VARCHAR(12),
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_patient_contact ON patients (contact_number);


-- dentists working in the clinic
CREATE TABLE dentists (
    dentist_id       INT AUTO_INCREMENT PRIMARY KEY,
    dentist_name     VARCHAR(100)   NOT NULL,
    specialization   VARCHAR(60)    NOT NULL,
    consultation_fee DECIMAL(10, 2) NOT NULL,
    is_available     TINYINT(1)     NOT NULL DEFAULT 1
);


-- treatment types with the standard price
CREATE TABLE treatments (
    treatment_id     INT AUTO_INCREMENT PRIMARY KEY,
    treatment_name   VARCHAR(80)    NOT NULL UNIQUE,
    base_cost        DECIMAL(10, 2) NOT NULL,
    duration_minutes INT            NOT NULL DEFAULT 30
);


-- one row for every patient visit
CREATE TABLE appointments (
    appointment_id   INT AUTO_INCREMENT PRIMARY KEY,
    appointment_no   VARCHAR(20) NOT NULL UNIQUE,     -- example APT-20260824-001
    patient_id       INT         NOT NULL,
    dentist_id       INT         NOT NULL,
    treatment_id     INT         NOT NULL,
    appointment_date DATE        NOT NULL,
    appointment_time TIME        NOT NULL,
    status           ENUM('BOOKED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'BOOKED',
    created_by       INT,                             -- which staff member entered it
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_app_patient   FOREIGN KEY (patient_id)   REFERENCES patients (patient_id),
    CONSTRAINT fk_app_dentist   FOREIGN KEY (dentist_id)   REFERENCES dentists (dentist_id),
    CONSTRAINT fk_app_treatment FOREIGN KEY (treatment_id) REFERENCES treatments (treatment_id),
    CONSTRAINT fk_app_user      FOREIGN KEY (created_by)   REFERENCES users (user_id),

    -- this line stops double booking:
    -- the same dentist cannot have two appointments at the same date and time
    CONSTRAINT uq_dentist_slot UNIQUE (dentist_id, appointment_date, appointment_time)
);

CREATE INDEX idx_appointment_date ON appointments (appointment_date);


-- bill created after the treatment
CREATE TABLE bills (
    bill_id          INT AUTO_INCREMENT PRIMARY KEY,
    appointment_id   INT            NOT NULL UNIQUE,   -- one bill per appointment
    treatment_cost   DECIMAL(10, 2) NOT NULL,
    consultation_fee DECIMAL(10, 2) NOT NULL,
    discount         DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    tax              DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    total_amount     DECIMAL(10, 2) NOT NULL,
    payment_method   ENUM('CASH','CARD') NOT NULL DEFAULT 'CASH',
    billed_by        INT,
    billed_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bill_appointment FOREIGN KEY (appointment_id) REFERENCES appointments (appointment_id),
    CONSTRAINT fk_bill_user        FOREIGN KEY (billed_by)      REFERENCES users (user_id)
);
