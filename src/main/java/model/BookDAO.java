package model;

import java.sql.*;
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
                    rs.getString("id"),
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

    public boolean updateBook(Book book) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {

            String query = "UPDATE books SET title = ?, author = ?, category = ?, quantity = ?, price = ?, description = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, book.getTitle());
                stmt.setString(2, book.getAuthor());
                stmt.setString(3, book.getCategory());
                stmt.setInt(4, book.getQuantity());
                stmt.setDouble(5, book.getPrice());
                stmt.setString(8, book.getDescription());
                stmt.setString(11, book.getId());
                int rowsUpdated = stmt.executeUpdate();
                return rowsUpdated > 0;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public  boolean deleteBook(String id)  {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM books WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                int rowsDeleted = stmt.executeUpdate();
                return rowsDeleted > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Book> searchBooks(String query, int page, int limit) throws SQLException {
    	List<Book> books = new ArrayList<>();
    	int offset = page * limit;
    	String sql = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ? LIMIT ? OFFSET ?";
        
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            String searchQuery = "%" + query + "%"; // Thêm ký tự % để tìm kiếm
            stmt.setString(1, searchQuery);
            stmt.setString(2, searchQuery);
            stmt.setString(3, searchQuery);
            stmt.setInt(4, limit);
            stmt.setInt(5, offset);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Book book = new Book(
                        rs.getString("id"),
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

    public Book getBookById(String id)  {
       Book book =null;
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                   book = new Book(
                        rs.getString("id"),
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return book;

    }
    public List<Book> getBooks(int page, int pageSize) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books LIMIT ? OFFSET ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, pageSize);
            stmt.setInt(2, page * pageSize);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getString("id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setCategory(rs.getString("category"));
                book.setImageLink(rs.getString("image_link"));
                book.setQuantity(rs.getInt("quantity"));
                book.setPrice(rs.getDouble("price"));
                book.setPublisher(rs.getString("publisher"));
                book.setPublishYear(rs.getInt("publish_year"));
                book.setDescription(rs.getString("description"));
                books.add(book);
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return books;
    }

    public int countBooks() {
        int total = 0;
        String sql = "SELECT COUNT(*) FROM books";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    public int countBooksByQuery(String query) throws SQLException {
        String sql = "SELECT COUNT(*) FROM books WHERE title LIKE ? OR author LIKE ? OR category LIKE ?";
        int total = 0;
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(sql)) {
            String searchQuery = "%" + query + "%"; // Thêm ký tự % để tìm kiếm
            stmt.setString(1, searchQuery);
            stmt.setString(2, searchQuery);
            stmt.setString(3, searchQuery);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt(1);
                }
            }
        }
        return total;
    }
}
