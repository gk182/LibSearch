package model;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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


	public static boolean register(String username,String email, String password) {
        // Tạo salt
        String salt = SHA256WithSalt.generateSalt();

        // Hash mật khẩu với salt
        String hashedPassword = SHA256WithSalt.hashPasswordWithSalt(password, salt);

        try (Connection conn = DBConnection.getConnection()) {
            // Câu lệnh SQL để thêm người dùng mới
            String sql = "INSERT INTO users (username, email, password_hash, salt) VALUES (?,?,?,?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, hashedPassword);
                stmt.setString(4, salt);

                // Thực thi câu lệnh
                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0; // Trả về true nếu thêm thành công
            }
        } catch (Exception e) {
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

    public static void cleanupExpiredOTP() {
        String sql = "DELETE FROM otp WHERE expiration_time < ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
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

    public static boolean checkUserExistsByGoogleId(String googleId) {
        boolean userExists = false;
        String query = "SELECT * FROM users WHERE google_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, googleId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userExists = true; // Nếu kết quả tồn tại, user đã tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userExists;
    }

    public static boolean addUserGoogle(String username, String email, String googleId) {
    	String passwordString = OTPGenerator.generateOTP();
    	String salt = SHA256WithSalt.generateSalt();

        // Hash mật khẩu với salt
        String hashedPassword = SHA256WithSalt.hashPasswordWithSalt(passwordString, salt);
        try (Connection conn = DBConnection.getConnection()) {
            // Câu lệnh SQL để thêm người dùng mới
            String sql = "INSERT INTO users (username, email, google_id, password_hash, salt) VALUES (?,?,?,?,?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, googleId); // Lưu google_id
                stmt.setString(4, hashedPassword);
                stmt.setString(5, salt);

                // Thực thi câu lệnh
                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0; // Trả về true nếu thêm thành công
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static boolean loginWithGoogle(String googleId, String username, String email) {
        // Kiểm tra xem tài khoản Google đã tồn tại trong cơ sở dữ liệu chưa
        if (checkUserExistsByGoogleId(googleId)) {
            System.out.println("Tài khoản Google đã tồn tại.");
            return true; // Tài khoản đã tồn tại, cho phép đăng nhập
        } else {
            // Nếu chưa tồn tại, thêm tài khoản Google mới vào cơ sở dữ liệu
            boolean isAdded = addUserGoogle(username, email, googleId);
            if (isAdded) {
                System.out.println("Tạo tài khoản Google mới thành công.");
                return true; // Tài khoản Google đã được thêm thành công
            } else {
                System.out.println("Thêm tài khoản Google không thành công.");
                return false; // Thêm tài khoản không thành công
            }
        }
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

    public static void main(String[] args) {
        String username = "giakhai182";
        String otp = "104079";
        int a = verifyOTP(username,otp);
        if (a == 1) {
            System.out.println("Đăng nhập thành công!");
        }
        else if (a == -1 ) {
        	System.out.println("Het Han");
		}
        else if (a == 0) {
        	System.out.println("Sai");
		}
    }
}