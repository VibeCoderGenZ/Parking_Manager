package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lớp này chịu trách nhiệm quản lý kết nối đến cơ sở dữ liệu.
 */
public class DBConnection {

    // --- THAY ĐỔI CÁC THÔNG SỐ NÀY CHO PHÙ HỢP VỚI DATABASE CỦA BẠN ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/parking_manager"; // Ví dụ cho MySQL
    private static final String USER = "root";       // Tên người dùng database
    private static final String PASS = "1234";   // Mật khẩu database
    // --------------------------------------------------------------------

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // TEST KẾT NỐI ĐẾN DATABASE
    public static void main(String[] args) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection != null) {
                System.out.println("Kết nối đến cơ sở dữ liệu thành công!");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi kết nối đến cơ sở dữ liệu: " + e.getMessage());
        }
    }
}
