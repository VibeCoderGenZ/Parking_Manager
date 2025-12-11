package gui;

import logic.ParkingLot;
import logic.ParkingSpot;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Panel tìm kiếm vị trí đỗ.
 * Chức năng: Tìm chỗ đỗ theo Mã chỗ hoặc Biển số xe đang đỗ.
 */
public class SearchSpotPanel extends JPanel {

    // --- Fields: Logic ---
    /** Đối tượng xử lý logic chính. */
    private ParkingLot parkingLot;

    // --- Fields: UI Components ---
    private JComboBox<String> searchTypeCombo; // Lựa chọn tiêu chí tìm kiếm
    private JTextField searchField; // Ô nhập từ khóa
    private JTable resultTable; // Bảng hiển thị kết quả
    private DefaultTableModel tableModel; // Model dữ liệu cho bảng
    private JButton searchButton; // Nút thực hiện tìm kiếm

    // --- Constructor ---
    public SearchSpotPanel(ParkingLot parkingLot) {
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

        String[] searchTypes = { "Mã Chỗ", "Biển Số Xe" };
        searchTypeCombo = new JComboBox<>(searchTypes);
        topPanel.add(searchTypeCombo);

        searchField = new JTextField(20);
        topPanel.add(searchField);

        searchButton = new JButton("Tìm kiếm");
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initResultTable() {
        String[] columnNames = { "Mã Chỗ", "Loại Xe Cho Phép", "Trạng Thái", "Biển Số Xe" };
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
     * Xử lý sự kiện khi người dùng nhấn nút Tìm kiếm hoặc Enter.
     * 1. Lấy từ khóa và loại tìm kiếm.
     * 2. Gọi hàm tìm kiếm tương ứng.
     * 3. Hiển thị kết quả lên bảng.
     */
    private void handleSearchAction(ActionEvent e) {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0); // Xóa kết quả cũ
        String searchType = (String) searchTypeCombo.getSelectedItem();
        ArrayList<ParkingSpot> results = new ArrayList<>();

        if (keyword.isEmpty()) {
            results.addAll(parkingLot.getSpots());
        } else if ("Mã Chỗ".equals(searchType)) {
            searchBySpotId(keyword, results);
        } else if ("Biển Số Xe".equals(searchType)) {
            searchByLicensePlate(keyword, results);
        }

        displayResults(results);
    }

    /**
     * Tìm kiếm vị trí đỗ theo ID (Mã chỗ).
     * 
     * @param keyword Chuỗi nhập vào (phải là số).
     * @param results Danh sách chứa kết quả tìm được.
     */
    private void searchBySpotId(String keyword, ArrayList<ParkingSpot> results) {
        try {
            int spotID = Integer.parseInt(keyword);
            ParkingSpot s = parkingLot.getSpotBySpotID(spotID);
            if (s != null) {
                results.add(s);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã chỗ phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tìm kiếm vị trí đỗ đang chứa xe có biển số tương ứng.
     * 
     * @param keyword Biển số xe cần tìm.
     * @param results Danh sách chứa kết quả tìm được.
     */
    private void searchByLicensePlate(String keyword, ArrayList<ParkingSpot> results) {
        ParkingSpot s = parkingLot.getSpotByLicensePlate(keyword);
        if (s != null) {
            results.add(s);
        }
    }

    /**
     * Hiển thị danh sách kết quả lên bảng.
     * Nếu không có kết quả -> Thông báo.
     */
    private void displayResults(ArrayList<ParkingSpot> results) {
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

    /** Cho phép bên ngoài kích hoạt lại tìm kiếm. */
    public void refreshResults() {
        handleSearchAction(null);
    }
}
