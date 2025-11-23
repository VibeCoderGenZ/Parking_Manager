package gui;

import javax.swing.*;
import java.awt.*;

import logic.ParkingLot;
import logic.Vehicle;

import java.awt.event.ActionEvent;

public class CreateTicketPanel extends JPanel {

    private ParkingLot parkingLot;
    private JTextField txtLicensePlate;
    private JButton btnPark;
    private Runnable onAddNewVehicle;

    public CreateTicketPanel(ParkingLot parkingLot, Runnable onAddNewVehicle) {
        this.parkingLot = parkingLot;
        this.onAddNewVehicle = onAddNewVehicle;
        setLayout(new GridBagLayout()); // Dùng GridBagLayout để căn giữa mọi thứ

        initComponents();
    }

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
        btnPark.setBackground(new Color(0, 102, 204));
        btnPark.setForeground(Color.WHITE);
        btnPark.setFocusPainted(false);
        btnPark.setPreferredSize(new Dimension(200, 40));

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(btnPark, gbc);

        // Xử lý sự kiện bấm nút
        btnPark.addActionListener(this::handleParkAction);

        // Xử lý sự kiện nhấn Enter trong ô text
        txtLicensePlate.addActionListener(this::handleParkAction);
    }

    private void handleParkAction(ActionEvent e) {
        String plate = txtLicensePlate.getText().trim();

        if (plate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập biển số xe!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra xe có tồn tại trong hệ thống chưa
        Vehicle v = parkingLot.getVehicleByLicensePlate(plate);
        if (v == null) {
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
            return;
        }

        // Gọi logic đỗ xe
        boolean success = parkingLot.parkVehicleAuto(plate);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Đã tạo vé thành công cho xe: " + plate + "\nLoại xe: " + v.getType(),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            txtLicensePlate.setText(""); // Xóa ô nhập để nhập xe tiếp theo
            txtLicensePlate.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tạo vé!\nNguyên nhân có thể:\n- Xe đang đỗ trong bãi\n- Còn vé chưa thanh toán\n- Hết chỗ đỗ phù hợp",
                    "Thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
