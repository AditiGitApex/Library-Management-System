package LIBRARY;

class Request {
    private String type; // "RESERVATION", "RETURN", etc.
    private int userId;
    private int bookId;

    public Request(String type, int userId, int bookId) {
        this.type = type;
        this.userId = userId;
        this.bookId = bookId;
    }

    public String getType() {
        return type;
    }

    public int getUserId() {
        return userId;
    }

    public int getBookId() {
        return bookId;
    }

    public String toString() {
        return "Request{" +"type='" + type + '\'' +", userId=" + userId +", bookId=" + bookId +'}';
    }
}
