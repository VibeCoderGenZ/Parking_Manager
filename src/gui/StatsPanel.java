package gui;

import controller.ParkingController;
import dao.TicketDAO;
import model.Ticket;
import model.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StatsPanel extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private final TicketDAO ticketDAO;
    private final ParkingController parkingController;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
    private JTextField searchField;

    public StatsPanel() {
        this.ticketDAO = new TicketDAO();
        this.parkingController = new ParkingController();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Lịch Sử Ra/Vào Bãi"));

        initUI();
        loadTicketHistory();
    }

    private void initUI() {
        String[] columnNames = {"ID Vé", "Biển Số", "Thời Gian Vào", "Thời Gian Ra", "Loại Xe"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setFillsViewportHeight(true);
        historyTable.setRowHeight(25);
        historyTable.setFont(new Font("Arial", Font.PLAIN, 14));
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(historyTable);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        topPanel.add(new JLabel("Tìm theo biển số:"));
        searchField = new JTextField(15);
        topPanel.add(searchField);

        JButton searchButton = new JButton("Tìm Kiếm");
        topPanel.add(searchButton);

        JButton refreshButton = new JButton("Làm Mới Dữ Liệu");
        topPanel.add(refreshButton);

        searchButton.addActionListener(e -> {
            String licensePlate = searchField.getText().trim();
            if (!licensePlate.isEmpty()) {
                searchTicketHistory(licensePlate);
            } else {
                loadTicketHistory();
            }
        });

        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadTicketHistory();
        });

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTicketHistory() {
        try {
            List<Ticket> tickets = ticketDAO.getAllTickets();
            updateTable(tickets);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lịch sử vé từ database.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void searchTicketHistory(String licensePlate) {
        try {
            List<Ticket> tickets = ticketDAO.getTicketsByLicensePlate(licensePlate);
            updateTable(tickets);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm lịch sử vé.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateTable(List<Ticket> tickets) {
        tableModel.setRowCount(0);

        for (Ticket ticket : tickets) {
            String entryTime = ticket.getEntryTime().format(formatter);
            String exitTime = (ticket.getExitTime() != null) ? ticket.getExitTime().format(formatter) : "Đang trong bãi";
            
            String vehicleType = "N/A";
            try {
                Vehicle vehicle = parkingController.getVehicle(ticket.getLicensePlate());
                if (vehicle != null) {
                    vehicleType = vehicle.getType().name();
                }
            } catch (SQLException e) {
                System.err.println("Lỗi khi lấy thông tin xe cho biển số: " + ticket.getLicensePlate());
                e.printStackTrace();
            }

            Object[] rowData = {
                ticket.getId(),
                ticket.getLicensePlate(),
                entryTime,
                exitTime,
                vehicleType
            };
            tableModel.addRow(rowData);
        }
    }
}
