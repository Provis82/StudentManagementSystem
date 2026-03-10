package sms.gui;

import sms.database.DatabaseConnection;
import sms.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MainFrame extends JFrame {
    
    // Input fields
    private JTextField nameField;
    private JTextField emailField;
    private JTextField studentIdField;
    private JTextField marksField;
    private JComboBox<String> courseBox;
    private JTextField searchField;
    
    // Table components
    private JTable table;
    private DefaultTableModel tableModel;
    
    // Buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JButton showAllButton;
    private JButton clearButton;
    
    // Filter components
    private JCheckBox filterHighMarks;
    private JRadioButton sortByName;
    private JRadioButton sortByMarks;
    private JSlider marksSlider;
    private JTabbedPane tabbedPane;
    private JTextArea displayArea;
    
    // Status label
    private JLabel statusLabel;
    private JLabel validationLabel;
    
    // Currently selected student ID
    private int selectedStudentId = -1;
    
    public MainFrame() {
        setTitle("Student Management System - Main Panel");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        createMenu();
        initComponents();
        layoutComponents();
        setupListeners();
        loadStudents();
        
        // Apply initial validation state
        validateInputs();
    }
    
    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        
        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('x');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl X"));
        exitItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to exit?", "Confirm Exit",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DatabaseConnection.closeConnection();
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);
        
        // Students Menu
        JMenu studentsMenu = new JMenu("Students");
        studentsMenu.setMnemonic('S');
        
        JMenuItem addItem = new JMenuItem("Add Student");
        addItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        addItem.addActionListener(e -> focusOnAdd());
        
        JMenuItem updateItem = new JMenuItem("Update Student");
        updateItem.setAccelerator(KeyStroke.getKeyStroke("ctrl U"));
        updateItem.addActionListener(e -> updateStudent());
        
        JMenuItem deleteItem = new JMenuItem("Delete Student");
        deleteItem.setAccelerator(KeyStroke.getKeyStroke("ctrl D"));
        deleteItem.addActionListener(e -> deleteStudent());
        
        JMenuItem searchItem = new JMenuItem("Search Student");
        searchItem.setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
        searchItem.addActionListener(e -> searchField.requestFocus());
        
        studentsMenu.add(addItem);
        studentsMenu.add(updateItem);
        studentsMenu.add(deleteItem);
        studentsMenu.addSeparator();
        studentsMenu.add(searchItem);
        
        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(e -> {
            String aboutText = "📚 Student Management System\n" +
                              "Version 2.0 (Complete)\n\n" +
                              "Developed for Year 2 CSE Lab Project\n" +
                              "All Requirements Met:\n" +
                              "✓ Abstract Classes & Interfaces\n" +
                              "✓ Inheritance & Polymorphism\n" +
                              "✓ JDBC Database Integration\n" +
                              "✓ CRUD Operations\n" +
                              "✓ Real-time Validation\n" +
                              "✓ String Manipulation (Title Case, etc.)\n" +
                              "✓ Advanced GUI Components\n" +
                              "✓ 5+ Test Students";
            JOptionPane.showMessageDialog(this, aboutText,
                "About SMS", JOptionPane.INFORMATION_MESSAGE);
        });
        helpMenu.add(aboutItem);
        
        menuBar.add(fileMenu);
        menuBar.add(studentsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    private void initComponents() {
        // Input fields
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        studentIdField = new JTextField(10);
        marksField = new JTextField(10);
        
        String[] courses = {
            "Computer Science", "Information Technology",
            "Software Engineering", "Data Science", 
            "Networking", "Artificial Intelligence"
        };
        courseBox = new JComboBox<>(courses);
        
        searchField = new JTextField(15);
        
        // Buttons
        addButton = new JButton("Add Student");
        updateButton = new JButton("Update Student");
        deleteButton = new JButton("Delete Student");
        searchButton = new JButton("Search");
        showAllButton = new JButton("Show All");
        clearButton = new JButton("Clear Form");
        
        // Initially disable update and delete
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Filter components
        filterHighMarks = new JCheckBox("Show only marks > 75");
        sortByName = new JRadioButton("Sort by Name");
        sortByMarks = new JRadioButton("Sort by Marks");
        ButtonGroup sortGroup = new ButtonGroup();
        sortGroup.add(sortByName);
        sortGroup.add(sortByMarks);
        sortByName.setSelected(true);
        
        marksSlider = new JSlider(0, 100, 50);
        marksSlider.setMajorTickSpacing(25);
        marksSlider.setMinorTickSpacing(5);
        marksSlider.setPaintTicks(true);
        marksSlider.setPaintLabels(true);
        marksSlider.setBorder(BorderFactory.createTitledBorder("Marks Filter (Min)"));
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Table setup
        String[] columns = {"ID", "Student ID", "Name", "Email", "Course", "Marks", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setRowHeight(25);
        
        // Add selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedStudent();
            }
        });
        
        // Text area for additional display
        displayArea = new JTextArea(8, 40);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        displayArea.setBorder(BorderFactory.createTitledBorder("Student Summary (String Manipulation Demo)"));
        
        // Status and validation labels
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        validationLabel = new JLabel(" ");
        validationLabel.setForeground(Color.RED);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Top panel - Input form
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Student Information"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1: Name
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        inputPanel.add(nameField, gbc);
        
        // Row 1: Student ID
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Student ID:*"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.2;
        inputPanel.add(studentIdField, gbc);
        
        // Row 2: Email
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        inputPanel.add(emailField, gbc);
        
        // Row 2: Course
        gbc.gridx = 2; gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Course:*"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.2;
        inputPanel.add(courseBox, gbc);
        
        // Row 3: Marks
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Marks:*"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        inputPanel.add(marksField, gbc);
        
        // Row 3: Validation message
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(validationLabel, gbc);
        gbc.gridwidth = 1;
        
        // Row 4: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(buttonPanel, gbc);
        gbc.gridwidth = 1;
        
        // Center panel - Table and filters
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filters & Sorting"));
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(showAllButton);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(filterHighMarks);
        filterPanel.add(sortByName);
        filterPanel.add(sortByMarks);
        
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(marksSlider, BorderLayout.CENTER);
        
        JPanel filterMainPanel = new JPanel(new BorderLayout());
        filterMainPanel.add(filterPanel, BorderLayout.NORTH);
        filterMainPanel.add(sliderPanel, BorderLayout.SOUTH);
        
        centerPanel.add(filterMainPanel, BorderLayout.NORTH);
        
        // Table with tabs
        JScrollPane tableScroll = new JScrollPane(table);
        JScrollPane displayScroll = new JScrollPane(displayArea);
        
        tabbedPane.addTab("Table View", tableScroll);
        tabbedPane.addTab("Summary View", displayScroll);
        
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Add panels to frame
        add(inputPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);
    }
    
    private void setupListeners() {
        // Button listeners
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        searchButton.addActionListener(e -> searchStudents());
        showAllButton.addActionListener(e -> loadStudents());
        clearButton.addActionListener(e -> clearFields());
        
        // Document listeners for real-time validation
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateInputs(); }
            public void removeUpdate(DocumentEvent e) { validateInputs(); }
            public void changedUpdate(DocumentEvent e) { validateInputs(); }
        };
        
        nameField.getDocument().addDocumentListener(docListener);
        emailField.getDocument().addDocumentListener(docListener);
        studentIdField.getDocument().addDocumentListener(docListener);
        marksField.getDocument().addDocumentListener(docListener);
        
        // Filter listeners
        filterHighMarks.addActionListener(e -> filterStudents());
        sortByName.addActionListener(e -> filterStudents());
        sortByMarks.addActionListener(e -> filterStudents());
        marksSlider.addChangeListener(e -> filterStudents());
        
        // Search on enter
        searchField.addActionListener(e -> searchStudents());
    }
    
    private void validateInputs() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String marksText = marksField.getText().trim();
        
        boolean valid = true;
        StringBuilder message = new StringBuilder();
        
        if (name.isEmpty()) {
            message.append("Name required. ");
            valid = false;
        }
        
        if (studentId.isEmpty()) {
            message.append("Student ID required. ");
            valid = false;
        }
        
        if (email.isEmpty() || !ValidationUtil.isValidEmail(email)) {
            message.append("Valid email required. ");
            valid = false;
        }
        
        if (marksText.isEmpty() || !ValidationUtil.isNumeric(marksText)) {
            message.append("Valid number required for marks. ");
            valid = false;
        } else {
            double marks = Double.parseDouble(marksText);
            if (marks < 0 || marks > 100) {
                message.append("Marks must be 0-100. ");
                valid = false;
            }
        }
        
        if (valid) {
            validationLabel.setText("✅ All fields valid");
            validationLabel.setForeground(new Color(0, 150, 0));
            addButton.setEnabled(true);
        } else {
            validationLabel.setText("❌ " + message.toString());
            validationLabel.setForeground(Color.RED);
            addButton.setEnabled(false);
        }
    }
    
    private void loadStudents() {
        tableModel.setRowCount(0);
        displayArea.setText(""); // Clear display area
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM students ORDER BY name ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String studentId = rs.getString("student_id");
                String course = rs.getString("course");
                double marks = rs.getDouble("marks");
                
                // Get grade based on marks
                String grade = getGrade(marks);
                
                tableModel.addRow(new Object[]{
                    id,
                    studentId,
                    name,
                    email,
                    course,
                    marks,
                    grade
                });
                
                // String manipulation demos:
                // 1. Split - get first name
                String firstName = name.split(" ")[0];
                
                // 2. Substring - get email username
                String emailUsername = email.substring(0, email.indexOf('@'));
                
                // 3. Concatenation - build summary
                String summary = "Name: " + name + 
                               " | Course: " + course + 
                               " | Marks: " + marks + 
                               " | Grade: " + grade;
                
                displayArea.append(summary + "\n");
                displayArea.append("First Name: " + firstName + 
                                 " | Email Username: " + emailUsername + "\n\n");
                
                count++;
            }
            
            statusLabel.setText("Loaded " + count + " students");
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper method to determine grade
    private String getGrade(double marks) {
        if (marks >= 90) return "A";
        else if (marks >= 80) return "B";
        else if (marks >= 70) return "C";
        else if (marks >= 60) return "D";
        else return "F";
    }
    
    // Convert to Title Case (string manipulation)
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1))
                      .append(" ");
            }
        }
        return result.toString().trim();
    }
    
    private void addStudent() {
        if (!validateBeforeSave()) return;
        
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String course = courseBox.getSelectedItem().toString();
        double marks = Double.parseDouble(marksField.getText().trim());
        
        // Convert name to Title Case
        name = toTitleCase(name);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO students (name, email, student_id, course, marks) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, studentId.toUpperCase());
            ps.setString(4, course);
            ps.setDouble(5, marks);
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                String grade = getGrade(marks);
                String summary = "Name: " + name + " | Course: " + course + 
                               " | Marks: " + marks + " | Grade: " + grade;
                
                JOptionPane.showMessageDialog(this, 
                    "Student added successfully!\n" + summary,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                loadStudents();
                clearFields();
                statusLabel.setText("Student added successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error adding student: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStudent() {
        if (selectedStudentId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a student to update",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateBeforeSave()) return;
        
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String course = courseBox.getSelectedItem().toString();
        double marks = Double.parseDouble(marksField.getText().trim());
        
        // Convert name to Title Case
        name = toTitleCase(name);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE students SET name=?, email=?, course=?, marks=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, course);
            ps.setDouble(4, marks);
            ps.setInt(5, selectedStudentId);
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Student updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                loadStudents();
                clearFields();
                updateButton.setEnabled(false);
                deleteButton.setEnabled(false);
                selectedStudentId = -1;
                statusLabel.setText("Student updated successfully");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Error updating student: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteStudent() {
        if (selectedStudentId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a student to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseConnection.getConnection()) {
                String sql = "DELETE FROM students WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, selectedStudentId);
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Student deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    loadStudents();
                    clearFields();
                    updateButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    selectedStudentId = -1;
                    statusLabel.setText("Student deleted successfully");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Error deleting student: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void searchStudents() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadStudents();
            return;
        }
        
        tableModel.setRowCount(0);
        displayArea.setText("");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Case-insensitive search using LOWER()
            String sql = "SELECT * FROM students WHERE LOWER(name) LIKE ? OR LOWER(student_id) LIKE ? OR LOWER(course) LIKE ? OR LOWER(email) LIKE ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            String pattern = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String studentId = rs.getString("student_id");
                String course = rs.getString("course");
                double marks = rs.getDouble("marks");
                String grade = getGrade(marks);
                
                tableModel.addRow(new Object[]{
                    id,
                    studentId,
                    name,
                    email,
                    course,
                    marks,
                    grade
                });
                
                displayArea.append("🔍 Found: " + name + " (" + studentId + ")\n");
                count++;
            }
            
            statusLabel.setText("Found " + count + " students matching '" + keyword + "'");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void filterStudents() {
        // This is a simplified filter for demo purposes
        // In a real app, you'd query the database with filter conditions
        
        if (filterHighMarks.isSelected()) {
            statusLabel.setText("Filter: Marks > 75 (demo - showing all for now)");
        } else {
            statusLabel.setText("Filter removed - showing all students");
        }
        loadStudents(); // Just reload all for demo
    }
    
    private void loadSelectedStudent() {
        int row = table.getSelectedRow();
        if (row != -1) {
            selectedStudentId = (int) tableModel.getValueAt(row, 0);
            String studentId = (String) tableModel.getValueAt(row, 1);
            String name = (String) tableModel.getValueAt(row, 2);
            String email = (String) tableModel.getValueAt(row, 3);
            String course = (String) tableModel.getValueAt(row, 4);
            double marks = (double) tableModel.getValueAt(row, 5);
            
            nameField.setText(name);
            emailField.setText(email);
            studentIdField.setText(studentId);
            courseBox.setSelectedItem(course);
            marksField.setText(String.valueOf(marks));
            
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
            addButton.setEnabled(false);
        }
    }
    
    private void focusOnAdd() {
        clearFields();
        nameField.requestFocus();
    }
    
    private void clearFields() {
        nameField.setText("");
        emailField.setText("");
        studentIdField.setText("");
        marksField.setText("");
        courseBox.setSelectedIndex(0);
        table.clearSelection();
        selectedStudentId = -1;
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        addButton.setEnabled(true);
        validationLabel.setText(" ");
        statusLabel.setText("Form cleared");
    }
    
    private boolean validateBeforeSave() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String studentId = studentIdField.getText().trim();
        String marksText = marksField.getText().trim();
        
        if (name.isEmpty() || email.isEmpty() || studentId.isEmpty() || marksText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill all required fields",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a valid email address",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isNumeric(marksText)) {
            JOptionPane.showMessageDialog(this, 
                "Marks must be a number",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            marksField.requestFocus();
            return false;
        }
        
        double marks = Double.parseDouble(marksText);
        if (marks < 0 || marks > 100) {
            JOptionPane.showMessageDialog(this, 
                "Marks must be between 0 and 100",
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            marksField.requestFocus();
            return false;
        }
        
        return true;
    }
}