package model;
import controller.SHA256WithSalt;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AccountDAO {

    public static boolean login(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            // Lấy hashedPassword và salt từ database
            String sql = "SELECT hashedPassword, salt FROM account WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHashedPassword = rs.getString("hashedPassword");
                        String storedSalt = rs.getString("salt");

                        // Hash mật khẩu người dùng nhập với salt từ database
                        String inputHashedPassword = SHA256WithSalt.hashPasswordWithSalt(password, storedSalt);

                        // So sánh hash của mật khẩu nhập vào với hash trong database
                        return storedHashedPassword.equals(inputHashedPassword);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public static boolean register(String username, String password) {
        // Tạo salt
        String salt = SHA256WithSalt.generateSalt();

        // Hash mật khẩu với salt
        String hashedPassword = SHA256WithSalt.hashPasswordWithSalt(password, salt);

        try (Connection conn = DBConnection.getConnection()) {
            // Câu lệnh SQL để thêm người dùng mới
            String sql = "INSERT INTO account (username, hashedPassword, salt) VALUES (?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, hashedPassword);
                stmt.setString(3, salt);

                // Thực thi câu lệnh
                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0; // Trả về true nếu thêm thành công
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // TEST
//    public static void main(String[] args) {
//        String username = "";
//        String password = "";
//        if (register(username, password)) {
//            System.out.println("Đăng kí thành công!");
//        } else {
//            System.out.println("Tên người dùng hoặc mật khẩu không đúng.");
//        }
//    }
}
