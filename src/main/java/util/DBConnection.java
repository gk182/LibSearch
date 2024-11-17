package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/web";
    private static final String USER = "root";
    private static final String PASSWORD = "2782004";

    // Hàm kết nối với MySQL
    public static Connection getConnection() throws SQLException {
        try {
            // Đăng ký driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Tạo và trả về kết nối
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver không được tìm thấy!");
            e.printStackTrace();
            throw new SQLException("MySQL Driver không tìm thấy.");
        } catch (SQLException e) {
            System.out.println("Lỗi kết nối cơ sở dữ liệu.");
            e.printStackTrace();
            throw e;
        }
    }

//    public static void main(String[] args) {
//        try (Connection conn = getConnection()) {
//            if (conn != null) {
//                System.out.println("Kết nối thành công!");
//            }
//        } catch (SQLException e) {
//            System.out.println("Lỗi kết nối cơ sở dữ liệu.");
//            e.printStackTrace();
//        }
//    }
}
