CREATE DATABASE library_db;
USE library_db;

CREATE TABLE books(
id INT PRIMARY KEY AUTO_INCREMENT,
title VARCHAR(100) NOT NULL,
author VARCHAR(100) NOT NULL,
status ENUM('issued','available') DEFAULT 'available'
);

CREATE TABLE users(
id INT PRIMARY KEY AUTO_INCREMENT,
first_name VARCHAR(30) NOT NULL,
last_name VARCHAR(30) NOT NULL,
username VARCHAR(50) UNIQUE NOT NULL,
password VARCHAR(50) NOT NULL,
role ENUM('user','admin') NOT NULL
);

CREATE TABLE issue_books(
issue_id INT PRIMARY KEY AUTO_INCREMENT,
book_id INT NOT NULL,
user_id INT NOT NULL,
issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
return_date TIMESTAMP NULL,
FOREIGN KEY (book_id) REFERENCES books(id),
FOREIGN KEY (user_id) REFERENCES users(id)
);

--  Admin User
INSERT INTO users (first_name, last_name, username, password, role)
VALUES ('Prasanna', 'Talekar', 'admin', 'prasanna', 'admin');

--  Normal Users
INSERT INTO users (first_name, last_name, username, password, role)
VALUES 
('John', 'Doe', 'johndoe', '1234', 'user'),
('Jane', 'Smith', 'janesmith', 'abcd', 'user');

--  Book Data
INSERT INTO books (title, author) VALUES 
('Java Programming', 'James Gosling'),
('Clean Code', 'Robert C. Martin'),
('Effective Java', 'Joshua Bloch');

-- Book Data
INSERT INTO books (title, author) VALUES
('The Pragmatic Programmer', 'Andrew Hunt'),
('Design Patterns', 'Erich Gamma'),
('Introduction to Algorithms', 'Thomas H. Cormen'),
('Artificial Intelligence: A Modern Approach', 'Stuart Russell'),
('Python Crash Course', 'Eric Matthes'),
('You Don\'t Know JS', 'Kyle Simpson'),
('Refactoring', 'Martin Fowler'),
('Structure and Interpretation of Computer Programs', 'Harold Abelson'),
('The Mythical Man-Month', 'Frederick P. Brooks Jr.'),
('Head First Design Patterns', 'Eric Freeman'),
('Deep Learning', 'Ian Goodfellow'),
('Cracking the Coding Interview', 'Gayle Laakmann McDowell'),
('Code Complete', 'Steve McConnell'),
('Operating System Concepts', 'Abraham Silberschatz'),
('Database System Concepts', 'Abraham Silberschatz'),
('Computer Networks', 'Andrew S. Tanenbaum'),
('Eloquent JavaScript', 'Marijn Haverbeke'),
('Learning SQL', 'Alan Beaulieu'),
('Data Structures and Algorithms in Java', 'Robert Lafore'),
('The Art of Computer Programming', 'Donald Knuth');
  
