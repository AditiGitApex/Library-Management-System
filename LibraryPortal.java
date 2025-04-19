package LIBRARY;

import java.sql.*;
import java.util.*;

public class LibraryPortal extends DBConnection {
    private List<Book> books = new ArrayList<>();
    private LibraryService libraryService;
    private User currentUser = null;

    public LibraryPortal() throws SQLException {
        super();
        libraryService = new LibraryService();
    }

    public static void main(String[] args) throws SQLException {
        LibraryPortal libraryPortal = new LibraryPortal();
        libraryPortal.init();
    }

    public void init() {
        try {
            fetchBooksFromDatabase();

            Scanner sc = new Scanner(System.in);
            System.out.println("Welcome to the Library Portal");

            while (true) {
                System.out.println("\n===== Main Menu =====");
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.print("Enter your choice: ");

                int choice;
                try {
                    choice = Integer.parseInt(sc.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number.");
                    continue;
                }

                switch (choice) {
                    case 1:
                        registerUser(sc);
                        break;
                    case 2:
                        if (loginUser(sc)) {
                            showMenu(sc);
                        } else {
                            System.out.println("Login failed. Please try again.");
                        }
                        break;
                    case 3:
                        System.out.println("Exiting Library Portal. Goodbye!");
                        sc.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please enter a valid option.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void registerUser(Scanner sc) throws SQLException {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        String password;
        String confirmPassword;
    
        // Loop to ensure that the password and confirmation match
        while (true) {
            System.out.print("Enter password: ");
            password = sc.nextLine();
            System.out.print("Confirm password: ");
            confirmPassword = sc.nextLine();
    
            if (password.equals(confirmPassword)) {
                break; // Exit the loop if the passwords match
            } else {
                System.out.println("Passwords do not match. Please try again.");
            }
        }
    
        System.out.print("Is this user an admin? (yes/no): ");
        boolean isAdmin = sc.nextLine().equalsIgnoreCase("yes");
    
        if (libraryService.registerUser(username, password, isAdmin)) {
            System.out.println("User registered successfully!");
        } else {
            System.out.println("User registration failed. Username may already exist.");
        }
    }

    private boolean loginUser(Scanner sc) throws SQLException {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();

        currentUser = libraryService.authenticateUser(username, password);
        return currentUser != null;
    }

    private void showMenu(Scanner sc) throws SQLException {
        while (true) {
            System.out.println("\n===== Menu =====");
            System.out.println("1. Issue Book");
            System.out.println("2. Return Book");
            System.out.println("3. View All Books");
            System.out.println("4. Sort Books");
            System.out.println("5. Add Book");
            System.out.println("6. Delete Book");
            System.out.println("7. Search Books");
            System.out.println("8. Process Payment");
            System.out.println("9. Undo Last Action");
            System.out.println("10. Process Requests");
            System.out.println("11. Exit");

            System.out.print("Enter your choice: ");
            int choice;
            try {
                choice = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1:
                    issueBook(sc);
                    break;
                case 2:
                    returnBook(sc);
                    break;
                case 3:
                    viewAllBooks(sc);
                    break;
                case 4:
                    sortBooks(sc);
                    break;
                case 5:
                    if (currentUser.isAdmin()) {
                        addBook(sc);
                    } else {
                        System.out.println("Access denied. Admins only.");
                    }
                    break;
                case 6:
                    if (currentUser.isAdmin()) {
                        deleteBook(sc);
                    } else {
                        System.out.println("Access denied. Admins only.");
                    }
                    break;
                case 7:
                    search(sc);
                    break;
                case 8:
                    processPayment(sc);
                    break;
                case 9:
                    libraryService.undoLastAction();
                    System.out.println("Last action undone.");
                    break;
                case 10:
                    libraryService.processRequests();
                    System.out.println("Requests processed.");
                    break;
                case 11:
                    System.out.println("Exiting Library Portal. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a valid option.");
            }
        }
    }

    private void issueBook(Scanner sc) throws SQLException {
        System.out.print("Enter User ID: ");
        int userId = Integer.parseInt(sc.nextLine());

        System.out.print("Enter Book ID: ");
        int bookId = Integer.parseInt(sc.nextLine());

        // Check if the user exists
        if (!libraryService.doesUserExist(userId)) {
            System.out.println("User ID does not exist.");
            return;
        }

        // Check if the book exists
        if (!libraryService.doesBookExist(bookId)) {
            System.out.println("Book ID does not exist.");
            return;
        }

        // Proceed to issue the book if both exist
        libraryService.issueBook(userId, bookId);
        System.out.println("Book issued successfully.");
    }

    private void returnBook(Scanner sc) throws SQLException {
        System.out.print("Enter User ID: ");
        int userId = Integer.parseInt(sc.nextLine());

        System.out.print("Enter Book ID: ");
        int bookId = Integer.parseInt(sc.nextLine());

        // Check if the user exists
        if (!libraryService.doesUserExist(userId)) {
            System.out.println("User ID does not exist.");
            return;
        }

        // Check if the book exists
        if (!libraryService.doesBookExist(bookId)) {
            System.out.println("Book ID does not exist.");
            return;
        }

        // Check if the user has borrowed the book
        if (!libraryService.hasUserBorrowedBook(userId, bookId)) {
            System.out.println("This book was not borrowed by the user.");
            return;
        }

        // Proceed to return the book if all checks pass
        libraryService.returnBook(userId, bookId);
        System.out.println("Book returned successfully.");
    }

    private void addBook(Scanner sc) {
        System.out.println("Enter book id: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Enter Book Title: ");
        String title = sc.nextLine();

        System.out.print("Enter Book Author: ");
        String author = sc.nextLine();

        System.out.print("Enter Book Genre: ");
        String genre = sc.nextLine();

        System.out.print("Enter Book Quantity: ");
        int quantity = Integer.parseInt(sc.nextLine());

        libraryService.addBook(new Book(id, title, author, genre, quantity));
    }

    private void deleteBook(Scanner sc) throws SQLException {
        System.out.print("Enter Book ID to Delete: ");
        int bookId = Integer.parseInt(sc.nextLine());

        // Check if the book exists
        if (!libraryService.doesBookExist(bookId)) {
            System.out.println("Book ID does not exist.");
            return;
        }

        // Proceed to delete the book if it exists
        libraryService.deleteBook(bookId);
        System.out.println("Book deleted successfully.");
    }

    private void viewAllBooks(Scanner sc) {
        System.out.println("\nSort Books By:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Genre");
        System.out.println("4. Quantity");
        System.out.print("Enter your choice: ");

        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        String sortBy;
        switch (choice) {
            case 1:
                sortBy = "title";
                break;
            case 2:
                sortBy = "author";
                break;
            case 3:
                sortBy = "genre";
                break;
            case 4:
                sortBy = "quantity";
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
                return;
        }

        String query = "SELECT * FROM Books ORDER BY " + sortBy;
        libraryService.viewAllBooks(query);
    }

    private void fetchBooksFromDatabase() throws SQLException {
        String query = "SELECT * FROM Books";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            books.clear(); // Clear existing data to avoid duplication
            while (rs.next()) {
                books.add(new Book(rs.getInt("id"), rs.getString("title"), rs.getString("author"), rs.getString("genre"), rs.getInt("quantity")));
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private void sortBooks(Scanner sc) {
        System.out.println("\nSort Books By:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Genre");
        System.out.println("4. Quantity");
        System.out.print("Enter your choice: ");
        int choice;
        try {
            choice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        String orderByColumn;
        switch (choice) {
            case 1:
                orderByColumn = "title";
                break;
            case 2:
                orderByColumn = "author";
                break;
            case 3:
                orderByColumn = "genre";
                break;
            case 4:
                orderByColumn = "quantity";
                break;
            default:
                System.out.println("Invalid choice. Please enter a valid option.");
                return;
        }

        // Perform the SQL query to fetch and sort books
        String query = "SELECT * FROM Books ORDER BY " + orderByColumn;
        libraryService.sortBooks(query);
    }

    private void search(Scanner sc) throws SQLException {
        System.out.println("Search by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. Genre");
        System.out.print("Choose an option (1, 2, or 3): ");
        int choice = Integer.parseInt(sc.nextLine());

        String searchCriteria = "";
        String query = "";

        switch (choice) {
            case 1:
                System.out.print("Enter title to search: ");
                searchCriteria = "%" + sc.nextLine() + "%";
                query = "SELECT * FROM Books WHERE title LIKE ?";
                break;
            case 2:
                System.out.print("Enter author to search: ");
                searchCriteria = "%" + sc.nextLine() + "%";
                query = "SELECT * FROM Books WHERE author LIKE ?";
                break;
            case 3:
                System.out.print("Enter genre to search: ");
                searchCriteria = "%" + sc.nextLine() + "%";
                query = "SELECT * FROM Books WHERE genre LIKE ?";
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        libraryService.search(searchCriteria, query);
    }

    private void processPayment(Scanner sc) throws SQLException {
        System.out.print("Enter User ID: ");
        int userId = Integer.parseInt(sc.nextLine());

        // Check if the user exists
        if (!libraryService.doesUserExist(userId)) {
            System.out.println("User ID does not exist.");
            return;
        }

        System.out.print("Enter amount to pay: ");
        double amount;
        try {
            amount = Double.parseDouble(sc.nextLine());
            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
            return;
        }

        // Process the payment
        libraryService.processPayment(userId, amount);
        System.out.println("Payment processed successfully.");
    }
}
