package gui;

import logic.ParkingLot;
import logic.Ticket;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Panel tìm kiếm vé.
 * Chức năng: Tìm vé theo Mã vé hoặc Biển số xe.
 */
public class SearchTicketPanel extends JPanel {

    // --- Fields: Logic ---
    /** Đối tượng xử lý logic chính. */
    private ParkingLot parkingLot;
    /** Định dạng ngày giờ hiển thị. */
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // --- Fields: UI Components ---
    private JComboBox<String> searchTypeCombo; // Lựa chọn tiêu chí tìm kiếm
    private JTextField searchField; // Ô nhập từ khóa
    private JTable resultTable; // Bảng hiển thị kết quả
    private DefaultTableModel tableModel; // Model dữ liệu
    private JButton searchButton; // Nút tìm kiếm

    // --- Constructor ---
    public SearchTicketPanel(ParkingLot parkingLot) {
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

        String[] searchTypes = { "Mã Vé", "Biển Số Xe" };
        searchTypeCombo = new JComboBox<>(searchTypes);
        topPanel.add(searchTypeCombo);

        searchField = new JTextField(20);
        topPanel.add(searchField);

        searchButton = new JButton("Tìm kiếm");
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private void initResultTable() {
        String[] columnNames = { "Mã Vé", "Mã Chỗ", "Biển Số", "Giờ Vào", "Giờ Ra", "Trạng Thái" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
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
        searchField.addActionListener(this::handleSearchAction); // Allow pressing Enter
    }

    // --- Business Logic ---

    /**
     * Xử lý sự kiện tìm kiếm vé.
     * Hỗ trợ tìm theo Mã vé (chính xác) hoặc Biển số xe (lịch sử vé của xe đó).
     */
    private void handleSearchAction(ActionEvent e) {
        String keyword = searchField.getText().trim();
        tableModel.setRowCount(0); // Xóa kết quả cũ

        if (keyword.isEmpty()) {
            // Khi không nhập gì, hiển thị toàn bộ vé hiện có
            displayResults(new ArrayList<>(parkingLot.getTickets()));
            return;
        }

        String searchType = (String) searchTypeCombo.getSelectedItem();
        ArrayList<Ticket> results = new ArrayList<>();

        if ("Mã Vé".equals(searchType)) {
            searchByTicketId(keyword, results);
        } else if ("Biển Số Xe".equals(searchType)) {
            searchByLicensePlate(keyword, results);
        }

        displayResults(results);
    }

    /**
     * Cho phép các thành phần bên ngoài kích hoạt lại tìm kiếm để làm mới hiển thị.
     */
    public void refreshResults() {
        handleSearchAction(null);
    }

    /**
     * Tìm vé theo ID.
     * 
     * @param keyword Mã vé (số nguyên).
     * @param results Danh sách kết quả.
     */
    private void searchByTicketId(String keyword, ArrayList<Ticket> results) {
        try {
            int ticketID = Integer.parseInt(keyword);
            Ticket t = parkingLot.getTicketByTicketID(ticketID);
            if (t != null) {
                results.add(t);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mã vé phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Tìm tất cả vé liên quan đến một biển số xe (bao gồm cả vé cũ).
     * 
     * @param keyword Biển số xe.
     * @param results Danh sách kết quả.
     */
    private void searchByLicensePlate(String keyword, ArrayList<Ticket> results) {
        // Duyệt qua toàn bộ danh sách vé để tìm các vé khớp biển số
        for (Ticket t : parkingLot.getTickets()) {
            if (t.getLicensePlate().equalsIgnoreCase(keyword)) {
                results.add(t);
            }
        }
    }

    /**
     * Hiển thị kết quả tìm kiếm lên bảng.
     */
    private void displayResults(ArrayList<Ticket> results) {
        for (Ticket t : results) {
            String status = (t.getExitTime() == null) ? "Đang đỗ" : "Đã rời";
            String exitTimeStr = (t.getExitTime() == null) ? "-" : t.getExitTime().format(formatter);

            Object[] rowData = {
                    t.getTicketID(),
                    t.getSpotID(),
                    t.getLicensePlate(),
                    t.getEntryTime().format(formatter),
                    exitTimeStr,
                    status
            };
            tableModel.addRow(rowData);
        }
    }
}
