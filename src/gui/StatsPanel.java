package gui;

import dao.TicketDAO;
import model.Ticket;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Panel hiển thị lịch sử tất cả các vé đã ra/vào bãi.
 */
public class StatsPanel extends JPanel {

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private final TicketDAO ticketDAO;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
    private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public StatsPanel() {
        this.ticketDAO = new TicketDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Lịch Sử Ra/Vào Bãi"));

        initUI();
        loadTicketHistory();
    }

    private void initUI() {
        // --- Tạo bảng dữ liệu ---
        String[] columnNames = {"ID Vé", "Biển Số", "Thời Gian Vào", "Thời Gian Ra", "Loại Vé", "Giá Tiền"};

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
        // Set độ rộng cho các cột
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Time In
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Time Out

        JScrollPane scrollPane = new JScrollPane(historyTable);

        // --- Tạo panel chứa nút bấm ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Làm Mới Dữ Liệu");
        topPanel.add(refreshButton);

        // --- Gắn hành động ---
        refreshButton.addActionListener(e -> loadTicketHistory());

        // --- Thêm các thành phần vào panel chính ---
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Tải toàn bộ lịch sử vé từ database và cập nhật vào bảng.
     */
    private void loadTicketHistory() {
        try {
            // Gọi DAO để lấy tất cả các vé
            List<Ticket> tickets = ticketDAO.getAllTickets();

            // Xóa dữ liệu cũ
            tableModel.setRowCount(0);

            // Đổ dữ liệu mới vào bảng
            for (Ticket ticket : tickets) {
                String entryTime = ticket.getEntryTime().format(formatter);
                String exitTime = (ticket.getExitTime() != null) ? ticket.getExitTime().format(formatter) : "Đang trong bãi";
                String price = currencyFormatter.format(ticket.getPrice());

                Object[] rowData = {
                    ticket.getId(),
                    ticket.getLicensePlate(),
                    entryTime,
                    exitTime,
                    ticket.getTicketType().name(),
                    price
                };
                tableModel.addRow(rowData);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải lịch sử vé từ database.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
