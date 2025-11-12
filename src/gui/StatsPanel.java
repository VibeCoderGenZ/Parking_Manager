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
    private final ParkingController parkingController; // Thêm ParkingController
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");

    public StatsPanel() {
        this.ticketDAO = new TicketDAO();
        this.parkingController = new ParkingController(); // Khởi tạo ParkingController

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Lịch Sử Ra/Vào Bãi"));

        initUI();
        loadTicketHistory();
    }

    private void initUI() {
        // Cập nhật columnNames: bỏ "Loại Vé", "Giá Tiền", thêm "Loại Xe"
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
        JButton refreshButton = new JButton("Làm Mới Dữ Liệu");
        topPanel.add(refreshButton);

        refreshButton.addActionListener(e -> loadTicketHistory());

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadTicketHistory() {
        try {
            List<Ticket> tickets = ticketDAO.getAllTickets();

            tableModel.setRowCount(0);

            for (Ticket ticket : tickets) {
                String entryTime = ticket.getEntryTime().format(formatter);
                String exitTime = (ticket.getExitTime() != null) ? ticket.getExitTime().format(formatter) : "Đang trong bãi";
                
                // Lấy thông tin loại xe
                String vehicleType = "N/A"; // Mặc định nếu không tìm thấy
                Vehicle vehicle = parkingController.getVehicle(ticket.getLicensePlate());
                if (vehicle != null) {
                    vehicleType = vehicle.getType().name();
                }

                Object[] rowData = {
                    ticket.getId(),
                    ticket.getLicensePlate(),
                    entryTime,
                    exitTime,
                    vehicleType // Thêm loại xe vào đây
                };
                tableModel.addRow(rowData);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lịch sử vé từ database.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
