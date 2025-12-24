package gui;

import logic.ParkingLot;
import logic.Vehicle;
import logic.VehicleType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * Panel quản lý danh sách xe (Đăng ký xe).
 * Chức năng: Xem danh sách, Thêm xe mới, Xóa xe, Xóa tất cả.
 */
public class VehicleManagementPanel extends JPanel {

    // --- Fields: Logic ---
    /** Đối tượng xử lý logic chính. */
    private ParkingLot parkingLot;

    // --- Fields: UI Components ---
    private JTable table; // Bảng hiển thị danh sách xe
    private DefaultTableModel tableModel; // Model dữ liệu
    private JButton btnAdd; // Nút thêm xe
    private JButton btnRemove; // Nút xóa xe
    private JButton btnRemoveAll; // Nút xóa tất cả xe

    // --- Constructor ---
    public VehicleManagementPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;

        setLayout(new BorderLayout());
        initComponents();
        loadData(); // Tải dữ liệu ngay khi khởi tạo
    }

    // --- Initialization Methods ---

    private void initComponents() {
        // 1. Khởi tạo bảng dữ liệu (Center)
        initTable();

        // 2. Khởi tạo panel điều khiển (Bottom)
        initControlPanel();
    }

    private void initTable() {
        String[] columnNames = { "Biển Số", "Loại Xe", "Chủ Xe", "Số Điện Thoại" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initControlPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnAdd = createStyledButton("Thêm Xe", new Color(0, 102, 204));
        btnRemove = createStyledButton("Xóa Xe", new Color(204, 0, 0));
        btnRemoveAll = createStyledButton("Xóa Tất Cả", new Color(153, 0, 0));

        bottomPanel.add(btnAdd);
        bottomPanel.add(btnRemove);
        bottomPanel.add(btnRemoveAll);

        add(bottomPanel, BorderLayout.SOUTH);

        // Gắn sự kiện
        btnAdd.addActionListener(e -> showAddVehicleDialog());
        btnRemove.addActionListener(e -> showRemoveVehicleDialog());
        btnRemoveAll.addActionListener(e -> showRemoveAllConfirmation());
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 35));
        return btn;
    }

    // --- Business Logic ---

    /**
     * Tải lại toàn bộ dữ liệu xe từ logic lên bảng hiển thị.
     * Được gọi sau khi thêm, xóa hoặc khi cần làm mới.
     */
    public void loadData() {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
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

    /**
     * Hiển thị Dialog (Cửa sổ phụ) để nhập thông tin xe mới.
     * Bao gồm: Biển số, Loại xe, Chủ xe, SĐT.
     */
    private void showAddVehicleDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm Xe Mới", true);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtPlate = new JTextField(15);
        JComboBox<VehicleType> cbType = new JComboBox<>(VehicleType.values());
        JTextField txtOwner = new JTextField(15);
        JTextField txtPhone = new JTextField(15);

        // Helper để thêm row vào dialog
        addFormRow(dialog, gbc, 0, "Biển số:", txtPlate);
        addFormRow(dialog, gbc, 1, "Loại xe:", cbType);
        addFormRow(dialog, gbc, 2, "Chủ xe:", txtOwner);
        addFormRow(dialog, gbc, 3, "SĐT:", txtPhone);

        // Nút xác nhận
        JButton btnConfirm = new JButton("Thêm");
        btnConfirm.setBackground(new Color(0, 102, 204));
        btnConfirm.setForeground(Color.WHITE);

        btnConfirm.addActionListener(e -> {
            if (processAddVehicle(txtPlate, cbType, txtOwner, txtPhone)) {
                dialog.dispose(); // Đóng dialog nếu thêm thành công
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

    private void addFormRow(JDialog dialog, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        dialog.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        dialog.add(comp, gbc);
    }

    /**
     * Xử lý logic thêm xe khi người dùng nhấn nút xác nhận trên Dialog.
     * 
     * @return true nếu thêm thành công, false nếu thất bại.
     */
    private boolean processAddVehicle(JTextField txtPlate, JComboBox<VehicleType> cbType, JTextField txtOwner,
            JTextField txtPhone) {
        String plate = txtPlate.getText().trim();
        VehicleType type = (VehicleType) cbType.getSelectedItem();
        String owner = txtOwner.getText().trim();
        String phone = txtPhone.getText().trim();

        if (plate.isEmpty() || owner.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        boolean success = parkingLot.addVehicle(plate, type, owner, phone);
        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm xe thành công!");
            loadData(); // Refresh bảng
            return true;
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!\n(Có thể biển số đã tồn tại)", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Hiển thị Dialog yêu cầu nhập biển số xe cần xóa.
     * Nếu người dùng đang chọn 1 dòng trên bảng, tự động điền biển số đó vào.
     */
    private void showRemoveVehicleDialog() {
        // Nếu có dòng đang chọn thì lấy biển số đó luôn
        String initialValue = "";
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            initialValue = (String) tableModel.getValueAt(selectedRow, 0);
        }

        String plate = (String) JOptionPane.showInputDialog(
                this,
                "Nhập biển số xe cần xóa:",
                "Xóa Xe",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                initialValue);

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

    /**
     * Hiển thị cảnh báo và thực hiện xóa toàn bộ dữ liệu xe.
     * Cần xác nhận kỹ lưỡng.
     */
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
