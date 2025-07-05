import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class LibraryService {
    static Connection con = DBConnection.getConnection();
    static Scanner sc = new Scanner(System.in);

    public static void viewBooks() throws SQLException {
        String sql = "SELECT * FROM books";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            boolean bookFound = false;
            while (rs.next()) {
                if (!bookFound) {
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-3s | %-55s | %-25s | %-10s%n", "ID", "Title", "Author", "Status");
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    bookFound = true;
                }
                System.out.printf("%-3d | %-55s | %-25s | %-10s%n",
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("status"));
            }
            if (!bookFound) {
                System.out.println("No books found! ");
            } else {
                System.out.println("--------------------------------------------------------------------------------------------------------");
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public static void searchBook() throws SQLException {
        System.out.println("Enter title: ");
        String title = sc.nextLine().trim();

        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?)";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, "%" + title + "%");
            rs = ps.executeQuery();

            boolean bookFound = false;
            while (rs.next()) {
                if (!bookFound) {
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-3s | %-55s | %-25s | %-10s%n", "ID", "Title", "Author", "Status");
                    System.out.println("--------------------------------------------------------------------------------------------------------");
                    bookFound = true;
                }
                System.out.printf("%-3d | %-55s | %-25s | %-10s%n",
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("status"));
            }
            if (!bookFound) {
                System.out.println("No book found with title : " + title);
            } else {
                System.out.println("--------------------------------------------------------------------------------------------------------");
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public static void issueBook(User user) throws SQLException {
        if (user == null) {
            System.out.println("Login first to issue book.");
            return;
        }

        System.out.println("Enter book ID to issue: ");
        int bookID = sc.nextInt();
        sc.nextLine();

        String sql = "SELECT * FROM books WHERE id=?";
        PreparedStatement ps = null;
        ResultSet rs = null;

        PreparedStatement dupliStmt = null;
        ResultSet dupRs = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, bookID);
            rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getString("status").equalsIgnoreCase("available")) {
                    String duplicateCheck = "SELECT * FROM issue_books WHERE user_id = ? AND book_id = ? AND return_date IS NULL";
                    dupliStmt = con.prepareStatement(duplicateCheck);
                    dupliStmt.setInt(1, user.getId());
                    dupliStmt.setInt(2, bookID);
                    dupRs = dupliStmt.executeQuery();

                    if (dupRs.next()) {
                        System.out.println("You have already issued this book and not returned it.");
                        return;
                    }

                    String updateSql = "UPDATE books set status='issued' WHERE id=?";
                    updateStmt = con.prepareStatement(updateSql);
                    updateStmt.setInt(1, bookID);

                    if (updateStmt.executeUpdate() > 0) {
                        String insertSql = "INSERT INTO issue_books (book_id,user_id) VALUES (?,?)";
                        insertStmt = con.prepareStatement(insertSql);
                        insertStmt.setInt(1, bookID);
                        insertStmt.setInt(2, user.getId());

                        if (insertStmt.executeUpdate() > 0) {
                            System.out.println("Book issued successfully.");
                        } else {
                            System.out.println("Failed to insert issue record.");
                        }
                    } else {
                        System.out.println("Failed to issue book.");
                    }
                } else {
                    System.out.println("Book not available.");
                }
            } else {
                System.out.println("Book does not exists.");
            }
        } finally {
            if (dupRs != null) dupRs.close();
            if (dupliStmt != null) dupliStmt.close();
            if (updateStmt != null) updateStmt.close();
            if (insertStmt != null) insertStmt.close();
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public static void returnBook(User user) throws SQLException {
        if (user == null) {
            System.out.println("Login first to return book.");
            return;
        }

        String sql = "SELECT ib.book_id, CONCAT(u.first_name,' ',u.last_name) AS Name , b.title AS Issued_books , ib.issue_date , "
            + "IFNULL(ib.return_date,'Not Returned') AS return_date "
            + "FROM books AS b "
            + "JOIN issue_books AS ib ON ib.book_id=b.id "
            + "JOIN users AS u ON u.id=ib.user_id "
            + "WHERE ib.user_id=? AND ib.return_date IS NULL";

        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement checkBook = null;
        ResultSet resultSet = null;
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;

        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, user.getId());
            rs = ps.executeQuery();

            boolean hasBooks = false;
            while (rs.next()) {
                if (!hasBooks) {
                    System.out.println("Issued books by : " + rs.getString("Name"));
                    System.out.println("---------------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-8s | %-55s | %-20s | %-15s%n", "Book ID", "Title", "Issue Date", "Return Date");
                    System.out.println("---------------------------------------------------------------------------------------------------------------");
                    hasBooks = true;
                }
                System.out.printf("%-8d | %-55s | %-20s | %-15s%n",
                    rs.getInt("book_id"),
                    rs.getString("Issued_books"),
                    rs.getString("issue_date"),
                    rs.getString("return_date"));
            }

            if (!hasBooks) {
                System.out.println("No book issued by you.");
                return;
            } else {
                System.out.println("---------------------------------------------------------------------------------------------------------------");
            }

            System.out.println("Enter book id to return: ");
            int bookId = sc.nextInt();
            sc.nextLine();

            String check = "SELECT * FROM issue_books WHERE book_id=? AND user_id=? AND return_date IS NULL";
            checkBook = con.prepareStatement(check);
            checkBook.setInt(1, bookId);
            checkBook.setInt(2, user.getId());
            resultSet = checkBook.executeQuery();

            if (resultSet.next()) {
                String updateBookSQL = "UPDATE books SET status='available' WHERE id=?";
                ps1 = con.prepareStatement(updateBookSQL);
                ps1.setInt(1, bookId);

                if (ps1.executeUpdate() > 0) {
                    String updateIssueSQL = "UPDATE issue_books SET return_date=CURDATE() WHERE user_id=? AND book_id=? AND return_date IS NULL ";
                    ps2 = con.prepareStatement(updateIssueSQL);
                    ps2.setInt(1, user.getId());
                    ps2.setInt(2, bookId);

                    if (ps2.executeUpdate() > 0) {
                        System.out.println("Book returned successfully.");
                    } else {
                        System.out.println("Failed to return the book.");
                    }
                } else {
                    System.out.println("Failed to return the book.");
                }
            } else {
                System.out.println("Invalid Book ID!");
            }

        } finally {
            if (resultSet != null) resultSet.close();
            if (checkBook != null) checkBook.close();
            if (ps1 != null) ps1.close();
            if (ps2 != null) ps2.close();
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }
}
