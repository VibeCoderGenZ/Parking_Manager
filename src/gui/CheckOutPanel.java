package gui;

import controller.ParkingController;
import controller.ParkingException;
import model.Ticket;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Panel chứa giao diện và logic cho chức năng Check-out xe ra khỏi bãi.
 */
public class CheckOutPanel extends JPanel {

    // Controller
    private final ParkingController parkingController;

    // Các thành phần giao diện
    private JTextField licensePlateField;
    private JButton checkOutButton;

    public CheckOutPanel() {
        this.parkingController = new ParkingController();

        // --- Cấu hình layout cho panel ---
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Check-out Xe Ra Bãi"));

        // --- Khởi tạo và sắp xếp các thành phần ---
        initAndLayoutComponents();

        // --- Gắn hành động ---
        attachActions();
    }

    private void initAndLayoutComponents() {
        licensePlateField = new JTextField(15);
        checkOutButton = new JButton("Xác nhận Check-out và Tính tiền");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.LINE_END;

        // Hàng 1: Biển số xe
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Biển số xe cần check-out:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(licensePlateField, gbc);

        // Hàng 2: Nút bấm
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(checkOutButton, gbc);
    }

    private void attachActions() {
        checkOutButton.addActionListener(e -> handleCheckOut());
    }

    private void handleCheckOut() {
        String licensePlate = licensePlateField.getText().trim();

        if (licensePlate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập biển số xe.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Gọi controller để xử lý nghiệp vụ
            Ticket updatedTicket = parkingController.checkOut(licensePlate);

            // Định dạng tiền tệ cho đẹp
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedPrice = currencyFormatter.format(updatedTicket.getPrice());

            // Hiển thị hóa đơn thành công
            String successMessage = String.format(
                "Check-out thành công!\n\nBiển số: %s\nThời gian vào: %s\nThời gian ra: %s\n\nTổng tiền: %s",
                updatedTicket.getLicensePlate(),
                updatedTicket.getEntryTime().toString(),
                updatedTicket.getExitTime().toString(),
                formattedPrice
            );
            JOptionPane.showMessageDialog(this, successMessage, "Hóa Đơn Thanh Toán", JOptionPane.INFORMATION_MESSAGE);

            // Xóa trống ô nhập liệu
            licensePlateField.setText("");

        } catch (ParkingException ex) {
            // Bắt lỗi nghiệp vụ
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Nghiệp Vụ", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            // Bắt lỗi database
            JOptionPane.showMessageDialog(this, "Lỗi khi tương tác với cơ sở dữ liệu.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
