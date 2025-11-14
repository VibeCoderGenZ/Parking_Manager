package gui;

import dao.VehicleDAO;
import model.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class CustomerInfoPanel extends JPanel {

    private JTable customerTable;
    private DefaultTableModel tableModel;
    private final VehicleDAO vehicleDAO;
    private JTextField searchField; // Ô tìm kiếm

    public CustomerInfoPanel() {
        this.vehicleDAO = new VehicleDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Thông Tin Khách Hàng"));

        initUI();
        loadCustomerData();
    }

    private void initUI() {
        // Panel cho các nút và ô tìm kiếm
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        topPanel.add(new JLabel("Tìm theo tên chủ xe:"));
        searchField = new JTextField(20);
        topPanel.add(searchField);

        JButton searchButton = new JButton("Tìm Kiếm");
        topPanel.add(searchButton);

        JButton refreshButton = new JButton("Làm Mới Dữ Liệu");
        topPanel.add(refreshButton);

        // Action Listeners
        searchButton.addActionListener(e -> {
            String ownerName = searchField.getText().trim();
            if (!ownerName.isEmpty()) {
                searchCustomers(ownerName);
            } else {
                loadCustomerData(); // Tải lại toàn bộ nếu ô tìm kiếm trống
            }
        });

        refreshButton.addActionListener(e -> {
            searchField.setText(""); // Xóa ô tìm kiếm
            loadCustomerData();
        });

        // Bảng hiển thị dữ liệu
        String[] columnNames = {"Biển Số Xe", "Tên Chủ Xe", "Số Điện Thoại", "Loại Xe"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customerTable = new JTable(tableModel);
        customerTable.setFillsViewportHeight(true);
        customerTable.setRowHeight(25);
        customerTable.setFont(new Font("Arial", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(customerTable);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadCustomerData() {
        try {
            List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
            updateTable(vehicles);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu khách hàng.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchCustomers(String ownerName) {
        try {
            // Giả định VehicleDAO có phương thức getVehiclesByOwnerName
            List<Vehicle> vehicles = vehicleDAO.getVehiclesByOwnerName(ownerName);
            updateTable(vehicles);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm khách hàng.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTable(List<Vehicle> vehicles) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        for (Vehicle vehicle : vehicles) {
            Object[] rowData = {
                vehicle.getLicensePlate(),
                vehicle.getOwnerName(),
                vehicle.getOwnerPhone(),
                vehicle.getType().name()
            };
            tableModel.addRow(rowData);
        }
    }
}
