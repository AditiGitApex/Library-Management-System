package LIBRARY;

class Action {
    private String type; // "ADD" or "DELETE"
    private Book book;

    public Action(String type, Book book) {
        this.type = type;
        this.book = book;
    }

    public String getType() {
        return type;
    }

    public Book getBook() {
        return book;
    }
}