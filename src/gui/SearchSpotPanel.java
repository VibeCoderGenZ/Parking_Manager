package gui;

import logic.ParkingLot;
import logic.ParkingSpot;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class SearchSpotPanel extends JPanel {
    private ParkingLot parkingLot;
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JTable resultTable;
    private DefaultTableModel tableModel;

    public SearchSpotPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel: Search Controls ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        topPanel.add(new JLabel("Tìm kiếm theo:"));

        String[] searchTypes = { "Mã Chỗ", "Biển Số Xe" };
        searchTypeCombo = new JComboBox<>(searchTypes);
        topPanel.add(searchTypeCombo);

        searchField = new JTextField(20);
        topPanel.add(searchField);

        JButton searchButton = new JButton("Tìm kiếm");
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Results Table ---
        String[] columnNames = { "Mã Chỗ", "Loại Xe Cho Phép", "Trạng Thái", "Biển Số Xe" };
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
        ArrayList<ParkingSpot> results = new ArrayList<>();

        if ("Mã Chỗ".equals(searchType)) {
            try {
                int spotID = Integer.parseInt(keyword);
                ParkingSpot s = parkingLot.getSpotBySpotID(spotID);
                if (s != null) {
                    results.add(s);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã chỗ phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if ("Biển Số Xe".equals(searchType)) {
            ParkingSpot s = parkingLot.getSpotByLicensePlate(keyword);
            if (s != null) {
                results.add(s);
            }
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả nào!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (ParkingSpot s : results) {
                String status = s.isOccupied() ? "Đang có xe" : "Trống";
                String plate = (s.getLicensePlate() == null || s.getLicensePlate().equals("null")) ? "-"
                        : s.getLicensePlate();

                Object[] rowData = {
                        s.getSpotID(),
                        s.getAllowedType(),
                        status,
                        plate
                };
                tableModel.addRow(rowData);
            }
        }
    }
}
