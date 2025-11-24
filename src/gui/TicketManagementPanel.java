package gui;

import logic.ParkingLot;
import logic.Ticket;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Panel quản lý vé (Lịch sử vé).
 * Chức năng: Xem danh sách vé đang hoạt động và vé đã sử dụng (lịch sử).
 */
public class TicketManagementPanel extends JPanel {

    // --- Fields: Logic ---
    private ParkingLot parkingLot;
    private boolean isShowingActive = true; // Trạng thái hiện tại

    // --- Fields: UI Components ---
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnActiveTickets;
    private JButton btnUsedTickets;

    // --- Constructor ---
    public TicketManagementPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;

        setLayout(new BorderLayout());
        initComponents();

        // Mặc định hiển thị vé đang hoạt động
        loadData(true);
    }

    // --- Initialization Methods ---

    private void initComponents() {
        // 1. Khởi tạo bảng dữ liệu (Center)
        initTable();

        // 2. Khởi tạo panel điều khiển (Bottom)
        initControlPanel();
    }

    private void initTable() {
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initControlPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        btnActiveTickets = new JButton("Vé Đang Hoạt Động");
        btnUsedTickets = new JButton("Vé Đã Trả / Đã Thu");

        styleButton(btnActiveTickets);
        styleButton(btnUsedTickets);

        bottomPanel.add(btnActiveTickets);
        bottomPanel.add(btnUsedTickets);

        add(bottomPanel, BorderLayout.SOUTH);

        // Gắn sự kiện
        btnActiveTickets.addActionListener(e -> loadData(true));
        btnUsedTickets.addActionListener(e -> loadData(false));
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setPreferredSize(new Dimension(180, 35));
        btn.setFocusPainted(false);
    }

    // --- Business Logic ---

    /**
     * Tải dữ liệu vé lên bảng.
     * 
     * @param isActive true: tải vé đang hoạt động, false: tải vé đã trả.
     */
    private void loadData(boolean isActive) {
        this.isShowingActive = isActive;
        tableModel.setRowCount(0); // Xóa dữ liệu cũ

        ArrayList<Ticket> list;
        if (isActive) {
            list = parkingLot.getActiveTicket();
            updateButtonState(true);
        } else {
            list = parkingLot.getUsedTicket();
            updateButtonState(false);
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

    private void updateButtonState(boolean isActiveMode) {
        btnActiveTickets.setEnabled(!isActiveMode);
        btnUsedTickets.setEnabled(isActiveMode);

        // Highlight nút đang chọn (Optional)
        if (isActiveMode) {
            btnActiveTickets.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnUsedTickets.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        } else {
            btnActiveTickets.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnUsedTickets.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
    }

    /**
     * Refresh lại dữ liệu hiện tại (dùng khi chuyển tab quay lại).
     */
    public void refresh() {
        loadData(isShowingActive);
    }
}