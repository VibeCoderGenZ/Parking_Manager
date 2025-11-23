package gui;

import logic.ParkingLot;
import logic.Vehicle;
import logic.VehicleType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class VehicleManagementPanel extends JPanel {

    private ParkingLot parkingLot;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnRemoveAll;

    public VehicleManagementPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout());

        initComponents();
        loadData();
    }

    private void initComponents() {
        // 1. Table (Center)
        String[] columnNames = { "Biển Số", "Loại Xe", "Chủ Xe", "Số Điện Thoại" };
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

        // 2. Buttons (Bottom)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnAdd = new JButton("Thêm Xe");
        btnRemove = new JButton("Xóa Xe");
        btnRemoveAll = new JButton("Xóa Tất Cả");

        styleButton(btnAdd, new Color(0, 102, 204)); // Xanh dương
        styleButton(btnRemove, new Color(204, 0, 0)); // Đỏ
        styleButton(btnRemoveAll, new Color(153, 0, 0)); // Đỏ đậm

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnRemove);
        bottomPanel.add(btnRemoveAll);

        add(bottomPanel, BorderLayout.SOUTH);

        // 3. Events
        btnAdd.addActionListener(e -> showAddVehicleDialog());
        btnRemove.addActionListener(e -> showRemoveVehicleDialog());
        btnRemoveAll.addActionListener(e -> showRemoveAllConfirmation());
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 35));
    }

    // Hàm public để refresh dữ liệu
    public void loadData() {
        tableModel.setRowCount(0);
        ArrayList<Vehicle> list = parkingLot.getVehicles();
        for (Vehicle v : list) {
            Object[] row = {
                    v.getLicensePlate(),
                    v.getType(),
                    v.getOwnerName(),
                    v.getOwnerPhone()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddVehicleDialog() {
        // Tạo Dialog nhập liệu
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Xe Mới", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtPlate = new JTextField(15);
        JComboBox<VehicleType> cbType = new JComboBox<>(VehicleType.values());
        JTextField txtOwner = new JTextField(15);
        JTextField txtPhone = new JTextField(15);

        // Row 0: Plate
        gbc.gridx = 0;
        gbc.gridy = 0;
        dialog.add(new JLabel("Biển số:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        dialog.add(txtPlate, gbc);

        // Row 1: Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        dialog.add(new JLabel("Loại xe:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        dialog.add(cbType, gbc);

        // Row 2: Owner
        gbc.gridx = 0;
        gbc.gridy = 2;
        dialog.add(new JLabel("Chủ xe:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        dialog.add(txtOwner, gbc);

        // Row 3: Phone
        gbc.gridx = 0;
        gbc.gridy = 3;
        dialog.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        dialog.add(txtPhone, gbc);

        // Row 4: Button Confirm
        JButton btnConfirm = new JButton("Thêm");
        btnConfirm.setBackground(new Color(0, 102, 204));
        btnConfirm.setForeground(Color.WHITE);

        btnConfirm.addActionListener(e -> {
            String plate = txtPlate.getText().trim();
            VehicleType type = (VehicleType) cbType.getSelectedItem();
            String owner = txtOwner.getText().trim();
            String phone = txtPhone.getText().trim();

            if (plate.isEmpty() || owner.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng điền đầy đủ thông tin!", "Lỗi",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = parkingLot.addVehicle(plate, type, owner, phone);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Thêm xe thành công!");
                loadData(); // Refresh bảng
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Thêm thất bại!\n(Có thể biển số đã tồn tại)", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        dialog.add(btnConfirm, gbc);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showRemoveVehicleDialog() {
        String plate = JOptionPane.showInputDialog(this, "Nhập biển số xe cần xóa:");
        if (plate != null && !plate.trim().isEmpty()) {
            boolean success = parkingLot.removeVehicle(plate.trim());
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa xe thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!\n(Xe không tồn tại hoặc đang đỗ trong bãi)", "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRemoveAllConfirmation() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "CẢNH BÁO: Bạn có chắc chắn muốn xóa TOÀN BỘ dữ liệu xe?\nHành động này không thể hoàn tác!",
                "Xác nhận xóa hết",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            parkingLot.resetVehicles();
            loadData();
            JOptionPane.showMessageDialog(this, "Đã xóa toàn bộ dữ liệu xe.");
        }
    }
}
