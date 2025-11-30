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
    /**
     * Đối tượng xử lý logic chính của bãi đỗ xe (tìm kiếm, tính toán, cập nhật
     * trạng thái).
     */
    private ParkingLot parkingLot;

    // --- Fields: UI Components ---
    private JRadioButton rbByTicketId; // Lựa chọn tìm theo mã vé
    private JRadioButton rbByPlate; // Lựa chọn tìm theo biển số
    private JLabel lblInstruction; // Nhãn hướng dẫn người dùng nhập gì
    private JTextField txtInput; // Ô nhập liệu
    private JButton btnCollect; // Nút thực hiện thu vé

    // --- Constructor ---
    /**
     * Khởi tạo Panel Thu Vé.
     * 
     * @param parkingLot Tham chiếu đến logic xử lý bãi đỗ xe.
     */
    public CollectTicketPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;

        setLayout(new GridBagLayout());
        initComponents();
    }

    // --- Initialization Methods ---

    /**
     * Khởi tạo và sắp xếp các thành phần giao diện (UI components).
     * Sử dụng GridBagLayout để căn chỉnh linh hoạt.
     */
    private void initComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 1. Panel lựa chọn chế độ tìm kiếm (Radio Buttons)
        JPanel pnlMode = createModeSelectionPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(pnlMode, gbc);

        // 2. Label hướng dẫn (Thay đổi nội dung dựa trên Radio Button được chọn)
        lblInstruction = new JLabel("Nhập mã vé:");
        lblInstruction.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblInstruction.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(lblInstruction, gbc);

        // 3. Text Field (Nơi nhập mã vé hoặc biển số)
        txtInput = new JTextField(20);
        txtInput.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtInput.setHorizontalAlignment(JTextField.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(txtInput, gbc);

        // 4. Button (Nút kích hoạt hành động thu vé)
        btnCollect = new JButton("Thu Vé / Trả Xe");
        btnCollect.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCollect.setBackground(new Color(204, 0, 0)); // Màu đỏ để phân biệt với nút Tạo vé (Xanh)
        btnCollect.setForeground(Color.WHITE);
        btnCollect.setFocusPainted(false);
        btnCollect.setPreferredSize(new Dimension(200, 40));

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(btnCollect, gbc);

        // Gắn sự kiện cho các component
        initEvents();
    }

    /**
     * Tạo panel chứa 2 RadioButton để chọn chế độ tìm kiếm.
     * 
     * @return JPanel chứa các RadioButton đã được group.
     */
    private JPanel createModeSelectionPanel() {
        JPanel pnlMode = new JPanel(new FlowLayout(FlowLayout.CENTER));
        rbByTicketId = new JRadioButton("Theo Mã Vé", true);
        rbByPlate = new JRadioButton("Theo Biển Số");

        // Group để đảm bảo chỉ chọn được 1 trong 2
        ButtonGroup group = new ButtonGroup();
        group.add(rbByTicketId);
        group.add(rbByPlate);

        pnlMode.add(rbByTicketId);
        pnlMode.add(rbByPlate);
        return pnlMode;
    }

    /**
     * Thiết lập các Event Listener (Lắng nghe sự kiện người dùng).
     */
    private void initEvents() {
        // Sự kiện khi nhấn nút hoặc nhấn Enter trong ô nhập liệu
        btnCollect.addActionListener(this::handleCollectAction);
        txtInput.addActionListener(this::handleCollectAction);

        // Đổi label hướng dẫn khi người dùng thay đổi chế độ tìm kiếm
        rbByPlate.addActionListener(e -> lblInstruction.setText("Nhập biển số xe:"));
        rbByTicketId.addActionListener(e -> lblInstruction.setText("Nhập mã vé:"));
    }

    // --- Business Logic ---

    /**
     * Xử lý logic chính khi người dùng yêu cầu thu vé.
     * 1. Kiểm tra input.
     * 2. Tìm vé tương ứng.
     * 3. Nếu tìm thấy -> Hiển thị xác nhận -> Thực hiện trả xe.
     * 
     * @param e Sự kiện ActionEvent (từ nút bấm hoặc phím Enter).
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
     * Tìm vé dựa trên input và chế độ đã chọn (Mã vé hoặc Biển số).
     * 
     * @param input Chuỗi nhập liệu từ người dùng.
     * @return Đối tượng Ticket nếu tìm thấy và hợp lệ, ngược lại trả về null.
     */
    private Ticket findTicket(String input) {
        if (rbByTicketId.isSelected()) {
            return findTicketById(input);
        } else {
            return findTicketByPlate(input);
        }
    }

    /**
     * Tìm vé theo ID.
     * Kiểm tra tính hợp lệ của ID (phải là số) và trạng thái vé (chưa ra bến).
     */
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
            JOptionPane.showMessageDialog(this, "Mã vé không chính xác", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Tìm vé theo Biển số xe.
     * Chỉ trả về vé nếu xe đang còn trong bãi (chưa có giờ ra).
     */
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
     * Hiển thị hộp thoại xác nhận thông tin trước khi thực hiện trả xe.
     * Giúp người dùng kiểm tra lại thông tin xe và chủ xe.
     * 
     * @param ticket Vé cần xử lý.
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

    /**
     * Thực hiện logic trả xe (cập nhật giờ ra, giải phóng chỗ đỗ) và thông báo kết
     * quả.
     * 
     * @param plate Biển số xe cần trả.
     */
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
