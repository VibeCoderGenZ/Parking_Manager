package gui;

import controller.ParkingController;
import controller.ParkingException;
import dao.ParkingSpotDAO; // Thêm import
import dao.ParkingZoneDAO;
import model.ParkingSpot;
import model.ParkingZone;
import model.VehicleType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Panel quản lý bãi xe với bộ lọc khu vực và bảng dữ liệu chi tiết.
 */
public class ManagementPanel extends JPanel {

    private final ParkingController parkingController;
    private final ParkingZoneDAO zoneDAO;
    private final ParkingSpotDAO spotDAO; // Thêm spotDAO

    private JTable parkingTable;
    private DefaultTableModel tableModel;
    private JPanel zoneSelectionPanel;
    private ParkingZone currentZone;

    public ManagementPanel() {
        this.parkingController = new ParkingController();
        this.zoneDAO = new ParkingZoneDAO();
        this.spotDAO = new ParkingSpotDAO(); // Khởi tạo spotDAO

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Quản Lý Chi Tiết Bãi Đỗ Xe"));

        initUI();
        loadZoneButtons();
    }

    private void initUI() {
        // --- Khu vực chọn Zone (NORTH) ---
        zoneSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JScrollPane zoneScrollPane = new JScrollPane(zoneSelectionPanel);
        zoneScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        zoneScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

        // --- Bảng dữ liệu (CENTER) ---
        String[] columnNames = {"Mã Chỗ Đỗ", "Trạng Thái", "Biển Số Xe"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        parkingTable = new JTable(tableModel);
        parkingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        parkingTable.setRowHeight(25);
        JScrollPane tableScrollPane = new JScrollPane(parkingTable);

        // --- Khu vực công cụ (SOUTH) ---
        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton createZoneButton = new JButton("Tạo Khu Vực Mới");
        JButton addSpotButton = new JButton("Thêm Chỗ Đỗ");
        JButton deleteSpotButton = new JButton("Xóa Chỗ Đã Chọn");
        toolPanel.add(createZoneButton);
        toolPanel.add(addSpotButton);
        toolPanel.add(deleteSpotButton);

        // --- Gắn hành động ---
        createZoneButton.addActionListener(e -> handleCreateNewZone());
        addSpotButton.addActionListener(e -> handleAddSpots());
        deleteSpotButton.addActionListener(e -> handleDeleteSpot());

        // --- Thêm vào panel chính ---
        add(zoneScrollPane, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(toolPanel, BorderLayout.SOUTH);
    }

    private void loadZoneButtons() {
        zoneSelectionPanel.removeAll();
        try {
            List<ParkingZone> allZones = zoneDAO.getAllParkingZones();
            if (allZones.isEmpty()) {
                zoneSelectionPanel.add(new JLabel("Chưa có khu vực nào."));
                tableModel.setRowCount(0);
            } else {
                JButton allButton = new JButton("Tất Cả Khu Vực");
                allButton.addActionListener(e -> loadSpotsForZone(null));
                zoneSelectionPanel.add(allButton);

                for (ParkingZone zone : allZones) {
                    JButton zoneButton = new JButton(zone.getName() + " (" + zone.getAllowedVehicleType() + ")");
                    zoneButton.addActionListener(e -> loadSpotsForZone(zone));
                    zoneSelectionPanel.add(zoneButton);
                }
                loadSpotsForZone(null);
            }
        } catch (SQLException e) {
            showError("Lỗi khi tải danh sách khu vực: " + e.getMessage());
        }
        revalidate();
        repaint();
    }

    private void loadSpotsForZone(ParkingZone zone) {
        this.currentZone = zone;
        tableModel.setRowCount(0);
        try {
            List<ParkingSpot> spots;
            if (zone == null) {
                spots = spotDAO.getAllParkingSpots(); // Sửa ở đây
            } else {
                spots = spotDAO.getParkingSpotsByZone(zone.getId()); // Sửa ở đây
            }
            for (ParkingSpot spot : spots) {
                Object[] rowData = {
                    spot.getId(),
                    spot.isOccupied() ? "Đã có xe" : "Còn trống",
                    spot.getLicensePlate() == null ? "" : spot.getLicensePlate(),
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            showError("Lỗi khi tải danh sách chỗ đỗ: " + e.getMessage());
        }
    }

    private void handleCreateNewZone() {
        JTextField zoneNameField = new JTextField();
        JSpinner numberOfSpotsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        JComboBox<VehicleType> vehicleTypeComboBox = new JComboBox<>(VehicleType.values());
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Tên khu vực mới (ví dụ: C):")); panel.add(zoneNameField);
        panel.add(new JLabel("Loại xe cho phép:")); panel.add(vehicleTypeComboBox);
        panel.add(new JLabel("Số lượng chỗ cần tạo:")); panel.add(numberOfSpotsSpinner);

        int result = JOptionPane.showConfirmDialog(this, panel, "Tạo Khu Vực và Chỗ Đỗ Mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String zoneName = zoneNameField.getText().trim().toUpperCase();
            VehicleType selectedType = (VehicleType) vehicleTypeComboBox.getSelectedItem();
            int numberOfSpots = (Integer) numberOfSpotsSpinner.getValue();
            if (zoneName.isEmpty() || zoneName.contains("-")) {
                showError("Tên khu vực không hợp lệ."); return;
            }
            try {
                parkingController.createNewZoneAndSpots(zoneName, selectedType, numberOfSpots);
                JOptionPane.showMessageDialog(this, "Đã tạo thành công khu vực '" + zoneName + "'.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadZoneButtons();
            } catch (SQLException | ParkingException ex) {
                showError("Lỗi khi tạo khu vực mới: " + ex.getMessage());
            }
        }
    }

    private void handleAddSpots() {
        if (currentZone == null) {
            showError("Vui lòng chọn một khu vực cụ thể để thêm chỗ đỗ.");
            return;
        }
        String input = JOptionPane.showInputDialog(this, "Nhập số lượng chỗ đỗ cần thêm vào khu vực '" + currentZone.getName() + "':", "Thêm Chỗ Đỗ", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int numberOfSpots = Integer.parseInt(input.trim());
                if (numberOfSpots <= 0) {
                    showError("Số lượng phải là số dương.");
                    return;
                }
                parkingController.addSpotsToZone(currentZone, numberOfSpots);
                JOptionPane.showMessageDialog(this, "Đã thêm " + numberOfSpots + " chỗ đỗ mới vào khu vực '" + currentZone.getName() + "'.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadSpotsForZone(currentZone);
            } catch (NumberFormatException ex) {
                showError("Vui lòng nhập một con số hợp lệ.");
            } catch (SQLException ex) {
                showError("Lỗi khi thêm chỗ đỗ mới: " + ex.getMessage());
            }
        }
    }

    private void handleDeleteSpot() {
        int selectedRow = parkingTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Vui lòng chọn một chỗ đỗ trong bảng để xóa.");
            return;
        }
        String spotId = (String) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa vĩnh viễn chỗ đỗ '" + spotId + "' không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                parkingController.deleteParkingSpot(spotId);
                JOptionPane.showMessageDialog(this, "Đã xóa thành công chỗ đỗ '" + spotId + "'.", "Thành Công", JOptionPane.INFORMATION_MESSAGE);
                loadSpotsForZone(currentZone);
            } catch (ParkingException | SQLException ex) {
                showError("Lỗi khi xóa chỗ đỗ: " + ex.getMessage());
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
