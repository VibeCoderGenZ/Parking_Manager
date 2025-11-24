package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;

import logic.ParkingLot;
import logic.Ticket;
import logic.Vehicle;

/**
 * Panel tạo vé (Chức năng vào bến).
 * Chức năng: Nhập biển số -> Kiểm tra xe -> Tạo vé -> Thông báo.
 */
public class CreateTicketPanel extends JPanel {

    // --- Fields: Logic ---
    private ParkingLot parkingLot;
    private Runnable onAddNewVehicle; // Callback để chuyển sang màn hình thêm xe

    // --- Fields: UI Components ---
    private JTextField txtLicensePlate;
    private JButton btnPark;

    // --- Constructor ---
    public CreateTicketPanel(ParkingLot parkingLot, Runnable onAddNewVehicle) {
        this.parkingLot = parkingLot;
        this.onAddNewVehicle = onAddNewVehicle;

        setLayout(new GridBagLayout()); // Dùng GridBagLayout để căn giữa mọi thứ
        initComponents();
    }

    // --- Initialization Methods ---

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Khoảng cách giữa các thành phần
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Label hướng dẫn
        JLabel lblInstruction = new JLabel("Nhập biển số xe để tạo vé:");
        lblInstruction.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInstruction.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(lblInstruction, gbc);

        // 2. Ô nhập biển số
        txtLicensePlate = new JTextField(20);
        txtLicensePlate.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtLicensePlate.setHorizontalAlignment(JTextField.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(txtLicensePlate, gbc);

        // 3. Nút hành động
        btnPark = new JButton("Tạo Vé / Đỗ Xe");
        btnPark.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPark.setBackground(new Color(0, 102, 204)); // Xanh dương
        btnPark.setForeground(Color.WHITE);
        btnPark.setFocusPainted(false);
        btnPark.setPreferredSize(new Dimension(200, 40));

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(btnPark, gbc);

        // Gắn sự kiện
        btnPark.addActionListener(this::handleParkAction);
        txtLicensePlate.addActionListener(this::handleParkAction); // Cho phép nhấn Enter
    }

    // --- Business Logic ---

    /**
     * Xử lý logic khi nhấn nút Tạo vé.
     */
    private void handleParkAction(ActionEvent e) {
        String plate = txtLicensePlate.getText().trim();

        // 1. Validate đầu vào
        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập biển số xe!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Kiểm tra xe có tồn tại trong hệ thống chưa
        Vehicle v = parkingLot.getVehicleByLicensePlate(plate);
        if (v == null) {
            askToAddNewVehicle(plate);
            return;
        }

        // 3. Gọi logic đỗ xe
        boolean success = parkingLot.parkVehicleAuto(plate);

        if (success) {
            showSuccessMessage(plate, v);
        } else {
            showFailureMessage();
        }
    }

    /**
     * Hiển thị hộp thoại hỏi người dùng có muốn thêm xe mới không.
     */
    private void askToAddNewVehicle(String plate) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Xe biển số " + plate + " chưa được đăng ký trong hệ thống.\nBạn có muốn thêm xe này ngay không?",
                "Xe chưa tồn tại",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            if (onAddNewVehicle != null) {
                onAddNewVehicle.run();
            }
        }
    }

    /**
     * Hiển thị thông báo thành công và chi tiết vé.
     */
    private void showSuccessMessage(String plate, Vehicle v) {
        Ticket t = parkingLot.getTicketByLicensePlate(plate);
        String entryTimeStr = t.getEntryTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        JOptionPane.showMessageDialog(this,
                "Đã tạo vé thành công!\n" +
                        "Biển số: " + plate + "\n" +
                        "Loại xe: " + v.getType() + "\n" +
                        "Mã vé: " + t.getTicketID() + "\n" +
                        "Vị trí đỗ: " + t.getSpotID() + "\n" +
                        "Giờ vào: " + entryTimeStr,
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);

        // Reset form
        txtLicensePlate.setText("");
        txtLicensePlate.requestFocus();
    }

    /**
     * Hiển thị thông báo lỗi khi không tạo được vé.
     */
    private void showFailureMessage() {
        JOptionPane.showMessageDialog(this,
                "Không thể tạo vé!\nNguyên nhân có thể:\n- Xe đang đỗ trong bãi\n- Còn vé chưa thanh toán\n- Hết chỗ đỗ phù hợp",
                "Thất bại",
                JOptionPane.ERROR_MESSAGE);
    }
}