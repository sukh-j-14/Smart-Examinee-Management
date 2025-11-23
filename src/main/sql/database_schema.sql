-- =====================================================
-- Smart Examinee Management System (SEMS)
-- Database Schema
-- =====================================================

-- Create database (uncomment if needed)
-- CREATE DATABASE IF NOT EXISTS sems_db;
-- USE sems_db;

-- =====================================================
-- Table: users
-- Stores admin and staff account information
-- =====================================================
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('admin', 'staff') NOT NULL DEFAULT 'staff',
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: examinees
-- Stores examinee (student) information
-- =====================================================
CREATE TABLE IF NOT EXISTS examinees (
    examinee_id INT AUTO_INCREMENT PRIMARY KEY,
    registration_number VARCHAR(50) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    pincode VARCHAR(10),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_registration_number (registration_number),
    INDEX idx_email (email),
    INDEX idx_name (first_name, last_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: exams
-- Stores exam information
-- =====================================================
CREATE TABLE IF NOT EXISTS exams (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_name VARCHAR(200) NOT NULL,
    exam_code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    exam_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    duration_minutes INT,
    venue VARCHAR(200),
    max_capacity INT DEFAULT 100,
    current_registrations INT DEFAULT 0,
    status ENUM('scheduled', 'ongoing', 'completed', 'cancelled') DEFAULT 'scheduled',
    created_by INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    INDEX idx_exam_code (exam_code),
    INDEX idx_exam_date (exam_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: exam_registrations
-- Links examinees to exams (many-to-many relationship)
-- =====================================================
CREATE TABLE IF NOT EXISTS exam_registrations (
    registration_id INT AUTO_INCREMENT PRIMARY KEY,
    examinee_id INT NOT NULL,
    exam_id INT NOT NULL,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('registered', 'confirmed', 'cancelled') DEFAULT 'registered',
    hall_ticket_number VARCHAR(50) UNIQUE,
    remarks TEXT,
    FOREIGN KEY (examinee_id) REFERENCES examinees(examinee_id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES exams(exam_id) ON DELETE CASCADE,
    UNIQUE KEY unique_examinee_exam (examinee_id, exam_id),
    INDEX idx_examinee_id (examinee_id),
    INDEX idx_exam_id (exam_id),
    INDEX idx_hall_ticket (hall_ticket_number),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Table: results
-- Stores exam results
-- =====================================================
CREATE TABLE IF NOT EXISTS results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    registration_id INT NOT NULL,
    marks_obtained DECIMAL(5,2) NOT NULL,
    max_marks DECIMAL(5,2) NOT NULL,
    percentage DECIMAL(5,2) NOT NULL,
    grade VARCHAR(10),
    remarks TEXT,
    entered_by INT NOT NULL,
    entered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (registration_id) REFERENCES exam_registrations(registration_id) ON DELETE CASCADE,
    FOREIGN KEY (entered_by) REFERENCES users(user_id) ON DELETE RESTRICT,
    UNIQUE KEY unique_registration_result (registration_id),
    INDEX idx_registration_id (registration_id),
    INDEX idx_grade (grade),
    CHECK (marks_obtained >= 0 AND marks_obtained <= max_marks),
    CHECK (percentage >= 0 AND percentage <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- Sample Data Insertion
-- =====================================================

-- Insert sample admin user
-- Password: admin123 (plain text for development)
INSERT INTO users (username, password, role, full_name, email, phone, is_active) 
VALUES ('admin', 'admin123', 'admin', 'System Administrator', 'admin@sems.local', '1234567890', TRUE);

-- Optional: Insert sample staff user
INSERT INTO users (username, password, role, full_name, email, phone, is_active) 
VALUES ('staff1', 'staff123', 'staff', 'John Doe', 'staff1@sems.local', '9876543210', TRUE);

-- Optional: Insert sample examinee
INSERT INTO examinees (registration_number, first_name, last_name, email, phone, date_of_birth, address, city, state, pincode)
VALUES ('REG001', 'Alice', 'Smith', 'alice.smith@example.com', '5551234567', '2000-05-15', '123 Main Street', 'Mumbai', 'Maharashtra', '400001');

-- Optional: Insert sample exam
INSERT INTO exams (exam_name, exam_code, description, exam_date, start_time, end_time, duration_minutes, venue, max_capacity, created_by, status)
VALUES ('Final Examination 2024', 'FINAL2024', 'Annual Final Examination for all students', '2024-12-20', '10:00:00', '13:00:00', 180, 'Main Hall', 200, 1, 'scheduled');

-- =====================================================
-- Views (Optional - for convenience)
-- =====================================================

-- View: Exam registration details with examinee and exam info
CREATE OR REPLACE VIEW vw_exam_registrations AS
SELECT 
    er.registration_id,
    er.registration_date,
    er.status AS registration_status,
    er.hall_ticket_number,
    e.examinee_id,
    e.registration_number AS examinee_reg_number,
    CONCAT(e.first_name, ' ', e.last_name) AS examinee_name,
    e.email AS examinee_email,
    e.phone AS examinee_phone,
    ex.exam_id,
    ex.exam_name,
    ex.exam_code,
    ex.exam_date,
    ex.start_time,
    ex.end_time,
    ex.venue,
    ex.status AS exam_status
FROM exam_registrations er
INNER JOIN examinees e ON er.examinee_id = e.examinee_id
INNER JOIN exams ex ON er.exam_id = ex.exam_id;

-- View: Results with full details
CREATE OR REPLACE VIEW vw_results AS
SELECT 
    r.result_id,
    r.marks_obtained,
    r.max_marks,
    r.percentage,
    r.grade,
    r.remarks,
    r.entered_at,
    er.registration_id,
    er.hall_ticket_number,
    e.examinee_id,
    e.registration_number AS examinee_reg_number,
    CONCAT(e.first_name, ' ', e.last_name) AS examinee_name,
    e.email AS examinee_email,
    ex.exam_id,
    ex.exam_name,
    ex.exam_code,
    ex.exam_date,
    u.full_name AS entered_by_name
FROM results r
INNER JOIN exam_registrations er ON r.registration_id = er.registration_id
INNER JOIN examinees e ON er.examinee_id = e.examinee_id
INNER JOIN exams ex ON er.exam_id = ex.exam_id
INNER JOIN users u ON r.entered_by = u.user_id;

-- =====================================================
-- End of Schema
-- =====================================================

