package sms.gui;

import sms.database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;
    private JTextField marksField;
    private JComboBox<String> courseBox;

    private JTable table;
    private DefaultTableModel model;

    public MainFrame() {

        setTitle("Student Management System");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createMenu();
        createForm();
        loadStudents();
        
    }

    private void createMenu() {

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");

        about.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "Student Management System\nVersion 1.0"));

        helpMenu.add(about);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createForm() {

        JPanel panel = new JPanel(new BorderLayout(10,10));

        JPanel form = new JPanel(new GridLayout(5,2,10,10));

        form.add(new JLabel("Name:"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Email:"));
        emailField = new JTextField();
        form.add(emailField);

        form.add(new JLabel("Course:"));
        courseBox = new JComboBox<>(new String[]{
                "Java", "Database", "Networking", "AI"
        });
        form.add(courseBox);

        form.add(new JLabel("Marks:"));
        marksField = new JTextField();
        form.add(marksField);

        JButton addButton = new JButton("Add Student");
        JButton deleteButton = new JButton("Delete Student");

        form.add(addButton);
        form.add(deleteButton);

        panel.add(form, BorderLayout.NORTH);

        String columns[] = {"Name", "Email", "Course", "Marks"};

        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);

        addButton.addActionListener(e -> addStudent());
        deleteButton.addActionListener(e -> deleteStudent());
    }

    private void loadStudents() {

        model.setRowCount(0);

        try {

            Connection conn = DatabaseConnection.getConnection();

            String sql = "SELECT * FROM students";

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while(rs.next()) {

                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("course"),
                        rs.getDouble("marks")
                });

            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void addStudent() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String course = courseBox.getSelectedItem().toString();
        String marksText = marksField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || marksText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill all fields.");
            return;
        }

        double marks;

        try {
            marks = Double.parseDouble(marksText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Marks must be a number.");
            return;
        }

        model.addRow(new Object[]{
                name,
                email,
                course,
                marks
        });

        clearFields();
    }

    private void deleteStudent() {

        int row = table.getSelectedRow();

        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student to delete.");
            return;
        }

        model.removeRow(row);
    }

    private void clearFields() {

        nameField.setText("");
        emailField.setText("");
        marksField.setText("");
    }
}