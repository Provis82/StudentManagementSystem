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
        setSize(750,500);
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
                        "Student Management System"));

        helpMenu.add(about);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void createForm() {

        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5,2,10,10));

        form.add(new JLabel("Name"));
        nameField = new JTextField();
        form.add(nameField);

        form.add(new JLabel("Email"));
        emailField = new JTextField();
        form.add(emailField);

        form.add(new JLabel("Course"));
        courseBox = new JComboBox<>(new String[]{
                "Java","Database","Networking","AI"
        });
        form.add(courseBox);

        form.add(new JLabel("Marks"));
        marksField = new JTextField();
        form.add(marksField);

        JButton addButton = new JButton("Add Student");
        JButton deleteButton = new JButton("Delete Student");

        form.add(addButton);
        form.add(deleteButton);

        panel.add(form,BorderLayout.NORTH);

        String columns[] = {"ID","Name","Email","Course","Marks"};

        model = new DefaultTableModel(columns,0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane,BorderLayout.CENTER);

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

        String name = nameField.getText();
        String email = emailField.getText();
        String course = courseBox.getSelectedItem().toString();
        double marks = Double.parseDouble(marksField.getText());

        try {

            Connection conn = DatabaseConnection.getConnection();

            String sql =
                    "INSERT INTO students(name,email,course,marks) VALUES(?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1,name);
            ps.setString(2,email);
            ps.setString(3,course);
            ps.setDouble(4,marks);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Student Added");

            loadStudents();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteStudent() {

        int row = table.getSelectedRow();

        if(row == -1) {
            JOptionPane.showMessageDialog(this,"Select a student first");
            return;
        }

        int id = (int) model.getValueAt(row,0);

        try {

            Connection conn = DatabaseConnection.getConnection();

            String sql = "DELETE FROM students WHERE id=?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1,id);

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,"Student Deleted");

            loadStudents();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}