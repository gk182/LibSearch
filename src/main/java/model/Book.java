package model;

public class Book {
    private int id;
    private String title;
    private String category;
    private String supplier;
    private String author;
    private String publisher;
    private int publishYear;
    private int pageCount;
    private String description;
    private String imageUrl;
    private int shelfId;

    // Constructor mặc định
    public Book() {}

    // Constructor với các tham số
    public Book(int id, String title, String category, String supplier, String author,
                String publisher, int publishYear, int pageCount, String description,
                String imageUrl, int shelfId) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.supplier = supplier;
        this.author = author;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.pageCount = pageCount;
        this.description = description;
        this.imageUrl = imageUrl;
        this.shelfId = shelfId;
    }

    // Getters và Setters cho các thuộc tính
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getShelfId() {
        return shelfId;
    }

    public void setShelfId(int shelfId) {
        this.shelfId = shelfId;
    }
}
