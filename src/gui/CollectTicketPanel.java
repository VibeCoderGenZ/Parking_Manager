package gui;

import javax.swing.*;
import java.awt.*;

import logic.ParkingLot;
import logic.Ticket;
import logic.Vehicle;

import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;

public class CollectTicketPanel extends JPanel {

    private ParkingLot parkingLot;
    private JRadioButton rbByPlate;
    private JRadioButton rbByTicketId;
    private JTextField txtInput;
    private JButton btnCollect;

    public CollectTicketPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new GridBagLayout());

        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Panel lựa chọn chế độ tìm kiếm
        JPanel pnlMode = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rbByTicketId = new JRadioButton("Theo Mã Vé", true);
        rbByPlate = new JRadioButton("Theo Biển Số");

        ButtonGroup group = new ButtonGroup();
        group.add(rbByTicketId);
        group.add(rbByPlate);

        pnlMode.add(rbByTicketId);
        pnlMode.add(rbByPlate);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(pnlMode, gbc);

        // 2. Label hướng dẫn
        JLabel lblInstruction = new JLabel("Nhập mã vé:");
        lblInstruction.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInstruction.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblInstruction, gbc);

        // 3. Text Field
        txtInput = new JTextField(20);
        txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtInput.setHorizontalAlignment(JTextField.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(txtInput, gbc);

        // 4. Button
        btnCollect = new JButton("Thu Vé / Trả Xe");
        btnCollect.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCollect.setBackground(new Color(204, 0, 0)); // Màu đỏ
        btnCollect.setForeground(Color.WHITE);
        btnCollect.setFocusPainted(false);
        btnCollect.setPreferredSize(new Dimension(200, 40));

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(btnCollect, gbc);

        // Events
        btnCollect.addActionListener(this::handleCollectAction);
        txtInput.addActionListener(this::handleCollectAction);

        // Đổi label khi chọn radio button
        rbByPlate.addActionListener(e -> lblInstruction.setText("Nhập biển số xe:"));
        rbByTicketId.addActionListener(e -> lblInstruction.setText("Nhập mã vé:"));
    }

    private void handleCollectAction(ActionEvent e) {
        String input = txtInput.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Ticket ticket = null;

        // --- BƯỚC 1: TÌM VÉ ---
        if (rbByPlate.isSelected()) {
            // Tìm theo biển số
            ticket = parkingLot.getTicketByLicensePlate(input);
            if (ticket == null || ticket.getExitTime() != null) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy vé hoạt động cho xe biển số: " + input,
                        "Không tìm thấy",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            // Tìm theo mã vé
            try {
                int ticketId = Integer.parseInt(input);
                ticket = parkingLot.getTicketByTicketID(ticketId);
                if (ticket == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy vé có mã: " + ticketId, "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (ticket.getExitTime() != null) {
                    JOptionPane.showMessageDialog(this, "Vé này đã được sử dụng (Xe đã ra).", "Lỗi",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã vé phải là số nguyên!", "Lỗi định dạng",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // --- BƯỚC 2: LẤY THÔNG TIN XE ---
        String plate = ticket.getLicensePlate();
        Vehicle v = parkingLot.getVehicleByLicensePlate(plate);

        // --- BƯỚC 3: XÁC NHẬN ---
        String ownerInfo = (v != null) ? v.getOwnerName() : "Không rõ";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận trả xe?\n\n" +
                        "Mã vé: " + ticket.getTicketID() + "\n" +
                        "Biển số: " + plate + "\n" +
                        "Chủ xe: " + ownerInfo + "\n" +
                        "Giờ vào: " + ticket.getEntryTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                "Xác nhận thu vé",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // --- BƯỚC 4: THỰC HIỆN TRẢ XE ---
        boolean success = parkingLot.retrieveVehicle(plate);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Đã thu vé và trả xe thành công!\nBiển số: " + plate + "\nGiờ ra: "
                            + java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")),
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            txtInput.setText("");
            txtInput.requestFocus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Có lỗi xảy ra khi trả xe. Vui lòng thử lại.",
                    "Thất bại",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
