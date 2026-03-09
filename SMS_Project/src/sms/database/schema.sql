-- Student Management System Database


CREATE DATABASE IF NOT EXISTS sms_db;

USE sms_db;

CREATE TABLE IF NOT EXISTS students (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    course VARCHAR(100) NOT NULL,
    marks DOUBLE NOT NULL)
-- Sample test data

INSERT INTO students(name,email,course,marks) VALUES
('John Doe','john@email.com','Java',80),
('Mary Ann','mary@email.com','Database',75),
('Peter Kim','peter@email.com','Networking',90);