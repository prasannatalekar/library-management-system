# 📚 Library Management System (Java + JDBC + MySQL)

A console-based Java application that simulates a real-world Library Management System. Built with JDBC for MySQL connectivity, featuring secure authentication, role-based access control, and robust data management.

---

## 🚀 What's New (v2.0)

| Feature | Description |
|---|---|
| 🔐 Password Hashing | Passwords secured using **BCrypt** hashing |
| 🗑️ Soft Delete | Books and users are never hard deleted — safely deactivated |
| 🔄 Book Restore | Re-adding a soft-deleted book restores it automatically |
| 🚫 Block / Unblock Users | Admin can block and unblock users instead of deleting |
| 💳 Transaction Management | Issue/Return operations use `commit` / `rollback` |
| 🔧 Improved JDBC Handling | All resources managed with `try-with-resources` |
| 👁️ Admin View Books | Admin sees all books including deleted ones with status |
| 🛡️ Secure Login | Unified error messages prevent username enumeration |

---

## 🎯 Features

### 👤 User Capabilities
- Register new account
- Login securely with BCrypt password verification
- View all available books
- Search book by title
- Issue a book
- Return a book

### 🔑 Admin Capabilities
- Login securely
- View all books (including deleted) with availability and delete status
- Search book by title
- Add new book (auto-restores if previously deleted)
- Delete book (soft delete — cannot delete issued books)
- View all users with Active / Blocked status
- Block user
- Unblock user
- View all currently issued books

---

## 🛠️ Technologies Used

- **Java 8+**
- **JDBC** (Java Database Connectivity)
- **MySQL** (Backend database)
- **BCrypt** (via `jbcrypt` library for password hashing)
- **Eclipse IDE**
- **SQL** (Joins, Transactions, CRUD operations)

---

## 🧱 Project Structure
```
├── Main.java              # Entry point, menu navigation
├── User.java              # User model class
├── DBConnection.java      # Singleton DB connection
├── HashPassword.java      # BCrypt password hashing utility
├── UserService.java       # Register and login logic
├── LibraryService.java    # User-side book operations
├── AdminService.java      # Admin-side book and user management
└── AdminCreation.java     # Standalone admin account seeder
```

---

## 🗄️ Database Schema
```sql
CREATE DATABASE library_db;
USE library_db;
SET sql_safe_updates = 0;

CREATE TABLE books (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    title       VARCHAR(100) NOT NULL,
    author      VARCHAR(100) NOT NULL,
    status      ENUM('issued', 'available') DEFAULT 'available',
    is_deleted  BOOLEAN DEFAULT false
);

CREATE TABLE users (
    id          INT PRIMARY KEY AUTO_INCREMENT,
    first_name  VARCHAR(30) NOT NULL,
    last_name   VARCHAR(30) NOT NULL,
    username    VARCHAR(50) UNIQUE NOT NULL,
    password    VARCHAR(300) NOT NULL,
    role        ENUM('user', 'admin') NOT NULL,
    is_deleted  BOOLEAN DEFAULT false
);

CREATE TABLE issue_books (
    issue_id    INT PRIMARY KEY AUTO_INCREMENT,
    book_id     INT NOT NULL,
    user_id     INT NOT NULL,
    issue_date  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_date TIMESTAMP NULL,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

> **Note:** `books` table must be created before `issue_books` due to foreign key dependency. `is_deleted` handles soft delete for both `books` and `users`.

---

## 🏁 Getting Started

### Prerequisites
- Java 8+
- MySQL Server
- JDBC MySQL Connector (`mysql-connector-j-9.2.0`)
- jBCrypt library (`jbcrypt-0.4.jar`)
- IDE (Eclipse / IntelliJ)

### Setup Steps

**1. Clone the repository**
```bash
git clone https://github.com/prasannatalekar/library-management-system.git
```

**2. Import the project into your IDE**

**3. Add required JAR files to build path**
- `mysql-connector-j-9.2.0.jar`
- `jbcrypt-0.4.jar`

**4. Create the database**

Run the schema SQL provided above in your MySQL client.

**5. Update DB credentials in `DBConnection.java`**
```java
private static final String URL = "jdbc:mysql://localhost:3306/library_db";
private static final String USER = "your_mysql_username";
private static final String PASSWORD = "your_mysql_password";
```

**6. Create admin account**

Run `AdminCreation.java` as a standalone Java application once before starting the main project. This will create your admin account with a BCrypt hashed password.

**7. Run the project**

Run `Main.java` and use the credentials created in step 6 to login as admin.

---

## 🔐 Security Highlights

- Passwords are never stored in plain text — BCrypt hashing with salt
- Login returns unified `"Invalid credentials!"` for both wrong username and wrong password — prevents username enumeration attacks
- Soft delete ensures no data is permanently lost
- Issued books cannot be deleted until returned
- Blocked users cannot login

---

## 📌 Key Concepts Demonstrated

- JDBC with `PreparedStatement` and `try-with-resources`
- Transaction management (`setAutoCommit`, `commit`, `rollback`)
- BCrypt password hashing and verification
- Soft delete pattern
- Role-based access control
- Singleton design pattern for DB connection
- Modular service-layer architecture
