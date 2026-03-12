package sms.gui;

import sms.database.DatabaseConnection;
import sms.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
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
    
    // Buttons - Clean aesthetic buttons
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JButton showAllButton;
    private JButton clearButton;
    
    // Filter components
    private JCheckBox filterHighMarks;
    private JCheckBox filterComputerScience;
    private JRadioButton sortByName;
    private JRadioButton sortByMarks;
    private JRadioButton sortByCourse;
    private ButtonGroup sortGroup;
    private JSlider marksSlider;
    private JLabel sliderValueLabel;
    
    // Status labels
    private JLabel statusLabel;
    private JLabel validationLabel;
    private JLabel studentCountLabel;
    
    // Currently selected student ID
    private int selectedStudentId = -1;
    
    // Color scheme - Light sky blue background
    private Color backgroundColor = new Color(230, 242, 255);    // Soft light sky blue
    private Color panelColor = Color.WHITE;
    private Color borderColor = new Color(200, 215, 230);        // Soft blue-gray
    
    // Button colors - Clean, aesthetic palette
    private Color addButtonColor = new Color(52, 152, 219);      // Soft blue
    private Color updateButtonColor = new Color(46, 204, 113);   // Soft green
    private Color deleteButtonColor = new Color(231, 76, 60);    // Soft red
    private Color clearButtonColor = new Color(155, 89, 182);    // Soft purple
    private Color searchButtonColor = new Color(52, 73, 94);     // Dark slate
    private Color showAllButtonColor = new Color(241, 196, 15);  // Soft yellow
    
    public MainFrame() {
        setTitle("Student Management System");
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(backgroundColor);
        
        initComponents();
        layoutComponents();
        setupListeners();
        loadStudents();
        validateInputs();
    }
    
    private void initComponents() {
        // Input fields
        nameField = createTextField();
        emailField = createTextField();
        studentIdField = createTextField();
        marksField = createTextField();
        
        String[] courses = {
            "Computer Science", "Information Technology",
            "Software Engineering", "Data Science", 
            "Networking", "Artificial Intelligence"
        };
        courseBox = new JComboBox<>(courses);
        courseBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        courseBox.setBackground(Color.WHITE);
        courseBox.setBorder(BorderFactory.createLineBorder(borderColor));
        
        searchField = createTextField();
        searchField.setPreferredSize(new Dimension(250, 38));
        
        // CRUD Buttons - No icons, just text
        addButton = createAestheticButton("Add", addButtonColor, Color.BLACK);
        updateButton = createAestheticButton("Update", updateButtonColor, Color.BLACK);
        deleteButton = createAestheticButton("Delete", deleteButtonColor, Color.BLACK);
        clearButton = createAestheticButton("Clear", clearButtonColor, Color.WHITE);
        searchButton = createAestheticButton("Search", searchButtonColor, Color.WHITE);
        showAllButton = createAestheticButton("Show All", showAllButtonColor, Color.BLACK);
        
        // Set button sizes
        Dimension crudSize = new Dimension(100, 40);
        addButton.setPreferredSize(crudSize);
        updateButton.setPreferredSize(crudSize);
        deleteButton.setPreferredSize(crudSize);
        clearButton.setPreferredSize(crudSize);
        searchButton.setPreferredSize(new Dimension(90, 38));
        showAllButton.setPreferredSize(new Dimension(90, 38));
        
        // Add hover effects
        addHoverEffect(addButton, addButtonColor);
        addHoverEffect(updateButton, updateButtonColor);
        addHoverEffect(deleteButton, deleteButtonColor);
        addHoverEffect(clearButton, clearButtonColor);
        addHoverEffect(searchButton, searchButtonColor);
        addHoverEffect(showAllButton, showAllButtonColor);
        
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        
        // Filter components
        filterHighMarks = new JCheckBox("Only marks > 75");
        filterComputerScience = new JCheckBox("Only Computer Science");
        
        sortByName = new JRadioButton("Sort by Name", true);
        sortByMarks = new JRadioButton("Sort by Marks");
        sortByCourse = new JRadioButton("Sort by Course");
        
        sortGroup = new ButtonGroup();
        sortGroup.add(sortByName);
        sortGroup.add(sortByMarks);
        sortGroup.add(sortByCourse);
        
        // Style checkboxes and radio buttons
        Font checkFont = new Font("Segoe UI", Font.PLAIN, 13);
        filterHighMarks.setFont(checkFont);
        filterHighMarks.setBackground(panelColor);
        filterComputerScience.setFont(checkFont);
        filterComputerScience.setBackground(panelColor);
        sortByName.setFont(checkFont);
        sortByName.setBackground(panelColor);
        sortByMarks.setFont(checkFont);
        sortByMarks.setBackground(panelColor);
        sortByCourse.setFont(checkFont);
        sortByCourse.setBackground(panelColor);
        
        // JSlider
        marksSlider = new JSlider(0, 100, 0);
        marksSlider.setMajorTickSpacing(25);
        marksSlider.setMinorTickSpacing(5);
        marksSlider.setPaintTicks(true);
        marksSlider.setPaintLabels(true);
        marksSlider.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        marksSlider.setPreferredSize(new Dimension(300, 50));
        marksSlider.setBackground(panelColor);
        
        sliderValueLabel = new JLabel("0");
        sliderValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sliderValueLabel.setForeground(updateButtonColor);
        
        // Table setup - Clean with black text, minimal borders
        String[] columns = {"ID", "Student ID", "Name", "Email", "Course", "Marks", "Grade"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(Color.BLACK);
        table.setRowHeight(35);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.BLACK);
        table.setBackground(panelColor);
        table.setBorder(BorderFactory.createEmptyBorder());
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Table header - Clean
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 248, 255));
        header.setForeground(Color.BLACK);
        header.setPreferredSize(new Dimension(header.getWidth(), 38));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                loadSelectedStudent();
            }
        });
        
        // Status labels
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        validationLabel = new JLabel(" ");
        validationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        validationLabel.setForeground(deleteButtonColor);
        
        studentCountLabel = new JLabel("Total Students: 0");
        studentCountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentCountLabel.setForeground(updateButtonColor);
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(Color.BLACK);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }
    
    private JButton createAestheticButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(getBackground().darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(getBackground().brighter());
                } else {
                    g2.setColor(getBackground());
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        return button;
    }
    
    private void addHoverEffect(JButton button, Color originalColor) {
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
                button.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
                button.repaint();
            }
        });
    }
    
    private void layoutComponents() {
        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top Panel - Title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(backgroundColor);
        
        JLabel titleLabel = new JLabel("Student Management System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(updateButtonColor);
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(studentCountLabel, BorderLayout.EAST);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Split into left and right
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(15, 0, 0, 0);
        
        // Left Panel - Student Information Form
        JPanel leftPanel = createFormPanel();
        gbc.gridx = 0;
        gbc.weightx = 0.35;
        centerPanel.add(leftPanel, gbc);
        
        // Right Panel - Student List
        JPanel rightPanel = createListPanel();
        gbc.gridx = 1;
        gbc.weightx = 0.65;
        gbc.insets = new Insets(15, 15, 0, 0);
        centerPanel.add(rightPanel, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Title - No icon
        JLabel title = new JLabel("Student Information");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(updateButtonColor);
        panel.add(title, BorderLayout.NORTH);
        
        // Form fields
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(panelColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 5, 8, 5);
        
        // Labels - No icons
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel idLabel = new JLabel("Student ID:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel courseLabel = new JLabel("Course:");
        courseLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JLabel marksLabel = new JLabel("Marks:");
        marksLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Add to form
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(idLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(studentIdField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(emailLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.3;
        formPanel.add(courseLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(courseBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weightx = 0.3;
        formPanel.add(marksLabel, gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(marksField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 5;
        formPanel.add(validationLabel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        // CRUD Buttons - No icons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(panelColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(panelColor);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Top section - Search and Filters
        JPanel topSection = new JPanel();
        topSection.setBackground(panelColor);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        
        // Title - No icon
        JLabel listTitle = new JLabel("Student List");
        listTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        listTitle.setForeground(updateButtonColor);
        listTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(listTitle);
        topSection.add(Box.createVerticalStrut(15));
        
        // Search row
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchRow.setBackground(panelColor);
        searchRow.add(new JLabel("Search:"));
        searchField.setPreferredSize(new Dimension(250, 38));
        searchRow.add(searchField);
        searchRow.add(searchButton);
        searchRow.add(showAllButton);
        topSection.add(searchRow);
        topSection.add(Box.createVerticalStrut(10));
        
        // Filter row - Checkboxes
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterRow.setBackground(panelColor);
        filterRow.add(filterHighMarks);
        filterRow.add(filterComputerScience);
        topSection.add(filterRow);
        topSection.add(Box.createVerticalStrut(10));
        
        // Sort row - Radio buttons
        JPanel sortRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        sortRow.setBackground(panelColor);
        sortRow.add(new JLabel("Sort by:"));
        sortRow.add(sortByName);
        sortRow.add(sortByMarks);
        sortRow.add(sortByCourse);
        topSection.add(sortRow);
        topSection.add(Box.createVerticalStrut(10));
        
        // Slider row
        JPanel sliderRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        sliderRow.setBackground(panelColor);
        sliderRow.add(new JLabel("Minimum Marks:"));
        sliderRow.add(marksSlider);
        sliderRow.add(new JLabel("Value:"));
        sliderRow.add(sliderValueLabel);
        topSection.add(sliderRow);
        
        panel.add(topSection, BorderLayout.NORTH);
        
        // Table with scroll
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor));
        scrollPane.getViewport().setBackground(panelColor);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupListeners() {
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        searchButton.addActionListener(e -> searchStudents());
        showAllButton.addActionListener(e -> loadStudents());
        clearButton.addActionListener(e -> clearFields());
        
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validateInputs(); }
            public void removeUpdate(DocumentEvent e) { validateInputs(); }
            public void changedUpdate(DocumentEvent e) { validateInputs(); }
        };
        
        nameField.getDocument().addDocumentListener(docListener);
        emailField.getDocument().addDocumentListener(docListener);
        studentIdField.getDocument().addDocumentListener(docListener);
        marksField.getDocument().addDocumentListener(docListener);
        
        filterHighMarks.addActionListener(e -> applyFilters());
        filterComputerScience.addActionListener(e -> applyFilters());
        sortByName.addActionListener(e -> applyFilters());
        sortByMarks.addActionListener(e -> applyFilters());
        sortByCourse.addActionListener(e -> applyFilters());
        
        marksSlider.addChangeListener(e -> {
            int value = marksSlider.getValue();
            sliderValueLabel.setText(String.valueOf(value));
            applyFilters();
        });
        
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
            message.append("Valid number required. ");
            valid = false;
        } else {
            double marks = Double.parseDouble(marksText);
            if (marks < 0 || marks > 100) {
                message.append("Marks 0-100. ");
                valid = false;
            }
        }
        
        if (valid) {
            validationLabel.setText("All fields valid");
            validationLabel.setForeground(addButtonColor);
            addButton.setEnabled(true);
        } else {
            validationLabel.setText(message.toString());
            validationLabel.setForeground(deleteButtonColor);
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
        String sql = "SELECT * FROM students ORDER BY name ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            displayStudentsFromResultSet(rs);
            statusLabel.setText("Loaded all students");
            
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error loading students");
            JOptionPane.showMessageDialog(this, 
                "Database error: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displayStudentsFromResultSet(ResultSet rs) throws SQLException {
        tableModel.setRowCount(0);
        
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
            count++;
        }
        
        studentCountLabel.setText("Total Students: " + count);
    }
    
    private void applyFilters() {
        StringBuilder sql = new StringBuilder("SELECT * FROM students WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (filterHighMarks.isSelected()) {
            sql.append(" AND marks > 75");
        }
        
        if (filterComputerScience.isSelected()) {
            sql.append(" AND course = 'Computer Science'");
        }
        
        int minMarks = marksSlider.getValue();
        if (minMarks > 0) {
            sql.append(" AND marks >= ?");
            params.add(minMarks);
        }
        
        if (sortByName.isSelected()) {
            sql.append(" ORDER BY name ASC");
        } else if (sortByMarks.isSelected()) {
            sql.append(" ORDER BY marks DESC");
        } else if (sortByCourse.isSelected()) {
            sql.append(" ORDER BY course, name ASC");
        } else {
            sql.append(" ORDER BY name ASC");
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = ps.executeQuery();
            displayStudentsFromResultSet(rs);
            
            statusLabel.setText("Filters applied");
            
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Filter error");
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
                     "LOWER(course) LIKE ? " +
                     "ORDER BY name ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String pattern = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            
            ResultSet rs = ps.executeQuery();
            
            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, 
                    "No students found matching '" + keyword + "'",
                    "Search Result", JOptionPane.INFORMATION_MESSAGE);
                loadStudents();
                return;
            }
            
            displayStudentsFromResultSet(rs);
            statusLabel.setText("Found results for: '" + keyword + "'");
            
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Search error");
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
                    "Student added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                loadStudents();
                clearFields();
                statusLabel.setText("Student added");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Error adding student");
            JOptionPane.showMessageDialog(this, 
                "Database Error: " + e.getMessage(),
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
                    "Student updated!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                loadStudents();
                clearFields();
                statusLabel.setText("Student updated");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            statusLabel.setText("Update error");
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
            "Delete this student?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "DELETE FROM students WHERE id=?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                
                ps.setInt(1, selectedStudentId);
                
                int result = ps.executeUpdate();
                
                if (result > 0) {
                    JOptionPane.showMessageDialog(this, 
                        "Student deleted!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                    
                    loadStudents();
                    clearFields();
                    statusLabel.setText("Student deleted");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                statusLabel.setText("Delete error");
            }
        }
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
            JOptionPane.showMessageDialog(this, "All fields required");
            return false;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email");
            return false;
        }
        
        if (!ValidationUtil.isNumeric(marksText)) {
            JOptionPane.showMessageDialog(this, "Marks must be number");
            return false;
        }
        
        double marks = Double.parseDouble(marksText);
        if (marks < 0 || marks > 100) {
            JOptionPane.showMessageDialog(this, "Marks 0-100");
            return false;
        }
        
        return true;
    }
}