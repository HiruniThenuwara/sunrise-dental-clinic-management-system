-- Sunrise Dental Clinic Management System
-- Sample data. Run this after schema.sql

USE sunrise_clinic;


-- staff logins
-- password_hash = SHA-256 of (salt + password), same way PasswordUtil does it in Java
-- admin   / admin123
-- kamal   / kamal123
-- nimali  / nimali123
INSERT INTO users (username, password_hash, salt, full_name, role) VALUES
('admin',  SHA2(CONCAT('K7dQ2mZp', 'admin123'),  256), 'K7dQ2mZp', 'System Administrator', 'ADMIN'),
('kamal',  SHA2(CONCAT('X3vB9tLr', 'kamal123'),  256), 'X3vB9tLr', 'Kamal Silva',          'RECEPTIONIST'),
('nimali', SHA2(CONCAT('Q8nW4hYd', 'nimali123'), 256), 'Q8nW4hYd', 'Nimali Perera',        'RECEPTIONIST');


-- dentists of the clinic (fees in LKR)
INSERT INTO dentists (dentist_name, specialization, consultation_fee) VALUES
('Dr. Saman Perera',       'General Dentistry',    1500.00),
('Dr. Anusha Fernando',    'Orthodontics',         2500.00),
('Dr. Roshan De Silva',    'Oral Surgery',         3000.00),
('Dr. Malini Jayawardena', 'Pediatric Dentistry',  2000.00);


-- treatment price list
INSERT INTO treatments (treatment_name, base_cost, duration_minutes) VALUES
('Dental Check-up',       1000.00, 15),
('Scaling and Cleaning',  3500.00, 45),
('Tooth Filling',         4500.00, 40),
('Tooth Extraction',      5000.00, 30),
('Root Canal Treatment', 15000.00, 90),
('Teeth Whitening',      12000.00, 60),
('Braces Consultation',   8000.00, 30),
('Dental X-Ray',          2000.00, 15);


-- a few patients so the system is not empty at the demonstration
INSERT INTO patients (patient_name, address, contact_number, nic) VALUES
('Sunil Bandara',   'No 45, Galle Road, Colombo 03',   '0771234567', '199012345678'),
('Chamari Wijesin', 'No 12, Temple Lane, Nugegoda',    '0712345678', '199523456789'),
('Ruwan Jayasuriya','No 8, Flower Road, Colombo 07',   '0765554433', '198834567890');


-- sample appointments
INSERT INTO appointments (appointment_no, patient_id, dentist_id, treatment_id,
                          appointment_date, appointment_time, status, created_by) VALUES
('APT-20260824-001', 1, 1, 2, '2026-08-24', '09:00:00', 'COMPLETED', 2),
('APT-20260824-002', 2, 2, 7, '2026-08-24', '10:30:00', 'BOOKED',    2),
('APT-20260825-001', 3, 3, 4, '2026-08-25', '14:00:00', 'BOOKED',    3);


-- bill for the completed appointment
-- 3500 treatment + 1500 consultation, no discount, 2% tax
INSERT INTO bills (appointment_id, treatment_cost, consultation_fee, discount, tax,
                   total_amount, payment_method, billed_by) VALUES
(1, 3500.00, 1500.00, 0.00, 100.00, 5100.00, 'CASH', 2);
