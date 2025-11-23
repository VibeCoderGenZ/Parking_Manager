package gui;

import logic.ParkingLot;
import logic.Vehicle;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SearchVehiclePanel extends JPanel {
    private ParkingLot parkingLot;
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public SearchVehiclePanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel: Search Controls ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        topPanel.add(new JLabel("Tìm kiếm theo:"));

        String[] searchTypes = { "Biển Số Xe", "Tên Chủ Xe" };
        searchTypeCombo = new JComboBox<>(searchTypes);
        topPanel.add(searchTypeCombo);

        searchField = new JTextField(20);
        topPanel.add(searchField);

        JButton searchButton = new JButton("Tìm kiếm");
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Results Table ---
        String[] columnNames = { "Biển Số", "Loại Xe", "Chủ Xe", "Số Điện Thoại" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);

        // --- Event Handling ---
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); // Clear previous results
        String searchType = (String) searchTypeCombo.getSelectedItem();
        ArrayList<Vehicle> results = new ArrayList<>();

        if ("Biển Số Xe".equals(searchType)) {
            Vehicle v = parkingLot.getVehicleByLicensePlate(keyword);
            if (v != null) {
                results.add(v);
            }
        } else if ("Tên Chủ Xe".equals(searchType)) {
            results = parkingLot.getVehicleByOwnerName(keyword);
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả nào!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Vehicle v : results) {
                Object[] rowData = {
                        v.getLicensePlate(),
                        v.getType(),
                        v.getOwnerName(),
                        v.getOwnerPhone()
                };
                tableModel.addRow(rowData);
            }
        }
    }
}
