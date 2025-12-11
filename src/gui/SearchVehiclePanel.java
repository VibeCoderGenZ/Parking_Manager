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
    /** Đối tượng xử lý logic chính. */
    private ParkingLot parkingLot;

    // --- Fields: UI Components ---
    private JComboBox<String> searchTypeCombo; // Lựa chọn tiêu chí tìm kiếm
    private JTextField searchField; // Ô nhập từ khóa
    private JTable resultTable; // Bảng hiển thị kết quả
    private DefaultTableModel tableModel; // Model dữ liệu
    private JButton searchButton; // Nút tìm kiếm

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

    /**
     * Xử lý sự kiện tìm kiếm xe.
     * Hỗ trợ tìm theo Biển số hoặc Tên chủ xe.
     */
    private void handleSearchAction(ActionEvent e) {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0); // Xóa kết quả cũ
        String searchType = (String) searchTypeCombo.getSelectedItem();
        ArrayList<Vehicle> results = new ArrayList<>();

        if (keyword.isEmpty()) {
            results.addAll(parkingLot.getVehicles());
        } else if ("Biển Số Xe".equals(searchType)) {
            searchByLicensePlate(keyword, results);
        } else if ("Tên Chủ Xe".equals(searchType)) {
            searchByOwnerName(keyword, results);
        }

        displayResults(results);
    }

    /**
     * Tìm xe theo biển số (chính xác).
     */
    private void searchByLicensePlate(String keyword, ArrayList<Vehicle> results) {
        Vehicle v = parkingLot.getVehicleByLicensePlate(keyword);
        if (v != null) {
            results.add(v);
        }
    }

    /**
     * Tìm xe theo tên chủ xe (gần đúng hoặc chính xác tùy logic cài đặt trong
     * ParkingLot).
     */
    private void searchByOwnerName(String keyword, ArrayList<Vehicle> results) {
        results.addAll(parkingLot.getVehicleByOwnerName(keyword));
    }

    /**
     * Hiển thị kết quả tìm kiếm lên bảng.
     */
    private void displayResults(ArrayList<Vehicle> results) {
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

    /** Cho phép các panel ngoài tái sử dụng tìm kiếm mặc định. */
    public void refreshResults() {
        handleSearchAction(null);
    }
}
