package LIBRARY;
import java.sql.*;
import java.util.*;

class LibraryService extends DBConnection {
    private List<Book> books = new ArrayList<>();
    private Stack<Action> actionStack = new Stack<>(); // Stack for undo/redo actions
    private Queue<Request> requestQueue = new LinkedList<>(); // Queue for managing requests

    public LibraryService() throws SQLException {
        // Fetch books from the database on initialization
        try {
            fetchBooksFromDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchBooksFromDatabase() {
        String query = "SELECT * FROM Books";
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            books.clear();
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean doesUserExist(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Users WHERE user_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public boolean doesBookExist(int bookId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Books WHERE id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, bookId);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public boolean hasUserBorrowedBook(int userId, int bookId) throws SQLException {
        String query = "SELECT COUNT(*) FROM BorrowedBooks WHERE user_id = ? AND book_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, userId);
        stmt.setInt(2, bookId);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        return rs.getInt(1) > 0;
    }

    public void addBook(Book book) {
        String insertQuery = "INSERT INTO Books (id, title, author, genre, quantity) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement pstmt = conn.prepareStatement(insertQuery);

            pstmt.setInt(1, book.getId());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getGenre());
            pstmt.setInt(5, book.getQuantity());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                books.add(book); // Add the book to the list only if it was successfully added to the database
                actionStack.push(new Action("ADD", book)); // Push action to stack
                System.out.println("Book added successfully.");
            } else {
                System.out.println("Failed to add the book.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while adding the book: " + e.getMessage());
        }
    }

    public void deleteBook(int bookId) {
        String deleteQuery = "DELETE FROM Books WHERE id = ?";

        try {
            PreparedStatement pstmt = conn.prepareStatement(deleteQuery);

            pstmt.setInt(1, bookId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Optionally, remove the book from the in-memory list after a successful deletion
                books.removeIf(book -> book.getId() == bookId);
                actionStack.push(new Action("DELETE", new Book(bookId, "", "", "", 0))); // Push action to stack
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("No book found with the given ID.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while deleting the book: " + e.getMessage());
        }
    }

    public void search(String searchCriteria, String query) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, searchCriteria);
        ResultSet rs = stmt.executeQuery();

        // Display search results
        if (rs.next()) {
            do {
                System.out.println("Book ID: " + rs.getInt("id"));
                System.out.println("Title: " + rs.getString("title"));
                System.out.println("Author: " + rs.getString("author"));
                System.out.println("Genre: " + rs.getString("genre"));
                System.out.println("-----");
            } while (rs.next());
        } else {
            System.out.println("No results found.");
        }
    }

    public void viewAllBooks(String query) {
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            List<Book> allBooks = new ArrayList<>();
            while (rs.next()) {
                allBooks.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getInt("quantity")
                ));
            }

            System.out.println("\n===== All Books =====");
            for (Book book : allBooks) {
                System.out.println(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while fetching the books: " + e.getMessage());
        }
    }

    public void sortBooks(String query) {
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            List<Book> sortedBooks = new ArrayList<>();
            while (rs.next()) {
                sortedBooks.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("genre"),
                        rs.getInt("quantity")
                ));
            }

            // Display sorted books
            System.out.println("\n===== Sorted Book List =====");
            for (Book book : sortedBooks) {
                System.out.println(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("An error occurred while fetching and sorting the books: " + e.getMessage());
        }
    }

    public boolean registerUser(String username, String password, boolean isAdmin) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{CALL RegisterUser(?, ?, ?)}");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.setBoolean(3, isAdmin);
        stmt.execute();
        return true; // Return true if registration successful
    }

    public User authenticateUser(String username, String password) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{CALL AuthenticateUser(?, ?, ?, ?)}");
        stmt.setString(1, username);
        stmt.setString(2, password);
        stmt.registerOutParameter(3, Types.INTEGER);
        stmt.registerOutParameter(4, Types.BOOLEAN);
        stmt.execute();
        boolean isAdmin = stmt.getBoolean(4);
        return new User(username, isAdmin);
    }

    public void issueBook(int userId, int bookId) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{CALL IssueBook(?, ?)}");
        stmt.setInt(1, userId);
        stmt.setInt(2, bookId);
        stmt.execute();
        System.out.println("Book issued to User ID: " + userId);
    }

    public void returnBook(int userId, int bookId) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{CALL ReturnBook(?, ?)}");
        stmt.setInt(1, userId);
        stmt.setInt(2, bookId);
        stmt.execute();
        System.out.println("Book returned by User ID: " + userId);
    }

    public void processPayment(int userId, double amount) throws SQLException {
        CallableStatement stmt = conn.prepareCall("{CALL ProcessPayment(?, ?)}");
        stmt.setInt(1, userId);
        stmt.setDouble(2, amount);
        stmt.execute();
        System.out.println("Processed payment of $" + amount + " for User ID: " + userId);
    }

    // Methods for managing the Stack and Queue

    public void undoLastAction() {
        if (!actionStack.isEmpty()) {
            Action lastAction = actionStack.pop();
            // Handle the undo operation based on the action type
            switch (lastAction.getType()) {
                case "ADD":
                    deleteBook(lastAction.getBook().getId()); // Undo book addition
                    break;
                case "DELETE":
                    addBook(lastAction.getBook()); // Undo book deletion
                    break;
            }
        } else {
            System.out.println("No actions to undo.");
        }
    }

    public void enqueueRequest(Request request) {
        requestQueue.add(request);
        System.out.println("Request added to queue: " + request);
    }

    public void processRequests() {
        while (!requestQueue.isEmpty()) {
            Request request = requestQueue.poll();
            // Process the request
            System.out.println("Processing request: " + request);
        }
    }
}