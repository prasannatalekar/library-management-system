import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminService {
    static Connection con = DBConnection.getConnection();
    static Scanner sc = new Scanner(System.in);

    public static void addBook() throws SQLException {
        System.out.println("Enter title: ");
        String title = sc.nextLine().trim();
        System.out.println("Enter author: ");
        String author = sc.nextLine().trim();

        String checkSQL = "SELECT COUNT(*) FROM books WHERE LOWER(title)=LOWER(?) AND LOWER(author)=LOWER(?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(checkSQL);
            ps.setString(1, title);
            ps.setString(2, author);
            rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                System.out.println("This book is already in library.");
            } else {
                String insertSQL = "INSERT INTO books (title,author) VALUES(?,?)";
                PreparedStatement insertStmt = con.prepareStatement(insertSQL);
                try {
                    insertStmt.setString(1, title);
                    insertStmt.setString(2, author);
                    if (insertStmt.executeUpdate() > 0) {
                        System.out.println("Book added successfully.");
                    } else {
                        System.out.println("Failed to add book.");
                    }
                } finally {
                    insertStmt.close();
                }
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public static void deleteBook() throws SQLException {
        System.out.println("Enter book id to delete book: ");
        int bookId = sc.nextInt();
        sc.nextLine();

        String checkSQL = "SELECT COUNT(*) FROM books WHERE id=?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(checkSQL);
            ps.setInt(1, bookId);
            rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                String deleteSQL = "DELETE FROM books WHERE id=?";
                PreparedStatement deleteStmt = con.prepareStatement(deleteSQL);
                try {
                    deleteStmt.setInt(1, bookId);
                    if (deleteStmt.executeUpdate() > 0) {
                        System.out.println("Book deleted successfully.");
                    } else {
                        System.out.println("Failed to delete book.");
                    }
                } finally {
                    deleteStmt.close();
                }
            } else {
                System.out.println("Invalid book id.");
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public static void viewUsers() throws SQLException {
        String sql = "SELECT id AS User_Id,CONCAT(first_name,' ',last_name) AS Name ,username FROM users WHERE role='user'";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            boolean userFound = false;

            while (rs.next()) {
                if (!userFound) {
                    System.out.println("------------------------------------------------------------");
                    System.out.printf("%-10s | %-25s | %-20s%n", "User ID", "Name", "Username");
                    System.out.println("------------------------------------------------------------");
                    userFound = true;
                }
                System.out.printf("%-10d | %-25s | %-20s%n",
                    rs.getInt("User_Id"),
                    rs.getString("Name"),
                    rs.getString("username"));
            }
            if (!userFound) {
                System.out.println("User not found.");
            } else {
                System.out.println("------------------------------------------------------------");
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public static void deleteUser() throws SQLException {
        System.out.println("Enter user ID to delete: ");
        int userId = sc.nextInt();
        sc.nextLine();

        String sql = "SELECT COUNT(*) FROM users WHERE id=? AND role='user'";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            rs = ps.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                String deleteSQL = "DELETE FROM users WHERE id=? AND role='user'";
                PreparedStatement deleteStmt = con.prepareStatement(deleteSQL);
                try {
                    deleteStmt.setInt(1, userId);
                    if (deleteStmt.executeUpdate() > 0) {
                        System.out.println("User deleted successfully.");
                    } else {
                        System.out.println("Failed to delete user.");
                    }
                } finally {
                    deleteStmt.close();
                }
            } else {
                System.out.println("Invalid user ID.");
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public static void viewIssuedBooks() throws SQLException {
        String sql = "SELECT b.id AS Book_ID, b.title AS Book_Name, b.author AS Author, "
            + "CONCAT(u.first_name,' ',u.last_name) AS Issued_by,"
            + "ib.issue_date FROM books AS b "
            + "JOIN issue_books AS ib ON ib.book_id=b.id "
            + "JOIN users AS u ON ib.user_id=u.id "
            + "WHERE b.status='issued';";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            boolean issuedFound = false;

            while (rs.next()) {
                if (!issuedFound) {
                    System.out.println("----------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-8s | %-35s | %-20s | %-20s | %-20s%n",
                        "Book ID", "Book Name", "Author", "Issued By", "Issue Date");
                    System.out.println("----------------------------------------------------------------------------------------------------------------------");
                    issuedFound = true;
                }
                System.out.printf("%-8d | %-35s | %-20s | %-20s | %-20s%n",
                    rs.getInt("Book_ID"),
                    rs.getString("Book_Name"),
                    rs.getString("Author"),
                    rs.getString("Issued_by"),
                    rs.getString("issue_date"));
            }
            if (!issuedFound) {
                System.out.println("No issued books.");
            } else {
                System.out.println("----------------------------------------------------------------------------------------------------------------------");
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }
}
