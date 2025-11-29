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
    /** Đối tượng xử lý logic chính của bãi đỗ xe. */
    private ParkingLot parkingLot;
    /**
     * Callback function: Được gọi khi người dùng muốn thêm xe mới (chuyển hướng
     * sang màn hình Quản lý xe).
     */
    private Runnable onAddNewVehicle;

    // --- Fields: UI Components ---
    private JTextField txtLicensePlate; // Ô nhập biển số xe
    private JButton btnPark; // Nút thực hiện tạo vé

    // --- Constructor ---
    /**
     * Khởi tạo Panel Tạo Vé.
     * 
     * @param parkingLot      Tham chiếu đến logic xử lý.
     * @param onAddNewVehicle Hành động cần thực hiện khi người dùng chọn "Thêm xe
     *                        mới".
     */
    public CreateTicketPanel(ParkingLot parkingLot, Runnable onAddNewVehicle) {
        this.parkingLot = parkingLot;
        this.onAddNewVehicle = onAddNewVehicle;

        setLayout(new GridBagLayout()); // Dùng GridBagLayout để căn giữa mọi thứ
        initComponents();
    }

    // --- Initialization Methods ---

    /**
     * Khởi tạo và bố trí các thành phần giao diện.
     */
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

        // 3. Nút hành động (Tạo vé)
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
        txtLicensePlate.addActionListener(this::handleParkAction); // Cho phép nhấn Enter để submit
    }

    // --- Business Logic ---

    /**
     * Xử lý logic khi nhấn nút Tạo vé.
     * 1. Validate biển số.
     * 2. Kiểm tra xe đã đăng ký chưa -> Nếu chưa thì hỏi thêm mới.
     * 3. Thực hiện logic đỗ xe (tìm chỗ trống, tạo vé).
     * 
     * @param e Sự kiện ActionEvent.
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

        // 3. Gọi logic đỗ xe (Tự động tìm chỗ phù hợp)
        boolean success = parkingLot.parkVehicleAuto(plate);

        if (success) {
            showSuccessMessage(plate, v);
        } else {
            showFailureMessage();
        }
    }

    /**
     * Hiển thị hộp thoại hỏi người dùng có muốn thêm xe mới không.
     * Nếu chọn YES -> Gọi callback onAddNewVehicle để chuyển màn hình.
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
     * Hiển thị thông báo thành công và chi tiết vé vừa tạo.
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

        // Reset form để nhập xe tiếp theo
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
