package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import util.DBConnection;

public class ShelfDAO {

    public ShelfDAO() {
	}

    public List<Shelf> getAllShelves() throws SQLException {
        String query = "SELECT * FROM shelves";
        List<Shelf> shelves = new ArrayList<>();
        try (Statement stmt = DBConnection.getConnection().createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Shelf shelf = new Shelf(
                    rs.getInt("id"),
                    rs.getString("shelf_name")
                );
                shelves.add(shelf);
            }
        }
        return shelves;
    }
    
    public Shelf getShelfByBookId(String bookId)  {
        String query = "SELECT s.id, s.shelf_name " +
                       "FROM shelves s " +
                       "JOIN book_shelves bs ON s.id = bs.shelf_id " +
                       "WHERE bs.book_id = ?";
        Shelf shelf = null;
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, bookId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    shelf = new Shelf(
                        rs.getInt("id"),
                        rs.getString("shelf_name")
                    );
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shelf;
    }


    public void addShelf(Shelf shelf) throws SQLException {
        String query = "INSERT INTO shelves (shelf_name) VALUES (?)";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query)) {
            stmt.setString(1, shelf.getShelfName());
            stmt.executeUpdate();
        }
    }

    public void deleteShelf(int shelfId) throws SQLException {
        String query = "DELETE FROM shelves WHERE id = ?";
        try (PreparedStatement stmt = DBConnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, shelfId);
            stmt.executeUpdate();
        }
    }
}
