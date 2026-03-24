import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminService {
    static Connection con = DBConnection.getConnection();
    
    public static void viewBooks() {
    	String viewBooksSql = "SELECT id, title, author, status, " +
                "CASE WHEN is_deleted=true THEN 'Deleted' ELSE 'Active' END AS book_status " +
                "FROM books";
    	
    	try (PreparedStatement viewBookStmt=con.prepareStatement(viewBooksSql);
    		 ResultSet viewBooksResult=viewBookStmt.executeQuery()) {
			
    		boolean bookFound=false;
    		
    		while(viewBooksResult.next()) {
    			if (!bookFound) {
                    System.out.println("----------------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-3s | %-55s | %-25s | %-17s | %-10s%n", "ID", "Title", "Author", "Available Status", "Book Status");
                    System.out.println("----------------------------------------------------------------------------------------------------------------------------");
                    bookFound = true;
                }
                System.out.printf("%-3d | %-55s | %-25s | %-17s | %-10s%n",
                	viewBooksResult.getInt("id"),
                	viewBooksResult.getString("title"),
                	viewBooksResult.getString("author"),
                	viewBooksResult.getString("status"),
                	viewBooksResult.getString("book_status"));
    		}
    		
    		if (!bookFound) {
				System.out.println("No book found!");
				return;
			}
    		
            System.out.println("----------------------------------------------------------------------------------------------------------------------------");
    		
		} catch (Exception e) {
			System.out.println("Something went wrong in AdminService.viewBooks() : " + e.getMessage());
	        e.printStackTrace();
		}
    }

    public static void addBook(Scanner sc) throws SQLException {
        System.out.println("Enter title: ");
        String title = sc.nextLine().trim();
        System.out.println("Enter author: ");
        String author = sc.nextLine().trim();

        String checkSQL = "SELECT id,is_deleted FROM books WHERE LOWER(title)=LOWER(?) AND LOWER(author)=LOWER(?)";        
        String addBookSql="INSERT INTO books (title,author) VALUES (?,?)";
        
        try(PreparedStatement checkBookStmt=con.prepareStatement(checkSQL);
        	PreparedStatement addBookStmt=con.prepareStatement(addBookSql)){
        	
        	checkBookStmt.setString(1, title);
        	checkBookStmt.setString(2, author);
        	
        	try(ResultSet checkBookResult=checkBookStmt.executeQuery()){
        		if (checkBookResult.next()) {
					
        			int bookId=checkBookResult.getInt("id");
        			
        			if(!checkBookResult.getBoolean("is_deleted")) {
        				System.out.println("Book already exists in library!");
                        return;
        			} 
        			else {
        				String restoreBook="UPDATE books SET is_deleted=false WHERE id=?";
        				
        				try (PreparedStatement restoreBookStmt=con.prepareStatement(restoreBook)) {
							restoreBookStmt.setInt(1, bookId);
							
							if (restoreBookStmt.executeUpdate()<=0) {
								System.out.println("Failed to restore book!");
								return;
							}
							System.out.println("Book restored successfully.");
						}
        				return;
					}
        			
				}
        		
        		addBookStmt.setString(1, title);
        		addBookStmt.setString(2, author);
        		int addBookResult=addBookStmt.executeUpdate();
        		
        		if (addBookResult<=0) {
					System.out.println("Failed to add book!");
					return;
				}
        		
        		System.out.println("Book \"" + title + "\" by " + author + " added successfully.");
        	}	
        	
        } catch (Exception e) {
			System.out.println("Something went wrong in AdminService.addBook() : "+e.getMessage());
			e.printStackTrace();
		}
    }

    public static void deleteBook(Scanner sc) throws SQLException {
        System.out.println("Enter book id to delete book: ");
        int bookId = Main.getIntInput();

        String checkBookSql = "SELECT status FROM books WHERE id=? AND is_deleted=false";       
        String deleteBookSql="UPDATE books SET is_deleted=true WHERE id=?";
        
        try (PreparedStatement checkBookStmt=con.prepareStatement(checkBookSql);
        	 PreparedStatement deleteBookStmt=con.prepareStatement(deleteBookSql)) {
			
        	checkBookStmt.setInt(1, bookId);
        	
        	try (ResultSet checkBookResul=checkBookStmt.executeQuery()) {
				
        		if (!checkBookResul.next()) {
					System.out.println("Invalid book id!");
					return;
				}
        		
        		if (checkBookResul.getString("status").equalsIgnoreCase("issued")) {
					System.out.println("This book is currently issued and cannot be deleted.");
					return;
				}
        		
        		deleteBookStmt.setInt(1, bookId);
        		int deleteBookResult=deleteBookStmt.executeUpdate();
        		
        		if(deleteBookResult<=0) {
        			System.out.println("Failed to delete book!");
        			return;
        		}
        		
        		System.out.println("Book deleted successfully.");
        		
			}
        	
		} catch (Exception e) {
			System.out.println("Something went wrong in AdminService.deleteBook() : "+e.getMessage());
			e.printStackTrace();
		}
    }

    public static void viewUsers() throws SQLException {
    	
    	String viewUsersSql = "SELECT id AS User_Id, CONCAT(first_name,' ',last_name) AS Name, " +
                "username, " +
                "CASE WHEN is_deleted = true THEN 'Blocked' ELSE 'Active' END AS Status " +
                "FROM users WHERE role='user'";
    	
    	try (PreparedStatement viewUsersStmt=con.prepareStatement(viewUsersSql);
    		 ResultSet viewUsersResult=viewUsersStmt.executeQuery()) {
			
    			boolean userFound=false;
    			
				while(viewUsersResult.next()) {
					if (!userFound) {
						System.out.println("-----------------------------------------------------------------------");
		                System.out.printf("%-10s | %-25s | %-20s | %-10s%n","User ID", "Name", "Username", "Status");
		                System.out.println("-----------------------------------------------------------------------");
		                userFound = true;
					}
					System.out.printf("%-10d | %-25s | %-20s | %-10s%n",
							viewUsersResult.getInt("User_Id"),
							viewUsersResult.getString("Name"),
							viewUsersResult.getString("username"),
							viewUsersResult.getString("Status"));
				}
				
				if (!userFound) {
					System.out.println("Users not found!");
					return;
				}
		        System.out.println("-----------------------------------------------------------------------");
			
		} catch (Exception e) {
			System.out.println("Something went wrong in AdminService.viewUsers() : "+e.getMessage());
			e.printStackTrace();
		}
    }

    public static void blockUser(Scanner sc) throws SQLException {
        System.out.println("Enter user id to block user : ");
        int userId = Main.getIntInput();
        
        String blockUserSql = "UPDATE users SET is_deleted=true WHERE id=? AND role='user' AND is_deleted=false";
        
        try (PreparedStatement blockUserStmt=con.prepareStatement(blockUserSql)) {
			
        	blockUserStmt.setInt(1, userId);
        	int blockUserResult = blockUserStmt.executeUpdate();
        	
        	if (blockUserResult <= 0) {
				System.out.println("Invalid user ID OR user already blocked.");
				return;
			}
        	
        	System.out.println("User has been blocked successfully.");
        	
		} catch (Exception e) {
			System.out.println("Something went wrong in AdminService.blockUsers() : "+e.getMessage());
			e.printStackTrace();
		}
    }
    
    public static void unblockUser(Scanner sc) throws SQLException {
    	System.out.println("Enter user ID to unblock user : ");
    	int userId=Main.getIntInput();
    	
    	String unblockUserSql = "UPDATE users SET is_deleted=false WHERE id=? AND role='user' AND is_deleted=true";
    	try (PreparedStatement unblockUserStmt=con.prepareStatement(unblockUserSql)) { 
			
    		unblockUserStmt.setInt(1, userId);
    		int unblockUserResult=unblockUserStmt.executeUpdate();
    		
    		if (unblockUserResult <= 0) {
				System.out.println("Invalid user ID OR user already unblocked.");
				return;
			}
    		
    		System.out.println("User has been unblocked successfully.");
    		
		} catch (Exception e) {
			System.out.println("Something went wrong in AdminService.unblockUsers() : "+e.getMessage());
			e.printStackTrace();
		}
    	
    }

    public static void viewIssuedBooks() throws SQLException {

    	String issuedBooksSql = "SELECT b.id AS Book_ID, b.title AS Book_Name, b.author AS Author, "
                + "CONCAT(u.first_name,' ',u.last_name) AS Issued_by,"
                + "ib.issue_date FROM books AS b "
                + "JOIN issue_books AS ib ON ib.book_id=b.id "
                + "JOIN users AS u ON ib.user_id=u.id "
                + "WHERE ib.return_date IS NULL";
    	
    	try (PreparedStatement issuedBookStmt=con.prepareStatement(issuedBooksSql);
    		 ResultSet issueedBookResult=issuedBookStmt.executeQuery()) {
			
    		boolean booksFound=false;
    		
    		while(issueedBookResult.next()) {
    			if (!booksFound) {
                    System.out.println("----------------------------------------------------------------------------------------------------------------------");
                    System.out.printf("%-8s | %-35s | %-20s | %-20s | %-20s%n","Book ID", "Issued Book", "Author", "Issued By", "Issue Date");
                    System.out.println("----------------------------------------------------------------------------------------------------------------------");
                    booksFound = true;
                }
                System.out.printf("%-8d | %-35s | %-20s | %-20s | %-20s%n",
                    issueedBookResult.getInt("Book_ID"),
                    issueedBookResult.getString("Book_Name"),
                    issueedBookResult.getString("Author"),
                    issueedBookResult.getString("Issued_by"),
                    issueedBookResult.getString("issue_date"));
    		}
    		
    		if (!booksFound) {
				System.out.println("No books issued.");
				return;
			}
    		
          System.out.println("----------------------------------------------------------------------------------------------------------------------");
    		
		} catch (Exception e) {
			System.out.println("Something went wrong in AdminService.viewIssuedBooks() : "+e.getMessage());
			e.printStackTrace();
		}
    }
}
