-- ================================
-- 1. DATABASE
-- ================================
drop database hostel_db;
CREATE DATABASE IF NOT EXISTS hostel_db;
USE hostel_db;

-- ================================
-- 2. USERS (BASE TABLE)
-- ================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15),
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'ADMIN', 'WARDEN', 'ACCOUNTANT') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- 3. ROLE-SPECIFIC TABLES
-- ================================
CREATE TABLE students (
    id BIGINT PRIMARY KEY,
    student_code VARCHAR(20) UNIQUE,
    status ENUM('REGISTERED', 'ASSIGNED', 'VACATED') DEFAULT 'REGISTERED',
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE admins (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE wardens (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE accountants (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE
);

-- ================================
-- 4. ROOM MANAGEMENT
-- ================================
CREATE TABLE rooms (
    room_id INT PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    occupied_count INT DEFAULT 0,
    status ENUM('AVAILABLE', 'FULL', 'MAINTENANCE') DEFAULT 'AVAILABLE',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ================================
-- 5. ROOM ALLOCATION (CRITICAL TABLE)
-- ================================
CREATE TABLE allocations (
    allocation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    room_id INT NOT NULL,
    allocated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    vacated_at DATETIME NULL,
    status ENUM('ACTIVE', 'VACATED') DEFAULT 'ACTIVE',

    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),

    -- active-allocation protection is enforced in application logic
    KEY idx_allocations_student_status (student_id, status)
);

-- ================================
-- 6. BOOKINGS (REQUEST STAGE)
-- ================================
CREATE TABLE bookings (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    room_id INT NOT NULL,
    request_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'PENDING',

    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),

    -- prevent duplicate pending requests
    UNIQUE KEY unique_pending_booking (student_id, room_id, status)
);

-- ================================
-- 7. RENT & PAYMENTS
-- ================================
CREATE TABLE rent_policies (
    policy_id INT AUTO_INCREMENT PRIMARY KEY,
    monthly_rent DOUBLE NOT NULL,
    late_fee DOUBLE DEFAULT 0.0
);

CREATE TABLE invoices (
    invoice_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    policy_id INT,
    amount DOUBLE NOT NULL,
    due_date DATE NOT NULL,
    status ENUM('PENDING', 'PAID', 'OVERDUE') DEFAULT 'PENDING',

    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (policy_id) REFERENCES rent_policies(policy_id)
);

CREATE TABLE payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    invoice_id BIGINT,
    amount DOUBLE NOT NULL,
    payment_mode VARCHAR(30),
    payment_channel VARCHAR(30),
    reference_id VARCHAR(80),
    payment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    status ENUM('SUCCESSFUL', 'REFUND_REQUESTED', 'FAILED', 'REFUNDED') NOT NULL,

    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (invoice_id) REFERENCES invoices(invoice_id)
);

CREATE TABLE receipts (
    receipt_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT UNIQUE,
    generated_date DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (payment_id) REFERENCES payments(payment_id)
);

-- ================================
-- 8. MAINTENANCE SYSTEM
-- ================================
CREATE TABLE maintenance_staff (
    staff_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(50),
    contact VARCHAR(15)
);

CREATE TABLE complaints (
    complaint_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT,
    room_id INT,
    assigned_staff_id BIGINT,
    description TEXT NOT NULL,

    status ENUM('PENDING', 'ASSIGNED', 'IN_PROGRESS', 'CLOSED', 'REOPENED') DEFAULT 'PENDING',
    priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id),
    FOREIGN KEY (assigned_staff_id) REFERENCES maintenance_staff(staff_id)
);

CREATE TABLE complaint_photos (
    photo_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT,
    file_path VARCHAR(255) NOT NULL,

    FOREIGN KEY (complaint_id) REFERENCES complaints(complaint_id)
);

-- ================================
-- 9. NOTIFICATIONS
-- ================================
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ================================
-- 10. SEED DATA
-- ================================
INSERT INTO rooms (room_id, type, capacity, occupied_count, status) VALUES 
(101, 'Single', 1, 0, 'AVAILABLE'),
(102, 'Double', 2, 0, 'AVAILABLE'),
(103, 'Single', 1, 0, 'AVAILABLE'),
(201, 'Double', 2, 0, 'AVAILABLE');

INSERT INTO rent_policies (monthly_rent, late_fee) 
VALUES (5000.0, 200.0);
