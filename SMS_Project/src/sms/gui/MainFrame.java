package sms.gui;

import sms.database.DatabaseConnection;
import sms.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
    
    // Filter components - COMPLETE SET
    private JCheckBox filterHighMarks;
    private JCheckBox filterComputerScience;
    private JRadioButton sortByName;
    private JRadioButton sortByMarks;
    private JRadioButton sortByCourse;
    private ButtonGroup sortGroup;
    private JSlider marksSlider;
    private JLabel sliderValueLabel;
    
    // Tabbed pane for multiple views
    private JTabbedPane tabbedPane;
    private JTextArea displayArea;
    private JTextArea stringManipulationArea;
    
    // Status and validation labels
    private JLabel statusLabel;
    private JLabel validationLabel;
    private JLabel studentCountLabel;
    
    // Currently selected student ID
    private int selectedStudentId = -1;
    
    public MainFrame() {
        setTitle("Student Management System - Main Panel");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        createMenu();
        initComponents();
        layoutComponents();
        setupListeners();
        loadStudents();
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
                              "Version 3.0 (Complete)\n\n" +
                              "Developed for Year 2 CSE Lab Project\n" +
                              "✅ All Requirements Met:\n" +
                              "   • Abstract Classes & Interfaces\n" +
                              "   • Inheritance & Polymorphism\n" +
                              "   • JDBC Database Integration\n" +
                              "   • CRUD Operations\n" +
                              "   • Real-time Validation\n" +
                              "   • String Manipulation\n" +
                              "   • JTabbedPane for Multiple Views\n" +
                              "   • JSlider for Marks Range Filter\n" +
                              "   • JRadioButtons for Sorting\n" +
                              "   • JCheckBoxes for Filtering\n" +
                              "   • 5+ Test Students\n\n" +
                              "🔧 Database: MySQL (SMS)\n" +
                              "👥 Team Project\n" +
                              "📅 March 2026";
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
            "Networking", "Artificial Intelligence",
            "Cyber Security"
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
        
        // Filter components - COMPLETE SET
        filterHighMarks = new JCheckBox("Show only marks > 75");
        filterComputerScience = new JCheckBox("Show only Computer Science");
        
        sortByName = new JRadioButton("Sort by Name", true);
        sortByMarks = new JRadioButton("Sort by Marks");
        sortByCourse = new JRadioButton("Sort by Course");
        
        sortGroup = new ButtonGroup();
        sortGroup.add(sortByName);
        sortGroup.add(sortByMarks);
        sortGroup.add(sortByCourse);
        
        // JSlider for marks range filtering
        marksSlider = new JSlider(0, 100, 50);
        marksSlider.setMajorTickSpacing(25);
        marksSlider.setMinorTickSpacing(5);
        marksSlider.setPaintTicks(true);
        marksSlider.setPaintLabels(true);
        marksSlider.setBorder(BorderFactory.createTitledBorder("Minimum Marks Filter"));
        
        sliderValueLabel = new JLabel("Current value: 50");
        sliderValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Tabbed pane for multiple views
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
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Add selection listener for Update/Delete
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedStudent();
            }
        });
        
        // Text areas for display
        displayArea = new JTextArea(10, 50);
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        displayArea.setBorder(BorderFactory.createTitledBorder("Student Records"));
        
        stringManipulationArea = new JTextArea(10, 50);
        stringManipulationArea.setEditable(false);
        stringManipulationArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        stringManipulationArea.setBorder(BorderFactory.createTitledBorder(
            "String Manipulation Demo (Title Case, Split, Substring, Concatenation)"));
        
        // Status and validation labels
        statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        
        validationLabel = new JLabel(" ");
        validationLabel.setForeground(Color.RED);
        
        studentCountLabel = new JLabel("Total Students: 0");
        studentCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        studentCountLabel.setForeground(new Color(70, 130, 180));
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
        
        // Row 1: Name and Student ID
        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Name:*"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.3;
        inputPanel.add(nameField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Student ID:*"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.2;
        inputPanel.add(studentIdField, gbc);
        
        // Row 2: Email and Course
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        inputPanel.add(new JLabel("Email:*"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(emailField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        inputPanel.add(new JLabel("Course:*"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(courseBox, gbc);
        
        // Row 3: Marks and Validation message
        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Marks:*"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(marksField, gbc);
        
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
        
        // Center panel - Table, filters, and tabs
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Filter panel - COMPLETE with all components
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Search, Filter & Sort Options"));
        
        // Row 1: Search
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Keyword:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);
        
        // Row 2: Checkboxes
        JPanel checkBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkBoxPanel.add(filterHighMarks);
        checkBoxPanel.add(filterComputerScience);
        
        // Row 3: Radio buttons
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radioPanel.add(new JLabel("Sort by:"));
        radioPanel.add(sortByName);
        radioPanel.add(sortByMarks);
        radioPanel.add(sortByCourse);
        
        // Row 4: Slider
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.add(marksSlider, BorderLayout.CENTER);
        sliderPanel.add(sliderValueLabel, BorderLayout.SOUTH);
        
        // Add all to filter panel
        filterPanel.add(searchPanel);
        filterPanel.add(checkBoxPanel);
        filterPanel.add(radioPanel);
        filterPanel.add(sliderPanel);
        
        // Table with tabs
        JScrollPane tableScroll = new JScrollPane(table);
        JScrollPane displayScroll = new JScrollPane(displayArea);
        JScrollPane stringScroll = new JScrollPane(stringManipulationArea);
        
        tabbedPane.addTab("📊 Table View", tableScroll);
        tabbedPane.addTab("📝 Summary View", displayScroll);
        tabbedPane.addTab("🔤 String Manipulation Demo", stringScroll);
        
        // Add student count label to north of center panel
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(filterPanel, BorderLayout.CENTER);
        northPanel.add(studentCountLabel, BorderLayout.EAST);
        
        centerPanel.add(northPanel, BorderLayout.NORTH);
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
        filterHighMarks.addActionListener(e -> applyFilters());
        filterComputerScience.addActionListener(e -> applyFilters());
        sortByName.addActionListener(e -> applyFilters());
        sortByMarks.addActionListener(e -> applyFilters());
        sortByCourse.addActionListener(e -> applyFilters());
        
        // Slider listener
        marksSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int value = marksSlider.getValue();
                sliderValueLabel.setText("Minimum marks: " + value);
                applyFilters();
            }
        });
        
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
    
    private String getGrade(double marks) {
        if (marks >= 90) return "A";
        if (marks >= 80) return "B";
        if (marks >= 70) return "C";
        if (marks >= 60) return "D";
        return "F";
    }
    
    private String toTitleCase(String input) {
        if (input == null || input.isEmpty()) return input;
        String[] words = input.trim().toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1)).append(" ");
            }
        }
        return result.toString().trim();
    }
    
    private void loadStudents() {
        String sql = "SELECT * FROM students ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            displayStudentsFromResultSet(rs);
            statusLabel.setText("✅ Loaded all students");
            
        } catch (SQLException e) {
            statusLabel.setText("❌ Error loading students");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayStudentsFromResultSet(ResultSet rs) throws SQLException {
        tableModel.setRowCount(0);
        displayArea.setText("");
        stringManipulationArea.setText("");
        
        stringManipulationArea.append("=== STRING MANIPULATION DEMONSTRATION ===\n");
        stringManipulationArea.append("1. Title Case: All names are stored in Title Case\n");
        stringManipulationArea.append("2. Split(): Extracting first names\n");
        stringManipulationArea.append("3. Substring(): Extracting email usernames\n");
        stringManipulationArea.append("4. Concatenation: Building summaries\n");
        stringManipulationArea.append("5. Case-insensitive search: Using LOWER() in SQL\n\n");
        stringManipulationArea.append("STUDENT DETAILS:\n");
        stringManipulationArea.append("================\n\n");
        
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
                id, studentId, name, email, course, marks, grade
            });
            
            // String manipulation demos
            
            // 1. Split - get first name
            String firstName = name.split(" ")[0];
            
            // 2. Substring - get email username
            String emailUsername = email.substring(0, email.indexOf('@'));
            
            // 3. Concatenation for summary
            String summary = "Name: " + name + " | Course: " + course + 
                           " | Marks: " + marks + " | Grade: " + grade;
            
            displayArea.append(summary + "\n");
            
            stringManipulationArea.append("Student " + (count+1) + ":\n");
            stringManipulationArea.append("  Full Name: " + name + " (Title Case)\n");
            stringManipulationArea.append("  First Name (split()): " + firstName + "\n");
            stringManipulationArea.append("  Email Username (substring()): " + emailUsername + "\n");
            stringManipulationArea.append("  Summary (concatenation): " + summary + "\n\n");
            
            count++;
        }
        
        studentCountLabel.setText("Total Students: " + count);
        statusLabel.setText("Displaying " + count + " students");
    }
    
    private void applyFilters() {
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // Apply checkbox filters
        if (filterHighMarks.isSelected()) {
            sql.append(" AND marks > 75");
        }
        
        if (filterComputerScience.isSelected()) {
            sql.append(" AND course = 'Computer Science'");
        }
        
        // Apply slider filter
        int minMarks = marksSlider.getValue();
        sql.append(" AND marks >= ?");
        params.add(minMarks);
        
        // Apply sorting
        if (sortByName.isSelected()) {
            sql.append(" ORDER BY name");
        } else if (sortByMarks.isSelected()) {
            sql.append(" ORDER BY marks DESC");
        } else if (sortByCourse.isSelected()) {
            sql.append(" ORDER BY course, name");
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            displayStudentsFromResultSet(rs);
            
            String filterDesc = "Filtered: ";
            if (filterHighMarks.isSelected()) filterDesc += "marks>75 ";
            if (filterComputerScience.isSelected()) filterDesc += "CS only ";
            filterDesc += "min marks=" + minMarks;
            statusLabel.setText("✅ " + filterDesc);
            
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Error applying filters");
        }
    }
    
    private void addStudent() {
        if (!validateBeforeSave()) return;
        
        String name = toTitleCase(nameField.getText().trim());
        String email = emailField.getText().trim();
        String studentId = studentIdField.getText().trim().toUpperCase();
        String course = courseBox.getSelectedItem().toString();
        double marks = Double.parseDouble(marksField.getText().trim());
        
        String sql = "INSERT INTO students (name, email, student_id, course, marks) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, studentId);
            ps.setString(4, course);
            ps.setDouble(5, marks);
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Student added successfully!\n" +
                    "Name: " + name + "\n" +
                    "ID: " + studentId + "\n" +
                    "Grade: " + getGrade(marks),
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                loadStudents();
                clearFields();
                statusLabel.setText("✅ Student added successfully");
            }
        } catch (SQLException e) {
            statusLabel.setText("❌ Error adding student");
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateStudent() {
        if (selectedStudentId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a student from the table to update",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateBeforeSave()) return;
        
        String name = toTitleCase(nameField.getText().trim());
        String email = emailField.getText().trim();
        String course = courseBox.getSelectedItem().toString();
        double marks = Double.parseDouble(marksField.getText().trim());
        
        String sql = "UPDATE students SET name=?, email=?, course=?, marks=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, course);
            ps.setDouble(4, marks);
            ps.setInt(5, selectedStudentId);
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, 
                    "✅ Student updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                loadStudents();
                clearFields();
                statusLabel.setText("✅ Student updated successfully");
            }
        } catch (SQLException e) {
            statusLabel.setText("❌ Error updating student");
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteStudent() {
        if (selectedStudentId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a student from the table to delete",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this student?\n" +
            "This action cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM students WHERE id=?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, selectedStudentId);
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "✅ Student deleted successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    loadStudents();
                    clearFields();
                    statusLabel.setText("✅ Student deleted successfully");
                }
            } catch (SQLException e) {
                statusLabel.setText("❌ Error deleting student");
                JOptionPane.showMessageDialog(this, 
                    "Database Error: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void searchStudents() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadStudents();
            return;
        }
        
        String sql = "SELECT * FROM students WHERE " +
                     "LOWER(name) LIKE ? OR " +
                     "LOWER(student_id) LIKE ? OR " +
                     "LOWER(course) LIKE ? OR " +
                     "LOWER(email) LIKE ? " +
                     "ORDER BY name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String pattern = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            ps.setString(4, pattern);
            
            ResultSet rs = ps.executeQuery();
            displayStudentsFromResultSet(rs);
            
            statusLabel.setText("✅ Searched for: '" + keyword + "'");
            
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("❌ Search error");
        }
    }
    
    private void loadSelectedStudent() {
        int row = table.getSelectedRow();
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
        
        statusLabel.setText("Selected: " + name);
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