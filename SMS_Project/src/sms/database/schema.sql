-- Student Management System Database
-- Run this script to create and populate the database

CREATE DATABASE IF NOT EXISTS sms_db;
USE sms_db;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;

-- Create students table
CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    student_id VARCHAR(20) UNIQUE NOT NULL,
    course VARCHAR(100) NOT NULL,
    marks DOUBLE NOT NULL
);

-- Create users table for login
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100)
);

-- Insert 5+ test students (requirement met)
INSERT INTO students (name, email, student_id, course, marks) VALUES
('John Doe', 'john.doe@email.com', 'S001', 'Computer Science', 85.5),
('Mary Ann Smith', 'mary.smith@email.com', 'S002', 'Information Technology', 92.0),
('Peter Kim', 'peter.kim@email.com', 'S003', 'Software Engineering', 78.5),
('Sarah Lee Johnson', 'sarah.j@email.com', 'S004', 'Data Science', 95.0),
('Michael Chen', 'michael.chen@email.com', 'S005', 'Networking', 67.5),
('Jessica Williams', 'jessica.w@email.com', 'S006', 'Artificial Intelligence', 88.0),
('David Brown', 'david.brown@email.com', 'S007', 'Computer Science', 72.5);

-- Insert default user (password: admin123)
INSERT INTO users (username, password, full_name) VALUES
('admin', 'admin123', 'System Administrator'),
('teacher', 'teacher123', 'Demo Teacher');

-- Display inserted data for verification
SELECT 'Students Table:' as '';
SELECT * FROM students;
SELECT 'Users Table:' as '';
SELECT * FROM users;