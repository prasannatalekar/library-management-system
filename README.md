# 📚 Library Management System (Java + JDBC + MySQL)

A **console-based Java application** designed to simulate a real-world Library Management System. This project uses JDBC to connect with a MySQL database and supports role-based access for admins and users.

---

## 🚀 Features

- ✅ Role-based login system (Admin / User)
- 📘 Admin Capabilities:
  - View Books
  - Search Book
  - Add new books
  - Delete books
  - View users
  - Delete user 
  - View issued book
- 👤 User Capabilities:
  - View all books
  - Search Book
  - Issue Book
  - Return books
- 📊 Book status tracking (`available` / `issued`)
- 🗂️ Modular architecture (`Main`, `UserService`, `AdminService`, `LibraryService`, etc.)

---

## 🛠️ Technologies Used

- **Java** 
- **JDBC** (Java Database Connectivity)
- **MySQL** (Backend database)
- **Eclipse IDE** 
- **SQL** (Joins, CRUD operations)

---

## 🧱 Database Schema

- `users` – Admin and user accounts  
- `books` – Book inventory  
- `issue_books` – Tracks issued/returned books

---

## 🏁 Getting Started

### Prerequisites
- Java 8+
- MySQL Server
- JDBC Driver
- IDE (Eclipse/IntelliJ)
