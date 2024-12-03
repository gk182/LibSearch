package model;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DBConnection;

public class BookDAO {


    public BookDAO() {
		// TODO Auto-generated constructor stub
	}

	public List<Book> getAllBooks() throws SQLException {
        String query = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();
        try (Statement stmt = DBConnection.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Book book = new Book(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getTimestamp("created_at"),
                    rs.getTimestamp("updated_at"),
                    rs.getString("publisher"),
                    rs.getInt("publish_year"),
                    rs.getString("description"),
                    rs.getString("image_link"),
                    rs.getInt("user_id")
                );
                books.add(book);
            }
        }
        return books;
    }

    public void addBook(Book book) throws SQLException {
        String query = "INSERT INTO books (title, author, category, quantity, price, publisher, publish_year, description, image_link, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getCategory());
            stmt.setInt(4, book.getQuantity());
            stmt.setDouble(5, book.getPrice());
            stmt.setString(6, book.getPublisher());
            stmt.setInt(7, book.getPublishYear());
            stmt.setString(8, book.getDescription());
            stmt.setString(9, book.getImageLink());
            stmt.setInt(10, book.getUserId());
            stmt.executeUpdate();
        }
    }

    public void updateBook(Book book) throws SQLException {
        String query = "UPDATE books SET title = ?, author = ?, category = ?, quantity = ?, price = ?, publisher = ?, publish_year = ?, description = ?, image_link = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getCategory());
            stmt.setInt(4, book.getQuantity());
            stmt.setDouble(5, book.getPrice());
            stmt.setString(6, book.getPublisher());
            stmt.setInt(7, book.getPublishYear());
            stmt.setString(8, book.getDescription());
            stmt.setString(9, book.getImageLink());
            stmt.setInt(10, book.getUserId());
            stmt.setInt(11, book.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteBook(int bookId) throws SQLException {
        String query = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    public List<Book> searchBooks(String query) throws SQLException {
        String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";
        List<Book> books = new ArrayList<>();
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            String searchQuery = "%" + query + "%"; // Thêm ký tự % để tìm kiếm
            stmt.setString(1, searchQuery);
            stmt.setString(2, searchQuery);
            stmt.setString(3, searchQuery);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("publisher"),
                        rs.getInt("publish_year"),
                        rs.getString("description"),
                        rs.getString("image_link"),
                        rs.getInt("user_id")
                    );
                    books.add(book);
                }
            }
        }
        return books;
    }

    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at"),
                        rs.getString("publisher"),
                        rs.getInt("publish_year"),
                        rs.getString("description"),
                        rs.getString("image_link"),
                        rs.getInt("user_id")
                    );
                }
            }
        }
        return null;
    }
}
