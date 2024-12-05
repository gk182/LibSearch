package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShelfDAO {
    private Connection connection;

    public ShelfDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Shelf> getAllShelves() throws SQLException {
        String query = "SELECT * FROM shelves";
        List<Shelf> shelves = new ArrayList<>();
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
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

    public void addShelf(Shelf shelf) throws SQLException {
        String query = "INSERT INTO shelves (shelf_name) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, shelf.getShelfName());
            stmt.executeUpdate();
        }
    }

    public void deleteShelf(int shelfId) throws SQLException {
        String query = "DELETE FROM shelves WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, shelfId);
            stmt.executeUpdate();
        }
    }
}
