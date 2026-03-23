import org.mindrot.jbcrypt.BCrypt;

public class HashPassword {
	
	public static String hashedPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}
	
	public static boolean checkPassword(String enteredPassword, String hashedPassword) {
		return BCrypt.checkpw(enteredPassword, hashedPassword);
	}
	
}
