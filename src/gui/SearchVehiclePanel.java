package gui;

import logic.ParkingLot;
import logic.Vehicle;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Panel tìm kiếm xe.
 * Chức năng: Tìm xe theo Biển số hoặc Tên chủ xe.
 */
public class SearchVehiclePanel extends JPanel {

    // --- Fields: Logic ---
    private ParkingLot parkingLot;

    // --- Fields: UI Components ---
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton searchButton;

    // --- Constructor ---
    public SearchVehiclePanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        initComponents();
    }

    // --- Initialization Methods ---

    private void initComponents() {
        // 1. Top Panel: Search Controls
        initSearchControls();

        // 2. Center Panel: Results Table
        initResultTable();

        // 3. Event Handling
        initEvents();
    }

    private void initSearchControls() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        topPanel.add(new JLabel("Tìm kiếm theo:"));

        String[] searchTypes = { "Biển Số Xe", "Tên Chủ Xe" };
        searchTypeCombo = new JComboBox<>(searchTypes);
        topPanel.add(searchTypeCombo);

        searchField = new JTextField(20);
        topPanel.add(searchField);

        searchButton = new JButton("Tìm kiếm");
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initResultTable() {
        String[] columnNames = { "Biển Số", "Loại Xe", "Chủ Xe", "Số Điện Thoại" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultTable = new JTable(tableModel);
        resultTable.setRowHeight(25);
        resultTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(resultTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initEvents() {
        searchButton.addActionListener(this::handleSearchAction);
        searchField.addActionListener(this::handleSearchAction);
    }

    // --- Business Logic ---

    private void handleSearchAction(ActionEvent e) {
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
            searchByLicensePlate(keyword, results);
        } else if ("Tên Chủ Xe".equals(searchType)) {
            searchByOwnerName(keyword, results);
        }

        displayResults(results);
    }

    private void searchByLicensePlate(String keyword, ArrayList<Vehicle> results) {
        Vehicle v = parkingLot.getVehicleByLicensePlate(keyword);
        if (v != null) {
            results.add(v);
        }
    }

    private void searchByOwnerName(String keyword, ArrayList<Vehicle> results) {
        results.addAll(parkingLot.getVehicleByOwnerName(keyword));
    }

    private void displayResults(ArrayList<Vehicle> results) {
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