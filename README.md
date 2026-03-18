# 🎓 Student Management System

A Java-based desktop application for managing student records, developed as part of Year 2 CSE Lab 1.

## 👥 Team Members
- **Providence ISINGIZWE(Provis82)** — Login Page, OOP Base Classes, Database Connection
- **Leonce MUGISHA(madboy-creator)** — Main Management Page, CRUD Operations, Validation

## 🛠️ Technologies
- Java (Swing GUI)
- MySQL 8.0
- JDBC (MySQL Connector/J)
- Apache NetBeans / VS Code
- Git & GitHub

## ✅ Features
- Secure login with progress bar animation
- Add, Update, Delete, Search students
- Real-time field validation
- Title Case & case-insensitive search
- JTable, JTabbedPane, JSlider, JComboBox
- Grade auto-calculation from marks

## 🗄️ Database Setup
1. Install MySQL 8.0
2. Run `SMS_Project/src/sms/database/schema.sql`
3. Update password in `DatabaseConnection.java`

## 🚀 How to Run
1. Clone the repository
2. Open in NetBeans or VS Code
3. Add MySQL Connector JAR to libraries
4. Run `Main.java`

## 📁 Project Structure
```
SMS_Project/
├── src/sms/
│   ├── database/     → DatabaseConnection.java
│   ├── gui/          → LoginFrame.java, MainFrame.java
│   ├── model/        → Person.java, Student.java, DatabaseOperations.java
│   ├── util/         → ValidationUtil.java
│   └── Main.java
└── lib/              → MySQL Connector JAR
```

## 🔗 GitHub
https://github.com/Provis82/StudentManagementSystem
```

5. Scroll down → add commit message:
```
Add comprehensive README with project documentation
