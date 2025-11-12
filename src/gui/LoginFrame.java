package gui;

import controller.UserController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Lớp giao diện cho cửa sổ Đăng nhập.
 */
public class LoginFrame extends JFrame {

    // Các thành phần giao diện
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Controller để xử lý logic
    private final UserController userController;

    public LoginFrame() {
        this.userController = new UserController();

        // --- Cấu hình cho cửa sổ (JFrame) ---
        setTitle("Đăng nhập Hệ thống Quản lý Bãi xe");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát ứng dụng khi đóng cửa sổ
        setLocationRelativeTo(null); // Hiển thị cửa sổ ở giữa màn hình
        setResizable(false);

        // --- Khởi tạo các thành phần ---
        initComponents();

        // --- Sắp xếp layout ---
        layoutComponents();

        // --- Gắn hành động cho nút bấm ---
        attachActions();
    }

    /**
     * Khởi tạo các thành phần trên giao diện.
     */
    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Đăng nhập");
    }

    /**
     * Sắp xếp vị trí của các thành phần trên cửa sổ.
     */
    private void layoutComponents() {
        // Sử dụng JPanel với GridBagLayout để căn chỉnh đẹp hơn
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các thành phần

        // Hàng 1: Nhãn "Tên đăng nhập"
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Tên đăng nhập:"), gbc);

        // Hàng 1: Ô nhập username
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(usernameField, gbc);

        // Hàng 2: Nhãn "Mật khẩu"
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        panel.add(new JLabel("Mật khẩu:"), gbc);

        // Hàng 2: Ô nhập password
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        panel.add(passwordField, gbc);

        // Hàng 3: Nút Đăng nhập
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        // Thêm panel vào frame
        this.add(panel);
    }

    /**
     * Gắn các hành động (sự kiện) cho thành phần, ví dụ như khi click nút.
     */
    private void attachActions() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    /**
     * Xử lý sự kiện khi người dùng bấm nút đăng nhập.
     * Đây là nơi GUI gọi đến Controller.
     */
    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        // Kiểm tra đầu vào cơ bản
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Gọi phương thức login từ Controller
            boolean loginSuccess = userController.login(username, password);

            if (loginSuccess) {
                // Đăng nhập thành công: Mở cửa sổ chính và đóng cửa sổ đăng nhập.
                SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
                this.dispose();
            } else {
                // Sai tên đăng nhập hoặc mật khẩu
                JOptionPane.showMessageDialog(this, "Sai tên đăng nhập hoặc mật khẩu.", "Lỗi Đăng nhập", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            // Lỗi từ tầng database
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu. Vui lòng kiểm tra lại.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
