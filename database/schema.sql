-- Easy-Q Database Schema
-- Digital Queue & Appointment Manager

CREATE DATABASE IF NOT EXISTS easyq_db;
USE easyq_db;

-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    role ENUM('ADMIN', 'STAFF', 'CUSTOMER') DEFAULT 'CUSTOMER',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Services table
CREATE TABLE services (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    duration_minutes INT NOT NULL DEFAULT 30,
    price DECIMAL(10,2),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Appointments table
CREATE TABLE appointments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('SCHEDULED', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED', 'NO_SHOW') DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE,
    UNIQUE KEY unique_appointment (appointment_date, appointment_time)
);

-- Queue entries table
CREATE TABLE queue_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    queue_number INT NOT NULL,
    status ENUM('WAITING', 'CALLED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'WAITING',
    estimated_wait_time INT, -- in minutes
    called_at TIMESTAMP NULL,
    started_at TIMESTAMP NULL,
    completed_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
);

-- Notifications table
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type ENUM('APPOINTMENT_REMINDER', 'QUEUE_UPDATE', 'APPOINTMENT_CONFIRMATION', 'QUEUE_CALLED') NOT NULL,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    is_sent BOOLEAN DEFAULT FALSE,
    sent_at TIMESTAMP NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Analytics table for admin dashboard
CREATE TABLE analytics_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    event_data JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert seed data

-- Admin user
INSERT INTO users (username, email, password, first_name, last_name, role) VALUES
('admin', 'admin@easyq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Admin', 'User', 'ADMIN');

-- Staff users
INSERT INTO users (username, email, password, first_name, last_name, role) VALUES
('staff1', 'staff1@easyq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'John', 'Doe', 'STAFF'),
('staff2', 'staff2@easyq.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Jane', 'Smith', 'STAFF');

-- Customer users
INSERT INTO users (username, email, password, first_name, last_name, phone) VALUES
('customer1', 'customer1@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Alice', 'Johnson', '+1234567890'),
('customer2', 'customer2@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Bob', 'Wilson', '+1234567891'),
('customer3', 'customer3@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'Carol', 'Brown', '+1234567892');

-- Services
INSERT INTO services (name, description, duration_minutes, price) VALUES
('General Consultation', 'General health consultation with doctor', 30, 100.00),
('Dental Checkup', 'Regular dental examination and cleaning', 45, 150.00),
('Eye Examination', 'Comprehensive eye examination', 20, 80.00),
('Blood Test', 'Complete blood count and basic tests', 15, 50.00),
('X-Ray', 'Chest X-ray examination', 10, 75.00);

-- Sample appointments
INSERT INTO appointments (user_id, service_id, appointment_date, appointment_time, status) VALUES
(4, 1, '2024-01-15', '09:00:00', 'SCHEDULED'),
(5, 2, '2024-01-15', '10:30:00', 'CONFIRMED'),
(6, 3, '2024-01-15', '14:00:00', 'COMPLETED'),
(4, 4, '2024-01-16', '08:00:00', 'SCHEDULED'),
(5, 5, '2024-01-16', '11:00:00', 'CANCELLED');

-- Sample queue entries
INSERT INTO queue_entries (user_id, service_id, queue_number, status, estimated_wait_time) VALUES
(4, 1, 1, 'WAITING', 15),
(5, 2, 2, 'WAITING', 30),
(6, 3, 3, 'CALLED', 0),
(4, 4, 4, 'IN_PROGRESS', 0),
(5, 5, 5, 'COMPLETED', 0);

-- Sample notifications
INSERT INTO notifications (user_id, type, title, message, is_sent) VALUES
(4, 'APPOINTMENT_REMINDER', 'Appointment Reminder', 'Your appointment is scheduled for tomorrow at 9:00 AM', TRUE),
(5, 'QUEUE_UPDATE', 'Queue Update', 'Your queue number is 2. Estimated wait time: 30 minutes', TRUE),
(6, 'QUEUE_CALLED', 'You are being called', 'Please proceed to the service counter', FALSE);

-- Sample analytics events
INSERT INTO analytics_events (event_type, event_data) VALUES
('appointment_created', '{"service_id": 1, "date": "2024-01-15"}'),
('queue_joined', '{"service_id": 2, "queue_number": 2}'),
('appointment_completed', '{"service_id": 3, "duration": 20}'),
('user_registered', '{"role": "CUSTOMER"}'),
('appointment_cancelled', '{"service_id": 5, "reason": "user_request"}');
