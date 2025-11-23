package gui;

import logic.ParkingLot;
import logic.ParkingSpot;
import logic.VehicleType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class SpotManagementPanel extends JPanel {

    private ParkingLot parkingLot;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Object> cbFilterType; // Object để chứa cả String "Tất cả" và Enum
    private JComboBox<String> cbFilterStatus; // Lọc theo trạng thái
    private JButton btnAddSpots;
    private JButton btnResetAll;

    public SpotManagementPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout());

        initComponents();
        loadData();
    }

    private void initComponents() {
        // 1. Top Panel (Filter)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Filter Type
        topPanel.add(new JLabel("Lọc theo loại xe:"));
        DefaultComboBoxModel<Object> cbModel = new DefaultComboBoxModel<>();
        cbModel.addElement("Tất cả");
        for (VehicleType type : VehicleType.values()) {
            cbModel.addElement(type);
        }
        cbFilterType = new JComboBox<>(cbModel);
        cbFilterType.addActionListener(e -> loadData());
        topPanel.add(cbFilterType);

        // Filter Status
        topPanel.add(new JLabel("   Lọc theo trạng thái:"));
        String[] statuses = { "Tất cả", "Trống", "Đang có xe" };
        cbFilterStatus = new JComboBox<>(statuses);
        cbFilterStatus.addActionListener(e -> loadData());
        topPanel.add(cbFilterStatus);

        add(topPanel, BorderLayout.NORTH);

        // 2. Center (Table)
        String[] columnNames = { "Mã Vị Trí", "Loại Xe Cho Phép", "Trạng Thái", "Biển Số Xe Đang Đỗ" };
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

        // 3. Bottom (Buttons)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnAddSpots = new JButton("Thêm Vị Trí Đỗ");
        btnResetAll = new JButton("Cấu Hình Lại (Reset)");

        styleButton(btnAddSpots, new Color(0, 102, 204));
        styleButton(btnResetAll, new Color(153, 0, 0));

        bottomPanel.add(btnAddSpots);
        bottomPanel.add(btnResetAll);

        add(bottomPanel, BorderLayout.SOUTH);

        // Events
        btnAddSpots.addActionListener(e -> showAddSpotsDialog());
        btnResetAll.addActionListener(e -> showResetConfirmation());
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(200, 35));
    }

    public void loadData() {
        tableModel.setRowCount(0);
        Object selectedType = cbFilterType.getSelectedItem();
        String selectedStatus = (String) cbFilterStatus.getSelectedItem();

        ArrayList<ParkingSpot> allSpots = parkingLot.getSpots();

        for (ParkingSpot spot : allSpots) {
            // Filter by Type
            if (!"Tất cả".equals(selectedType) && spot.getAllowedType() != selectedType) {
                continue;
            }

            // Filter by Status
            if ("Trống".equals(selectedStatus) && spot.isOccupied()) {
                continue;
            }
            if ("Đang có xe".equals(selectedStatus) && !spot.isOccupied()) {
                continue;
            }

            String status = spot.isOccupied() ? "Đang có xe" : "Trống";
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

    private void showAddSpotsDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Vị Trí Đỗ", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<VehicleType> cbType = new JComboBox<>(VehicleType.values());
        JSpinner spinnerQty = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

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

        JButton btnConfirm = new JButton("Thêm");
        btnConfirm.setBackground(new Color(0, 102, 204));
        btnConfirm.setForeground(Color.WHITE);

        btnConfirm.addActionListener(e -> {
            VehicleType type = (VehicleType) cbType.getSelectedItem();
            int qty = (int) spinnerQty.getValue();

            int count = 0;
            while (count < qty) {
                // Sử dụng hàm addSpot mới (chỉ cần truyền loại xe)
                parkingLot.addSpot(type);
                count++;
            }

            JOptionPane.showMessageDialog(dialog, "Đã thêm " + qty + " vị trí đỗ mới.");
            loadData();
            dialog.dispose();
        });

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        dialog.add(btnConfirm, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showResetConfirmation() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "CẢNH BÁO NGUY HIỂM:\n" +
                        "Hành động này sẽ xóa TOÀN BỘ  \"Vị Trí Đỗ\"  và TOÀN BỘ  \"Vé\"  hiện có.\n" +
                        "Dữ liệu sẽ không thể phục hồi.\n\n" +
                        "Bạn có chắc chắn muốn tiếp tục?",
                "Xác nhận Reset Hệ Thống",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            parkingLot.resetTickets(); // Xóa vé trước để tránh lỗi ràng buộc logic nếu có
            parkingLot.resetSpots(); // Xóa vị trí

            loadData();
            JOptionPane.showMessageDialog(this, "Hệ thống bãi đỗ đã được làm mới hoàn toàn.");
        }
    }
}
