package dao;

import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Lấy tất cả người dùng từ database.
     *
     * @return Danh sách các đối tượng User.
     * @throws SQLException nếu có lỗi khi truy vấn database.
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                users.add(new User(username, password));
            }
        }
        return users;
    }

    /**
     * Lấy một người dùng từ database dựa vào username.
     *
     * @param username Tên người dùng cần tìm.
     * @return Đối tượng User nếu tìm thấy, ngược lại trả về null.
     * @throws SQLException nếu có lỗi khi truy vấn database.
     */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password");
                    return new User(username, password);
                }
            }
        }
        return null; // Trả về null nếu không tìm thấy
    }

    /**
     * Thêm một người dùng mới vào database.
     *
     * @param user Đối tượng User chứa thông tin cần thêm.
     * @throws SQLException nếu có lỗi khi thực thi (ví dụ: username đã tồn tại).
     */
    public void addUser(User user) throws SQLException {
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.executeUpdate();
        }
    }

    /**
     * Cập nhật thông tin (chủ yếu là mật khẩu) của người dùng.
     *
     * @param user Đối tượng User với thông tin mới.
     * @throws SQLException nếu có lỗi khi thực thi.
     */
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getUsername());
            pstmt.executeUpdate();
        }
    }

    /**
     * Xóa một người dùng khỏi database dựa vào username.
     *
     * @param username Tên người dùng cần xóa.
     * @throws SQLException nếu có lỗi khi thực thi.
     */
    public void deleteUser(String username) throws SQLException {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.executeUpdate();
        }
    }
}
