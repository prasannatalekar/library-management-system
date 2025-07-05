import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserService {
    static Connection con = DBConnection.getConnection();
    static Scanner sc = new Scanner(System.in);

    public static User login(String role) throws SQLException {
        System.out.println("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.println("Enter password: ");
        String password = sc.nextLine().trim();

        String sql = "SELECT * FROM users WHERE username=? AND password=? AND role=?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("Login Successful");
                return new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("username"),
                    rs.getString("role")
                );
            } else {
                System.out.println("Invalid username or password!");
                return null;
            }
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
        }
    }

    public static void registerUser() throws SQLException {
        System.out.println("Enter your first name: ");
        String firstName = sc.nextLine().trim();
        System.out.println("Enter your last name: ");
        String lastName = sc.nextLine().trim();
        System.out.println("Enter username: ");
        String username = sc.nextLine().trim();
        System.out.println("Enter password: ");
        String password = sc.nextLine().trim();

        String sql = "INSERT INTO users (first_name,last_name,username,password,role) VALUES (?,?,?,?,'user')";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(sql);
            ps.setString(1, firstName);
            ps.setString(2, lastName);
            ps.setString(3, username);
            ps.setString(4, password);

            if (ps.executeUpdate() > 0) {
                System.out.println("User registered successfully!");
            } else {
                System.out.println("Registration failed.");
            }
        } finally {
            if (ps != null) ps.close();
        }
    }
}
