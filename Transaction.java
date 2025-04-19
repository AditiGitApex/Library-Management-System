package LIBRARY;

import java.time.LocalDate;

class Transaction {
    private int userId;
    private int bookId;
    private LocalDate date;
    private boolean isReturned;

    public Transaction(int userId, int bookId, LocalDate date, boolean isReturned) {
        this.userId = userId;
        this.bookId = bookId;
        this.date = date;
        this.isReturned = isReturned;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookId() {
        return bookId;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isReturned() {
        return isReturned;
    }
}
