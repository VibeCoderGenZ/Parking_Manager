package gui;

import logic.ParkingLot;
import logic.Ticket;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TicketManagementPanel extends JPanel {

    private ParkingLot parkingLot;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnActiveTickets;
    private JButton btnUsedTickets;

    public TicketManagementPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout());

        initComponents();
        // Mặc định hiển thị vé đang hoạt động
        loadData(true);
    }

    private void initComponents() {
        // 1. Bảng dữ liệu (Center)
        String[] columnNames = { "Mã Vé", "Vị Trí Đỗ", "Biển Số Xe", "Giờ Vào", "Giờ Ra" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa trực tiếp trên bảng
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 2. Panel nút bấm (Bottom)
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnActiveTickets = new JButton("Vé Đang Hoạt Động");
        btnUsedTickets = new JButton("Vé Đã Trả / Đã Thu");

        // Style cho nút
        styleButton(btnActiveTickets);
        styleButton(btnUsedTickets);

        bottomPanel.add(btnActiveTickets);
        bottomPanel.add(btnUsedTickets);

        add(bottomPanel, BorderLayout.SOUTH);

        // 3. Sự kiện
        btnActiveTickets.addActionListener(e -> loadData(true));
        btnUsedTickets.addActionListener(e -> loadData(false));
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(180, 35));
    }

    private void loadData(boolean isActive) {
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        ArrayList<Ticket> list;
        if (isActive) {
            list = parkingLot.getActiveTicket();
            btnActiveTickets.setEnabled(false); // Disable nút đang chọn
            btnUsedTickets.setEnabled(true);
        } else {
            list = parkingLot.getUsedTicket();
            btnActiveTickets.setEnabled(true);
            btnUsedTickets.setEnabled(false);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for (Ticket t : list) {
            String entry = t.getEntryTime() != null ? t.getEntryTime().format(formatter) : "";
            String exit = t.getExitTime() != null ? t.getExitTime().format(formatter) : "---";

            Object[] row = {
                    t.getTicketID(),
                    t.getSpotID(),
                    t.getLicensePlate(),
                    entry,
                    exit
            };
            tableModel.addRow(row);
        }
    }

    // Hàm public để refresh dữ liệu khi chuyển tab
    public void refresh() {
        // Refresh lại tab đang active (dựa vào trạng thái nút)
        loadData(!btnActiveTickets.isEnabled());
    }
}
