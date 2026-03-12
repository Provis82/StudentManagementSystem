-- Student Management System Database
-- Database name: SMS

-- Drop database if exists (for clean setup)
DROP DATABASE IF EXISTS SMS;

-- Create database
CREATE DATABASE SMS;
USE SMS;

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

-- Insert 7 test students (more than 5 required)
INSERT INTO students (name, email, student_id, course, marks) VALUES
('Mugisha Leonce', 'mugishaleonce@email.com', 'S001', 'Computer Science', 95.0),
('John Doe', 'john.doe@email.com', 'S002', 'Information Technology', 85.5),
('Mary Smith', 'mary.smith@email.com', 'S003', 'Software Engineering', 92.0),
('Inema Chris Ladjou', 'inemaki@email.com', 'S004', 'Data Science', 60.0),
('Alice Wonder', 'alice@email.com', 'S005', 'Artificial Intelligence', 78.5),
('Bob Johnson', 'bob.j@email.com', 'S006', 'Cyber Security', 88.0),
('Carol White', 'carol.w@email.com', 'S007', 'Networking', 72.0);

-- Insert default users
INSERT INTO users (username, password, full_name) VALUES
('admin', 'admin123', 'System Administrator'),
('teacher', 'teacher123', 'Demo Teacher');

-- Show results
SELECT '=== STUDENTS TABLE ===' as '';
SELECT * FROM students ORDER BY name;
SELECT '=== USERS TABLE ===' as '';
SELECT * FROM users;