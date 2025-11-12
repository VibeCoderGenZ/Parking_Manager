package controller;

import dao.UserDAO;
import model.User;

import java.sql.SQLException;

/**
 * Lớp Controller chịu trách nhiệm xử lý các logic nghiệp vụ liên quan đến Người dùng.
 * Nó hoạt động như một cầu nối giữa GUI và DAO, không chứa bất kỳ code giao diện nào.
 */
public class UserController {

    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
    }

    /**
     * Xử lý logic đăng nhập.
     *
     * @param username Tên đăng nhập do người dùng nhập vào.
     * @param password Mật khẩu do người dùng nhập vào.
     * @return true nếu đăng nhập thành công.
     * @return false nếu sai tên đăng nhập hoặc mật khẩu.
     * @throws SQLException nếu có lỗi xảy ra ở tầng database (ví dụ: mất kết nối).
     */
    public boolean login(String username, String password) throws SQLException {
        // Bước 1: Gọi DAO để lấy thông tin người dùng từ database.
        // Nếu có lỗi ở đây, SQLException sẽ được ném thẳng ra cho GUI xử lý.
        User userFromDB = userDAO.getUserByUsername(username);

        // Bước 2: Xử lý logic và trả về kết quả.
        // GUI sẽ dựa vào kết quả trả về (true/false) để quyết định hành động tiếp theo.
        return userFromDB != null && userFromDB.getPassword().equals(password);
    }
    
    // Các phương thức khác liên quan đến user (ví dụ: đổi mật khẩu, tạo người dùng mới)
    // có thể được thêm vào đây trong tương lai.
    // Chúng cũng sẽ tuân thủ nguyên tắc ném Exception hoặc trả về kết quả, không in ra console.
}
