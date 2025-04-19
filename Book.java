package LIBRARY;

class Book {
    private int id;
    private String title;
    private String author;
    private String genre;
    private int quantity;

    public Book(int id, String title, String author, String genre, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public int getQuantity() {
        return quantity;
    }

    public String toString() {
        return "Book{" +"id=" + id +", title='" + title + '\'' +", author='" + author + '\'' +", genre='" + genre + '\'' +", quantity=" + quantity +'}';
    }
}
