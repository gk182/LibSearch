package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import util.DBConnection;
import util.OTPGenerator;
import util.SHA256WithSalt;

public class UserDAO {
	public static boolean login(String username, String password) {
	    try (Connection conn = DBConnection.getConnection()) {
	        String sql = "SELECT username, password_hash, salt, email FROM users WHERE username = ? or email = ? ";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, username);
	            stmt.setString(2, username);
	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                String storedPasswordHash = rs.getString("password_hash");
	                String storedSalt = rs.getString("salt");

	                // Kiểm tra null hoặc rỗng
	                if (storedSalt == null || storedSalt.isEmpty()) {
	                    System.out.println("Salt is missing for user: " + username);
	                    return false;
	                }

	                // Hash mật khẩu nhập vào
	                String hashedInputPassword = SHA256WithSalt.hashPasswordWithSalt(password, storedSalt);

	                // Kiểm tra mật khẩu đã được hash đúng
	                if (hashedInputPassword.equals(storedPasswordHash)) {
	                    return true;
	                } else {
	                    System.out.println("Password mismatch for user: " + username);
	                }
	            } else {
	                System.out.println("User not found: " + username);
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false; // 0: Đăng nhập thất bại trong trường hợp lỗi
	    }
	    return false; // 0: Đăng nhập thất bại
	}


	public static boolean register(String username,String email, String password, String role) {
        // Tạo salt
        String salt = SHA256WithSalt.generateSalt();

        // Hash mật khẩu với salt
        String hashedPassword = SHA256WithSalt.hashPasswordWithSalt(password, salt);

        try (Connection conn = DBConnection.getConnection()) {
            // Câu lệnh SQL để thêm người dùng mới
            String sql = "INSERT INTO users (username, email, password_hash, salt, role) VALUES (?,?,?,?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, hashedPassword);
                stmt.setString(4, salt);
                stmt.setString(5, role);
                // Thực thi câu lệnh
                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0; // Trả về true nếu thêm thành công
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
	
	public static boolean updateUser(String username, String email, String role) {
	    try (Connection conn = DBConnection.getConnection()) {
	        String sql = "UPDATE users SET email = ?, role = ? WHERE username = ?";
	        
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, email);
	            stmt.setString(2, role);
	            stmt.setString(3, username);

	            int rowsUpdated = stmt.executeUpdate();
	            return rowsUpdated > 0; 
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public static boolean deleteUser(String username) {
	    try (Connection conn = DBConnection.getConnection()) {
	        String sql = "DELETE FROM users WHERE username = ?";
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, username);
	            int rowsDeleted = stmt.executeUpdate();
	            return rowsDeleted > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public static boolean checkUserExists(String username) {
        boolean userExists = false;
        String query = "SELECT * FROM users WHERE username = ? or email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userExists = true; // Nếu kết quả tồn tại, user đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExists;
    }
	public static String getEmailByUsername(String username) {
	    String email = null;
	    String sql = "SELECT email FROM users WHERE username = ? OR email = ?";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, username);
	        stmt.setString(2, username);

	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                email = rs.getString("email");
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return email;
	}
	public static boolean saveOTP(String username, String otp) {
        deleteOldOTP(username);

        String sql = "INSERT INTO otp (username, otp_code, timestamp, expiration_time) VALUES (?, ?, ?, ?)";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plus(2, ChronoUnit.MINUTES);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, otp);
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.setTimestamp(4, Timestamp.valueOf(expirationTime));

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Phương thức kiểm tra OTP có hợp lệ hay không
    public static int verifyOTP(String username, String otp) {
        String sql = "SELECT * FROM otp WHERE username = ? AND otp_code = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, otp);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp expirationTime = rs.getTimestamp("expiration_time");
                    LocalDateTime now = LocalDateTime.now();

                    if (now.isBefore(expirationTime.toLocalDateTime())) {
                        // Xóa OTP sau khi xác thực thành công
                        deleteOldOTP(username);
                        return 1;  // OTP hợp lệ
                    } else {
                        // Xóa OTP đã hết hạn
                        deleteOldOTP(username);
                        return -1;  // OTP hết hạn
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // OTP không đúng
    }

    // Thêm phương thức này
    private static void deleteOldOTP(String username) {
        String sql = "DELETE FROM otp WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    public static void cleanupExpiredOTP() {
//        String sql = "DELETE FROM otp WHERE expiration_time < ?";
//        try (Connection conn = DBConnection.getConnection();
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
//            stmt.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
    
    public static boolean canResendOTP(String username) {
		String sql = "SELECT timestamp FROM otp WHERE username = ? ORDER BY timestamp DESC LIMIT 1";
		try (Connection conn = DBConnection.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, username);
			try (ResultSet rs = stmt.executeQuery()) {
				if (rs.next()) {
					Timestamp lastTimestamp = rs.getTimestamp("timestamp");
					LocalDateTime lastTime = lastTimestamp.toLocalDateTime();
					LocalDateTime now = LocalDateTime.now();
					// Kiểm tra xem đã qua 1 phút chưa
					return ChronoUnit.MINUTES.between(lastTime, now) >= 1;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true; // Nếu không tìm thấy OTP trước đó, cho phép gửi lại
	}


    public static String getRoleByUsername(String username) {
        String role = null;
        String sql = "SELECT role FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
        	PreparedStatement stmt = conn.prepareStatement(sql) ) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                role = rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return role;
    }

    public static boolean updatePassword(String username, String newPassword) {
        String salt = SHA256WithSalt.generateSalt();
        String hashedPassword = SHA256WithSalt.hashPasswordWithSalt(newPassword, salt);

        String sql = "UPDATE users SET password_hash = ?, salt = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, hashedPassword);
            stmt.setString(2, salt);
            stmt.setString(3, username);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0; // Trả về true nếu cập nhật thành công
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getUsernameByEmail(String email) {
        String username = null;
        String sql = "SELECT username FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                username = rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
    public static boolean checkEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT username, email, role FROM users";
        
        try (PreparedStatement statement = DBConnection.getConnection().prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String email = resultSet.getString("email");
                String role = resultSet.getString("role");
                
                users.add(new User(username, email, role));
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Log lỗi nếu có
        }

        return users;
    }
}