package sms.model;

import java.util.List;
import java.sql.*;
import sms.database.DatabaseConnection;

public class Student extends Person implements DatabaseOperations<Student> {
    private int id;
    private String studentId;
    private String course;
    private double marks;

    public Student(String name, String email, String studentId, String course, double marks) {
        super(toTitleCase(name), email);
        this.studentId = studentId.toUpperCase();
        this.course = course;
        this.marks = marks;
    }

    // Getters
    public int getId() { return id; }
    public String getStudentId() { return studentId; }
    public String getCourse() { return course; }
    public double getMarks() { return marks; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStudentId(String studentId) { this.studentId = studentId.toUpperCase(); }
    public void setCourse(String course) { this.course = course; }
    public void setMarks(double marks) { this.marks = marks; }

    @Override
    public void displayInfo() {
        System.out.println("Student: " + name + " | ID: " + studentId +
                           " | Course: " + course + " | Marks: " + marks);
    }

    // Get grade from marks
    public String getGrade() {
        if (marks >= 90) return "A";
        else if (marks >= 80) return "B";
        else if (marks >= 70) return "C";
        else if (marks >= 60) return "D";
        else return "F";
    }

    // Generate student summary using string concatenation
    public String getSummary() {
        return "Name: " + toTitleCase(name) +
               " | Course: " + course +
               " | Marks: " + marks +
               " | Grade: " + getGrade();
    }

    // ─── DatabaseOperations Implementation ───────────────────────────

    @Override
    public boolean add(Student s) {
        String sql = "INSERT INTO students (name, email, student_id, course, marks) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toTitleCase(s.getName()));
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getStudentId().toUpperCase());
            ps.setString(4, s.getCourse());
            ps.setDouble(5, s.getMarks());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Add student error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(Student s) {
        String sql = "UPDATE students SET name=?, email=?, course=?, marks=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, toTitleCase(s.getName()));
            ps.setString(2, s.getEmail());
            ps.setString(3, s.getCourse());
            ps.setDouble(4, s.getMarks());
            ps.setInt(5, s.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update student error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM students WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete student error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Student> search(String keyword) {
        List<Student> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM students WHERE " +
                     "LOWER(name) LIKE ? OR LOWER(student_id) LIKE ? OR LOWER(course) LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Student s = new Student(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("student_id"),
                    rs.getString("course"),
                    rs.getDouble("marks")
                );
                s.setId(rs.getInt("id"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Search student error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Student> getAll() {
        List<Student> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Student s = new Student(
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("student_id"),
                    rs.getString("course"),
                    rs.getDouble("marks")
                );
                s.setId(rs.getInt("id"));
                list.add(s);
            }
        } catch (SQLException e) {
            System.err.println("Get all students error: " + e.getMessage());
        }
        return list;
    }
}