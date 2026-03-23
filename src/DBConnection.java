import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "prasanna";
    private static Connection con = null;

    public static Connection getConnection() {
        
    	try {
    		if (con == null || con.isClosed()) {
				Class.forName("com.mysql.cj.jdbc.Driver");
				con = DriverManager.getConnection(URL,USER,PASSWORD);
				System.out.println("Database connected successfully!");
			}
    	} catch (Exception e) {
			System.out.println("Connection Error : "+e.getMessage());
			e.printStackTrace();
		}
    	return con;
    }

    public static void closeConnection() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Failed to close connection: " + e.getMessage());
            }
        }
    }
}
