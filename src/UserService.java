import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserService {
    static Connection con = DBConnection.getConnection();

    public static User login(String role, Scanner sc) throws SQLException {
        System.out.println("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.println("Enter password: ");
        String password = sc.nextLine().trim();

        String sql = "SELECT * FROM users WHERE username=? AND role=?";  

        try (PreparedStatement ps=con.prepareStatement(sql)) {
			
        	ps.setString(1, username);
        	ps.setString(2, role);
        	
        	try (ResultSet rs=ps.executeQuery()) {
        		
        		if (rs.next()) {
        			
        			if(rs.getBoolean("is_deleted")) {
            			System.out.println("Your account has been blocked. Please contact admin!");
                        return null;
            		}
        			
					String storedPassword=rs.getString("password");
					
					if(HashPassword.checkPassword(password, storedPassword)) {
						System.out.println("Login Successful");
						
						return new User(rs.getInt("id"), 
										rs.getString("first_name"), 
										rs.getString("last_name"), 
										rs.getString("username"), 
										rs.getString("role"));
					}
					else {
						System.out.println("Invalid credentials!");
						return null;
					}
				}
        		else {
					System.out.println("Invalid credentials!");
					return null;
				}
        		
			}		
        	
		} catch (Exception e) {
			System.out.println("Something went wrong in UserService.login() : "+e.getMessage());
			e.printStackTrace();
			return null;
		}
    }

    public static void registerUser(Scanner sc) throws SQLException {
        System.out.println("Enter your first name: ");
        String firstName = sc.nextLine().trim();
        System.out.println("Enter your last name: ");
        String lastName = sc.nextLine().trim();
        System.out.println("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.println("Enter password: ");
        String password = sc.nextLine().trim();
        
        String checkUsernameSql="SELECT username FROM users WHERE username=?";
        String insertUserSql="INSERT INTO users (first_name,last_name,username,password,role) VALUES (?,?,?,?,'user')";
        
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
						System.out.println("User registered successfully.");
					else
						System.out.println("Registration failed!");
				}
        		
			} catch (Exception e) {
				System.out.println("Something went wrong in UserService.registerUser() : "+e.getMessage());
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			System.out.println("Something went wrong in UserService.registerUser() : "+e.getMessage());
			e.printStackTrace();
		}
        
    }
}
