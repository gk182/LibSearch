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



    public boolean updateBook(String title, String author, String category, int quantity, double price, String description, String publisher, int publishYear,  String id, String updatedBy) throws SQLException {
        try (Connection conn = DBConnection.getConnection()) {
            // Cập nhật thông tin sách trong bảng books
            String updateQuery = "UPDATE books SET title = ?, author = ?, category = ?, quantity = ?, price = ?, description = ?, publisher = ?, publish_year = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, title);
                stmt.setString(2, author);
                stmt.setString(3, category);
                stmt.setInt(4, quantity);
                stmt.setDouble(5, price);
                stmt.setString(6, description);
                stmt.setString(7, publisher);
                stmt.setInt(8, publishYear);
                stmt.setString(9, id);

                int rowsUpdated = stmt.executeUpdate();
                // Nếu cập nhật sách thành công, thêm vào bảng book_history để lưu lịch sử
                if (rowsUpdated > 0) {
                    // Lấy thông tin sách cũ trước khi cập nhật để so sánh
                    String selectQuery = "SELECT title, author, category, quantity, price, description, publisher, publish_year, image_link FROM books WHERE id = ?";
                    try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery)) {
                        selectStmt.setString(1, id);
                        try (ResultSet rs = selectStmt.executeQuery()) {
                            if (rs.next()) {
                                String oldTitle = rs.getString("title");
                                String oldAuthor = rs.getString("author");
                                String oldCategory = rs.getString("category");
                                int oldQuantity = rs.getInt("quantity");
                                double oldPrice = rs.getDouble("price");
                                String oldDescription = rs.getString("description");
                                String oldPublisher = rs.getString("publisher");
                                int oldPublishYear = rs.getInt("publish_year");
                                String oldImageLink = rs.getString("image_link");

                                // Ghi lại lịch sử thay đổi cho từng trường
                                recordHistory(conn, id, "title", oldTitle, title, updatedBy);
                                recordHistory(conn, id, "author", oldAuthor, author, updatedBy);
                                recordHistory(conn, id, "category", oldCategory, category, updatedBy);
                                recordHistory(conn, id, "quantity", String.valueOf(oldQuantity), String.valueOf(quantity), updatedBy);
                                recordHistory(conn, id, "price", String.valueOf(oldPrice), String.valueOf(price), updatedBy);
                                recordHistory(conn, id, "description", oldDescription, description, updatedBy);
                                recordHistory(conn, id, "publisher", oldPublisher, publisher, updatedBy);
                                recordHistory(conn, id, "publish_year", String.valueOf(oldPublishYear), String.valueOf(publishYear), updatedBy);
                            }
                        }
                    }

                    return true; // Trả về true nếu update thành công và thêm lịch sử
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false; // Trả về false nếu có lỗi xảy ra
    }


    private void recordHistory(Connection conn, String bookId, String fieldName, String oldValue, String newValue, String updatedBy) throws SQLException {
        String historyQuery = "INSERT INTO book_history (book_id, action, field_namebook_shelves, old_value, new_value, user_id) VALUES (?, 'update', ?, ?, ?, ?)";
        try (PreparedStatement historyStmt = conn.prepareStatement(historyQuery)) {
            historyStmt.setString(1, bookId);
            historyStmt.setString(2, fieldName);
            historyStmt.setString(3, oldValue);
            historyStmt.setString(4, newValue);

            // Lấy user_id từ bảng users dựa trên username hoặc thông tin người cập nhật
            String userIdQuery = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userIdQuery)) {
                userStmt.setString(1, updatedBy);
                try (ResultSet userRs = userStmt.executeQuery()) {
                    if (userRs.next()) {
                        int userId = userRs.getInt("id");
                        historyStmt.setInt(5, userId);
                        historyStmt.executeUpdate(); // Thực hiện insert vào bảng book_history
                    }
                }
            }
        }
    }

    public boolean deleteBook(String id) {
        try (Connection conn = DBConnection.getConnection()) {
            // Bắt đầu giao dịch
            conn.setAutoCommit(false);

            // Bước 1: Xóa các bản ghi trong book_shelves
            String deleteShelvesSql = "DELETE FROM book_shelves WHERE book_id = ?";
            try (PreparedStatement deleteShelvesStmt = conn.prepareStatement(deleteShelvesSql)) {
                deleteShelvesStmt.setString(1, id);
                deleteShelvesStmt.executeUpdate();
            }

            // Bước 2: Xóa các bản ghi trong book_history
            String deleteHistorySql = "DELETE FROM book_history WHERE book_id = ?";
            try (PreparedStatement deleteHistoryStmt = conn.prepareStatement(deleteHistorySql)) {
                deleteHistoryStmt.setString(1, id);
                deleteHistoryStmt.executeUpdate();
            }

            // Bước 3: Xóa bản ghi trong books
            String deleteBookSql = "DELETE FROM books WHERE id = ?";
            try (PreparedStatement deleteBookStmt = conn.prepareStatement(deleteBookSql)) {
                deleteBookStmt.setString(1, id);
                int rowsDeleted = deleteBookStmt.executeUpdate();

                // Xác nhận giao dịch nếu xóa thành công
                if (rowsDeleted > 0) {
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
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
