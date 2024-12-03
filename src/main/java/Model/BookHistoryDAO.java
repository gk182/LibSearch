package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookHistoryDAO {
    private Connection connection;

    public BookHistoryDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BookHistory> getHistoryByBookId(int bookId) throws SQLException {
        String query = "SELECT * FROM book_history WHERE book_id = ?";
        List<BookHistory> histories = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookHistory history = new BookHistory(
                        rs.getInt("id"),
                        rs.getInt("book_id"),
                        rs.getString("action"),
                        rs.getString("field_name"),
                        rs.getString("old_value"),
                        rs.getString("new_value"),
                        rs.getInt("user_id"),
                        rs.getTimestamp("timestamp")
                    );
                    histories.add(history);
                }
            }
        }
        return histories;
    }

    public void addHistory(BookHistory history) throws SQLException {
        String query = "INSERT INTO book_history (book_id, action, field_name, old_value, new_value, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, history.getBookId());
            stmt.setString(2, history.getAction());
            stmt.setString(3, history.getFieldName());
            stmt.setString(4, history.getOldValue());
            stmt.setString(5, history.getNewValue());
            stmt.setInt(6, history.getUserId());
            stmt.executeUpdate();
        }
    }
}
