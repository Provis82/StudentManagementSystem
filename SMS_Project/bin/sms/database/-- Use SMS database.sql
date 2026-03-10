-- Use SMS database
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

-- Insert test students
INSERT INTO students (name, email, student_id, course, marks) VALUES
('John Doe', 'john.doe@email.com', 'S001', 'Computer Science', 85.5),
('Mary Ann Smith', 'mary.smith@email.com', 'S002', 'Information Technology', 92.0),
('Peter Kim', 'peter.kim@email.com', 'S003', 'Software Engineering', 78.5),
('Sarah Lee Johnson', 'sarah.j@email.com', 'S004', 'Data Science', 95.0),
('Michael Chen', 'michael.chen@email.com', 'S005', 'Networking', 67.5);

-- Insert default users
INSERT INTO users (username, password, full_name) VALUES
('admin', 'admin123', 'System Administrator'),
('teacher', 'teacher123', 'Demo Teacher');

-- Verify
SELECT '✅ Students table created' as 'Status';
SELECT COUNT(*) as 'Total Students' FROM students;
SELECT * FROM users;