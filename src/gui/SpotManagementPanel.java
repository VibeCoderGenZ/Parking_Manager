package gui;

import logic.ParkingLot;
import logic.ParkingSpot;
import logic.VehicleType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Panel quản lý vị trí đỗ xe (Spot Management).
 * Chức năng: Xem danh sách, Lọc, Thêm vị trí, Reset hệ thống.
 */
public class SpotManagementPanel extends JPanel {

    // --- Fields: Logic ---
    private ParkingLot parkingLot;

    // --- Fields: UI Components ---
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Object> cbFilterType; // Lọc theo loại xe (Tất cả / Xe máy / Ô tô...)
    private JComboBox<String> cbFilterStatus; // Lọc theo trạng thái (Tất cả / Trống / Có xe)
    private JButton btnAddSpots;
    private JButton btnResetAll;

    // --- Constructor ---
    public SpotManagementPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout());

        // Khởi tạo giao diện
        initComponents();

        // Tải dữ liệu ban đầu
        loadData();
    }

    // --- Initialization Methods ---

    private void initComponents() {
        initTopPanel(); // Phần bộ lọc ở trên cùng
        initCenterPanel(); // Bảng dữ liệu ở giữa
        initBottomPanel(); // Các nút chức năng ở dưới cùng
    }

    /**
     * Tạo panel phía trên chứa các bộ lọc (Filter).
     */
    private void initTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // 1. Bộ lọc Loại xe
        topPanel.add(new JLabel("Lọc theo loại xe:"));
        DefaultComboBoxModel<Object> cbModel = new DefaultComboBoxModel<>();
        cbModel.addElement("Tất cả");
        for (VehicleType type : VehicleType.values()) {
            cbModel.addElement(type);
        }
        cbFilterType = new JComboBox<>(cbModel);
        cbFilterType.addActionListener(e -> loadData()); // Reload khi chọn
        topPanel.add(cbFilterType);

        // 2. Bộ lọc Trạng thái
        topPanel.add(new JLabel("   Lọc theo trạng thái:"));
        String[] statuses = { "Tất cả", "Trống", "Có xe" };
        cbFilterStatus = new JComboBox<>(statuses);
        cbFilterStatus.addActionListener(e -> loadData()); // Reload khi chọn
        topPanel.add(cbFilterStatus);

        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * Tạo panel ở giữa chứa bảng hiển thị dữ liệu.
     */
    private void initCenterPanel() {
        String[] columnNames = { "Mã Vị Trí", "Loại Xe Cho Phép", "Trạng Thái", "Biển Số Xe Đang Đỗ" };

        // Tạo model bảng không cho phép sửa trực tiếp
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Tạo panel phía dưới chứa các nút chức năng (Thêm, Reset).
     */
    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnAddSpots = new JButton("Thêm Vị Trí Đỗ");
        btnResetAll = new JButton("Cấu Hình Lại (Reset)");

        // Style cho nút đẹp hơn
        styleButton(btnAddSpots, new Color(0, 102, 204)); // Xanh dương
        styleButton(btnResetAll, new Color(153, 0, 0)); // Đỏ đậm

        // Gán sự kiện cho nút
        btnAddSpots.addActionListener(e -> showAddSpotsDialog());
        btnResetAll.addActionListener(e -> showResetConfirmation());

        bottomPanel.add(btnAddSpots);
        bottomPanel.add(btnResetAll);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Helper method để style nút bấm thống nhất.
     */
    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 35));
    }

    // --- Business Logic & Data Loading ---

    /**
     * Tải dữ liệu từ ParkingLot vào bảng, có áp dụng bộ lọc.
     */
    public void loadData() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        // Lấy giá trị từ bộ lọc
        Object selectedType = cbFilterType.getSelectedItem();
        String selectedStatus = (String) cbFilterStatus.getSelectedItem();

        ArrayList<ParkingSpot> allSpots = parkingLot.getSpots();

        for (ParkingSpot spot : allSpots) {
            // 1. Kiểm tra lọc theo Loại xe
            if (!"Tất cả".equals(selectedType) && spot.getAllowedType() != selectedType) {
                continue; // Bỏ qua nếu không khớp loại
            }

            // 2. Kiểm tra lọc theo Trạng thái
            if ("Trống".equals(selectedStatus) && spot.isOccupied()) {
                continue; // Đang tìm chỗ trống mà chỗ này có xe -> Bỏ qua
            }
            if ("Có xe".equals(selectedStatus) && !spot.isOccupied()) {
                continue; // Đang tìm chỗ có xe mà chỗ này trống -> Bỏ qua
            }

            // 3. Thêm vào bảng
            String status = spot.isOccupied() ? "Có xe" : "Trống";
            String plate = spot.getLicensePlate() == null ? "---" : spot.getLicensePlate();

            Object[] row = {
                    spot.getSpotID(),
                    spot.getAllowedType(),
                    status,
                    plate
            };
            tableModel.addRow(row);
        }
    }

    // --- Dialogs & Actions ---

    /**
     * Hiển thị Dialog để thêm nhiều vị trí đỗ cùng lúc.
     */
    private void showAddSpotsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Vị Trí Đỗ", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Components trong Dialog
        JComboBox<VehicleType> cbType = new JComboBox<>(VehicleType.values());
        JSpinner spinnerQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JButton btnConfirm = new JButton("Thêm");

        // Style nút xác nhận
        btnConfirm.setBackground(new Color(0, 102, 204));
        btnConfirm.setForeground(Color.WHITE);

        // Layout components
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Loại xe cho phép:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        dialog.add(cbType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Số lượng thêm:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        dialog.add(spinnerQty, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(btnConfirm, gbc);

        // Logic nút Thêm
        btnConfirm.addActionListener(e -> {
            VehicleType type = (VehicleType) cbType.getSelectedItem();
            int qty = (int) spinnerQty.getValue();

            int count = 0;
            while (count < qty) {
                parkingLot.addSpot(type);
                count++;
            }

            JOptionPane.showMessageDialog(dialog, "Đã thêm " + qty + " vị trí đỗ mới.");
            loadData(); // Refresh lại bảng chính
            dialog.dispose();
        });

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Hiển thị cảnh báo và thực hiện Reset toàn bộ dữ liệu.
     */
    private void showResetConfirmation() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "CẢNH BÁO NGUY HIỂM:\n" +
                        "Hành động này sẽ xóa TOÀN BỘ \"Vị Trí Đỗ\" và TOÀN BỘ \"Vé\" hiện có.\n" +
                        "Dữ liệu sẽ không thể phục hồi.\n\n" +
                        "Bạn có chắc chắn muốn tiếp tục?",
                "Xác nhận Reset Hệ Thống",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            parkingLot.resetTickets(); // Xóa vé trước để tránh lỗi ràng buộc
            parkingLot.resetSpots(); // Xóa vị trí

            loadData(); // Refresh lại bảng (sẽ trống trơn)
            JOptionPane.showMessageDialog(this, "Hệ thống bãi đỗ đã được làm mới hoàn toàn.");
        }
    }
}
