package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import logic.ParkingLot;
import logic.Ticket;
import logic.Vehicle;

/**
 * Panel thu vé (Chức năng ra bến).
 * Chức năng: Tìm vé (theo mã hoặc biển số) -> Xác nhận -> Thu vé/Trả xe.
 */
public class CollectTicketPanel extends JPanel {

    // --- Fields: Logic ---
    private ParkingLot parkingLot;

    // --- Fields: UI Components ---
    private JRadioButton rbByTicketId;
    private JRadioButton rbByPlate;
    private JLabel lblInstruction;
    private JTextField txtInput;
    private JButton btnCollect;

    // --- Constructor ---
    public CollectTicketPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;

        setLayout(new GridBagLayout());
        initComponents();
    }

    // --- Initialization Methods ---

    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Panel lựa chọn chế độ tìm kiếm
        JPanel pnlMode = createModeSelectionPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(pnlMode, gbc);

        // 2. Label hướng dẫn
        lblInstruction = new JLabel("Nhập mã vé:");
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

        // Gắn sự kiện
        initEvents();
    }

    private JPanel createModeSelectionPanel() {
        JPanel pnlMode = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rbByTicketId = new JRadioButton("Theo Mã Vé", true);
        rbByPlate = new JRadioButton("Theo Biển Số");

        ButtonGroup group = new ButtonGroup();
        group.add(rbByTicketId);
        group.add(rbByPlate);

        pnlMode.add(rbByTicketId);
        pnlMode.add(rbByPlate);
        return pnlMode;
    }

    private void initEvents() {
        btnCollect.addActionListener(this::handleCollectAction);
        txtInput.addActionListener(this::handleCollectAction);

        // Đổi label khi chọn radio button
        rbByPlate.addActionListener(e -> lblInstruction.setText("Nhập biển số xe:"));
        rbByTicketId.addActionListener(e -> lblInstruction.setText("Nhập mã vé:"));
    }

    // --- Business Logic ---

    /**
     * Xử lý logic khi nhấn nút Thu vé.
     */
    private void handleCollectAction(ActionEvent e) {
        String input = txtInput.getText().trim();

        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Ticket ticket = findTicket(input);
        if (ticket == null)
            return; // Đã xử lý thông báo lỗi trong findTicket

        confirmAndProcessReturn(ticket);
    }

    /**
     * Tìm vé dựa trên input và chế độ đã chọn.
     */
    private Ticket findTicket(String input) {
        if (rbByTicketId.isSelected()) {
            return findTicketById(input);
        } else {
            return findTicketByPlate(input);
        }
    }

    private Ticket findTicketById(String input) {
        try {
            int ticketId = Integer.parseInt(input);
            Ticket ticket = parkingLot.getTicketByTicketID(ticketId);

            if (ticket == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy vé có mã: " + ticketId, "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return null;
            }
            if (ticket.getExitTime() != null) {
                JOptionPane.showMessageDialog(this, "Vé này đã được sử dụng (Xe đã ra).", "Lỗi",
                        JOptionPane.WARNING_MESSAGE);
                return null;
            }
            return ticket;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã vé phải là số nguyên!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private Ticket findTicketByPlate(String input) {
        Ticket ticket = parkingLot.getTicketByLicensePlate(input);
        if (ticket == null || ticket.getExitTime() != null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy vé hoạt động cho xe biển số: " + input,
                    "Không tìm thấy",
                    JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return ticket;
    }

    /**
     * Hiển thị hộp thoại xác nhận và thực hiện trả xe.
     */
    private void confirmAndProcessReturn(Ticket ticket) {
        String plate = ticket.getLicensePlate();
        Vehicle v = parkingLot.getVehicleByLicensePlate(plate);
        String ownerInfo = (v != null) ? v.getOwnerName() : "Không rõ";
        String entryTimeStr = ticket.getEntryTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận trả xe?\n\n" +
                        "Mã vé: " + ticket.getTicketID() + "\n" +
                        "Biển số: " + plate + "\n" +
                        "Chủ xe: " + ownerInfo + "\n" +
                        "Giờ vào: " + entryTimeStr,
                "Xác nhận thu vé",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            processReturn(plate);
        }
    }

    private void processReturn(String plate) {
        boolean success = parkingLot.retrieveVehicle(plate);

        if (success) {
            String exitTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            JOptionPane.showMessageDialog(this,
                    "Đã thu vé và trả xe thành công!\nBiển số: " + plate + "\nGiờ ra: " + exitTimeStr,
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