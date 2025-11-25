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
    private ParkingLot parkingLot;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // --- Fields: UI Components ---
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JButton searchButton;

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

    private void handleSearchAction(ActionEvent e) {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); // Clear previous results
        String searchType = (String) searchTypeCombo.getSelectedItem();
        ArrayList<Ticket> results = new ArrayList<>();

        if ("Mã Vé".equals(searchType)) {
            searchByTicketId(keyword, results);
        } else if ("Biển Số Xe".equals(searchType)) {
            searchByLicensePlate(keyword, results);
        }

        displayResults(results);
    }

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

    private void searchByLicensePlate(String keyword, ArrayList<Ticket> results) {
        // Search all tickets matching the license plate (history)
        for (Ticket t : parkingLot.getTickets()) {
            if (t.getLicensePlate().equalsIgnoreCase(keyword)) {
                results.add(t);
            }
        }
    }

    private void displayResults(ArrayList<Ticket> results) {
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy kết quả nào!", "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
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
}
