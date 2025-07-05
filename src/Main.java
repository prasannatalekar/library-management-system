import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    static Scanner sc = new Scanner(System.in);
    static User currentUser = null;

    public static int getIntInput() {
        while (true) {
            try {
                int input = sc.nextInt();
                sc.nextLine();
                return input;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                sc.nextLine();
            }
        }
    }
    
    public static void main(String[] args) throws SQLException {
        int choice;
        do {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Create new user");
            System.out.println("2. Login as user");
            System.out.println("3. Login as admin");
            System.out.println("0. Exit");

            choice = getIntInput();

            switch (choice) {
                case 1 -> UserService.registerUser();
                case 2 -> {
                    currentUser = UserService.login("user");
                    if (currentUser != null) userMenu();
                }
                case 3 -> {
                    currentUser = UserService.login("admin");
                    if (currentUser != null) adminMenu();
                }
                case 0 -> System.out.println("Exited");
                default -> System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 0);

        DBConnection.closeConnection();
        sc.close();
    }
    
    public static void userMenu() throws SQLException {
        int choice;
        do {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. View Books");
            System.out.println("2. Search Book");
            System.out.println("3. Issue Book");
            System.out.println("4. Return Book");
            System.out.println("0. Logout");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> LibraryService.viewBooks();
                case 2 -> LibraryService.searchBook();
                case 3 -> LibraryService.issueBook(currentUser);
                case 4 -> LibraryService.returnBook(currentUser);
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 0);
        currentUser = null;
    }

    public static void adminMenu() throws SQLException {
        int choice;
        do {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View Books");
            System.out.println("2. Search Book");
            System.out.println("3. Add Book");
            System.out.println("4. Delete Book");
            System.out.println("5. View Users");
            System.out.println("6. Delete user");
            System.out.println("7. View Issued Books");
            System.out.println("0. Logout");

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> LibraryService.viewBooks();
                case 2 -> LibraryService.searchBook();
                case 3 -> AdminService.addBook();
                case 4 -> AdminService.deleteBook();
                case 5 -> AdminService.viewUsers();
                case 6 -> AdminService.deleteUser();
                case 7 -> AdminService.viewIssuedBooks();
                case 0 -> System.out.println("Logging out...");
                default -> System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 0);
        currentUser = null;
    }
}
