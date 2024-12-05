package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import util.DBConnection;

public class BookHistoryDAO {
    

    // Lấy lịch sử chỉnh sửa của một cuốn sách theo bookId
    public List<BookHistory> getHistoryByBookId(int bookId) throws SQLException {
        String query = "SELECT * FROM book_history WHERE book_id = ?";
        List<BookHistory> histories = new ArrayList<>();
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookHistory history = new BookHistory(
                        rs.getInt("id"),
                        rs.getString("book_id"),
                        rs.getString("action"),
                        rs.getString("field_name"),
                        rs.getString("old_value"),
                        rs.getString("new_value"),
                        rs.getString("user_id"),
                        rs.getTimestamp("timestamp")
                    );
                    histories.add(history);
                }
            }
        }
        return histories;
    }

    // Thêm lịch sử chỉnh sửa
    public void addHistory(BookHistory history) throws SQLException {
        String query = "INSERT INTO book_history (book_id, action, field_name, old_value, new_value, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, history.getBookId());
            stmt.setString(2, history.getAction());
            stmt.setString(3, history.getFieldName());
            stmt.setString(4, history.getOldValue());
            stmt.setString(5, history.getNewValue());
            stmt.setString(6, history.getUserName());
            stmt.executeUpdate();
        }
    }

    // Lấy tất cả lịch sử chỉnh sửa
    public List<BookHistory> getAllHistory() throws SQLException {
        String query =  "SELECT bh.id, bh.book_id, bh.action,bh.field_name,\r\n"
    			+ "		bh.old_value, bh.new_value, us.username, bh.timestamp\r\n"
    			+ " FROM web.book_history as bh join users as us on bh.user_id = us.id ;";
        List<BookHistory> histories = new ArrayList<>();
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                BookHistory history = new BookHistory(
                    rs.getInt("id"),
                    rs.getString("book_id"),
                    rs.getString("action"),
                    rs.getString("field_name"),
                    rs.getString("old_value"),
                    rs.getString("new_value"),
                    rs.getString("username"),
                    rs.getTimestamp("timestamp")
                );
                histories.add(history);
            }
        }
        return histories;
    }
    public List<BookHistory> getHistoryWithPaginationAndSearch(int start, int pageSize, String searchQuery) {
        List<BookHistory> historyList = new ArrayList<>();
        
        String sql = "SELECT * FROM book_history WHERE book_id LIKE ? OR action LIKE ? LIMIT ?, ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchQuery + "%");  // Tìm kiếm theo book_id
            ps.setString(2, "%" + searchQuery + "%");  // Tìm kiếm theo action
            ps.setInt(3, start);
            ps.setInt(4, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BookHistory history = new BookHistory();
                // Đọc dữ liệu từ ResultSet và thêm vào historyList
                historyList.add(history);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return historyList;
    }
    
    public int getTotalRecords() {
        int totalRecords = 0;
        String sql = "SELECT COUNT(*) FROM book_history";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                totalRecords = rs.getInt(1); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalRecords;
    }
    
    public int getTotalRecordsQuery(String searchQuery) {
        int totalRecords = 0;
        
        String sql = "SELECT COUNT(*) FROM book_history WHERE book_id LIKE ? OR action LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchQuery + "%");
            ps.setString(2, "%" + searchQuery + "%");

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                totalRecords = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return totalRecords;
    }

}