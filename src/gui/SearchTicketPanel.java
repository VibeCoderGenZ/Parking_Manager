package gui;

import logic.ParkingLot;
import logic.Ticket;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SearchTicketPanel extends JPanel {
    private ParkingLot parkingLot;
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public SearchTicketPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Top Panel: Search Controls ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        topPanel.add(new JLabel("Tìm kiếm theo:"));

        String[] searchTypes = { "Mã Vé", "Biển Số Xe" };
        searchTypeCombo = new JComboBox<>(searchTypes);
        topPanel.add(searchTypeCombo);

        searchField = new JTextField(20);
        topPanel.add(searchField);

        JButton searchButton = new JButton("Tìm kiếm");
        topPanel.add(searchButton);

        add(topPanel, BorderLayout.NORTH);

        // --- Center Panel: Results Table ---
        String[] columnNames = { "Mã Vé", "Mã Chỗ", "Biển Số", "Giờ Vào", "Giờ Ra", "Trạng Thái" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
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

        // Allow pressing Enter in the text field to search
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
        ArrayList<Ticket> results = new ArrayList<>();

        if ("Mã Vé".equals(searchType)) {
            try {
                int ticketID = Integer.parseInt(keyword);
                Ticket t = parkingLot.getTicketByTicketID(ticketID);
                if (t != null) {
                    results.add(t);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Mã vé phải là số!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else if ("Biển Số Xe".equals(searchType)) {
            // Search all tickets matching the license plate (history)
            for (Ticket t : parkingLot.getTickets()) {
                if (t.getLicensePlate().equalsIgnoreCase(keyword)) {
                    results.add(t);
                }
            }
        }

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
