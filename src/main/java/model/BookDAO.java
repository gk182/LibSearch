package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static java.sql.DriverManager.getConnection;

import lombok.SneakyThrows;
import util.DBConnection;

public class BookDAO {
    private Connection getConnection() throws SQLException {
        try {
            return DBConnection.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean addBook(Book book) {
        String query = "INSERT INTO Books (title, category, supplier, author, publisher, publish_year, page_count, description, image_url, shelf_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getCategory());
            stmt.setString(3, book.getSupplier());
            stmt.setString(4, book.getAuthor());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getPublishYear());
            stmt.setInt(7, book.getPageCount());
            stmt.setString(8, book.getDescription());
            stmt.setString(9, book.getImageUrl());
            stmt.setInt(10, book.getShelfId());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("supplier"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getInt("publish_year"),
                        rs.getInt("page_count"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getInt("shelf_id")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


    public boolean updateBook(Book book) {
        String query = "UPDATE Books SET title = ?, category = ?, supplier = ?, author = ?, publisher = ?, publish_year = ?, page_count = ?, description = ?, image_url = ?, shelf_id = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getCategory());
            stmt.setString(3, book.getSupplier());
            stmt.setString(4, book.getAuthor());
            stmt.setString(5, book.getPublisher());
            stmt.setInt(6, book.getPublishYear());
            stmt.setInt(7, book.getPageCount());
            stmt.setString(8, book.getDescription());
            stmt.setString(9, book.getImageUrl());
            stmt.setInt(10, book.getShelfId());
            stmt.setInt(11, book.getId());

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteBook(int bookId) {
        String query = "DELETE FROM Books WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, bookId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<Book> searchBooksByTitle(String title) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM Books WHERE title LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, "%" + title + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("category"),
                        rs.getString("supplier"),
                        rs.getString("author"),
                        rs.getString("publisher"),
                        rs.getInt("publish_year"),
                        rs.getInt("page_count"),
                        rs.getString("description"),
                        rs.getString("image_url"),
                        rs.getInt("shelf_id")
                );
                books.add(book);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }
}