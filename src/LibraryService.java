import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class LibraryService {
    static Connection con = DBConnection.getConnection();

    public static void viewBooks() throws SQLException {
        String sql = "SELECT * FROM books WHERE is_deleted=false";

        try (PreparedStatement ps=con.prepareStatement(sql);
        	 ResultSet rs=ps.executeQuery()) {

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
                return;
            }
            
            System.out.println("--------------------------------------------------------------------------------------------------------");
            
        } catch (Exception e) {
        	System.out.println("Something went wrong in LibraryService.viewBooks() : "+e.getMessage());
			e.printStackTrace();
		}
    }

    public static void searchBook(Scanner sc) throws SQLException {
        System.out.println("Enter title: ");
        String title = sc.nextLine().trim();

        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE LOWER(?) AND is_deleted=false";

        try (PreparedStatement ps=con.prepareStatement(sql)) {
        	
            ps.setString(1, "%" + title + "%");

            try (ResultSet rs=ps.executeQuery()) {
			
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
                     return;
                 } 
                 
                 System.out.println("--------------------------------------------------------------------------------------------------------");
                             	
			} catch (Exception e) {
				System.out.println("Something went wrong in LibraryService.searchBook() : "+e.getMessage());
				e.printStackTrace();
			}
        }
    }

    public static void issueBook(User user, Scanner sc) throws SQLException {
        if (user == null) {
            System.out.println("Login first to issue book.");
            return;
        }

        System.out.println("Enter book ID to issue book : ");
        int bookID = Main.getIntInput();
        
        String getBookSql = "SELECT * FROM books WHERE id=? AND is_deleted=false";
        String updateStatusSql = "UPDATE books SET status='issued' WHERE id=?";
        String issueBookSql = "INSERT INTO issue_books (book_id,user_id) VALUES (?,?)";
        
        try (PreparedStatement getBookStmt=con.prepareStatement(getBookSql);
        	 PreparedStatement updateStatusStmt=con.prepareStatement(updateStatusSql);
        	 PreparedStatement issueBookStmt=con.prepareStatement(issueBookSql)) {
			        	
        	getBookStmt.setInt(1, bookID);
        	
        	try (ResultSet rs=getBookStmt.executeQuery()) {
				
        		if(!rs.next()) {
        			System.out.println("Book not found. Enter correct id!");
        			return;
        		}
        		
        		if(rs.getString("status").equalsIgnoreCase("issued")) {
        			System.out.println("Book is already issued!");
        			return;
        		}
        	
        		con.setAutoCommit(false);
        		
        		updateStatusStmt.setInt(1, bookID);
        		int updateResult = updateStatusStmt.executeUpdate();
        		
        		issueBookStmt.setInt(1, bookID);
            	issueBookStmt.setInt(2, user.getId());
        		int insertResult = issueBookStmt.executeUpdate();
        		
        		if(updateResult <= 0 || insertResult <= 0) {
        			con.rollback();
        			System.out.println("Failed to issue book!");
        			return;
        		}
        		
        		con.commit();
        		System.out.println("Book issued successfully.");
        		
			} catch (Exception e) {
				con.rollback();
				System.out.println("Something went wrong in LibraryService.issueBook() : "+e.getMessage());
				e.printStackTrace();
			}
        	
		} finally {
			con.setAutoCommit(true);
		}
        
    }

    public static void returnBook(User user, Scanner sc) throws SQLException {
        if (user == null) {
            System.out.println("Login first to return book.");
            return;
        }

        String issuedBooksSql = "SELECT ib.book_id, CONCAT(u.first_name,' ',u.last_name) AS Name , b.title AS Issued_books , ib.issue_date , "
            + "IFNULL(ib.return_date,'Not Returned') AS return_date "
            + "FROM books AS b "
            + "JOIN issue_books AS ib ON ib.book_id=b.id "
            + "JOIN users AS u ON u.id=ib.user_id "
            + "WHERE ib.user_id=? AND ib.return_date IS NULL";
        
        String checkBookIdSql = "SELECT 1 FROM issue_books WHERE book_id=? AND user_id=? AND return_date IS NULL";
        String updateStatusSql = "UPDATE books SET status='available' WHERE id=?";
        String returnBookSql = "UPDATE issue_books SET return_date=CURRENT_TIMESTAMP WHERE user_id=? AND book_id=? AND return_date IS NULL";
        
        try (PreparedStatement issueBooksStmt=con.prepareStatement(issuedBooksSql);
        	 PreparedStatement checkBookIdStmt=con.prepareStatement(checkBookIdSql);
        	 PreparedStatement updateStatusStmt=con.prepareStatement(updateStatusSql);
        	 PreparedStatement returnBookStmt=con.prepareStatement(returnBookSql)) {
			
        	issueBooksStmt.setInt(1, user.getId());
        	
        	try (ResultSet rs=issueBooksStmt.executeQuery()) {
				
        		boolean hasBooks=false;
        		
        		while(rs.next()) {
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
        			System.out.println("No books issued by you.");
        			return;
				}
        		
        		System.out.println("---------------------------------------------------------------------------------------------------------------");
        		
        		System.out.println("Enter book id to return book : ");        		
        		int bookId=Main.getIntInput();
        		
        		checkBookIdStmt.setInt(1, bookId);
        		checkBookIdStmt.setInt(2, user.getId());
        		
        		try (ResultSet checkBookId=checkBookIdStmt.executeQuery()) {
					
        			if(!checkBookId.next()) {
        				System.out.println("Invalid book id!");
        				return;
        			}
        		}
        		
        		con.setAutoCommit(false);
        		
        		try {
				        			
        			updateStatusStmt.setInt(1, bookId);
        			int updateStatusResult=updateStatusStmt.executeUpdate();
        			
        			returnBookStmt.setInt(1, user.getId());
        			returnBookStmt.setInt(2, bookId);
        			int returnBookResult=returnBookStmt.executeUpdate();
        			
        			if (updateStatusResult<=0 || returnBookResult<=0) {
						con.rollback();
						System.out.println("Failed to return book!");
						return;
					}
        			
        			con.commit();
        			System.out.println("Book ID " + bookId + " returned successfully.");
        		
        		} catch (Exception e) {
        			con.rollback();
        			System.out.println("Something went wrong in LibraryService.returnBook() : "+e.getMessage());
    				e.printStackTrace();
				}
        			
			} catch (Exception e) {
				System.out.println("Something went wrong in LibraryService.returnBook() : "+e.getMessage());
				e.printStackTrace();
			}
        	
		} catch (Exception e) {
			System.out.println("Something went wrong in LibraryService.returnBook() : "+e.getMessage());
			e.printStackTrace();
			
		} finally {
			con.setAutoCommit(true);
		}
        
    }
}
