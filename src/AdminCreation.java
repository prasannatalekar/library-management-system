import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class AdminCreation {
	
	static Connection con = DBConnection.getConnection();

    public static void main(String[] args) {

    	Scanner sc=new Scanner(System.in);
    	
    	System.out.println("Enter your first name: ");
        String firstName = sc.nextLine().trim();
        System.out.println("Enter your last name: ");
        String lastName = sc.nextLine().trim();
        System.out.println("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.println("Enter password: ");
        String password = sc.nextLine().trim();

        String checkUsernameSql="SELECT username FROM users WHERE username=? AND role='admin'";
        String insertUserSql="INSERT INTO users (first_name,last_name,username,password,role) VALUES (?,?,?,?,'admin')";
        
        try (PreparedStatement checkUserStmt=con.prepareStatement(checkUsernameSql);
        	 PreparedStatement insertUserStmt=con.prepareStatement(insertUserSql)) {
        	
        	checkUserStmt.setString(1, username);
        	
        	try (ResultSet rs=checkUserStmt.executeQuery()) {
				
        		if (rs.next()) {
					System.out.println("Username already exist!");
				}else {
					insertUserStmt.setString(1, firstName);
					insertUserStmt.setString(2, lastName);
					insertUserStmt.setString(3, username);
					insertUserStmt.setString(4, HashPassword.hashedPassword(password));
					
					int result=insertUserStmt.executeUpdate();
					if (result>0) 
						System.out.println("Admin registered successfully.");
					else
						System.out.println("Registration failed!");
				}
        		
			} 			
		} catch (Exception e) {
			System.out.println("Something went wrong in AdminCreation.registerAdmin() : "+e.getMessage());
			e.printStackTrace();
		} finally {
			DBConnection.closeConnection();
			sc.close();
		}
    }
}