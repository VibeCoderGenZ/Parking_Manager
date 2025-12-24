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
    /** Đối tượng xử lý logic chính. */
    private ParkingLot parkingLot;
    /**
     * Trạng thái hiển thị hiện tại: true = Vé đang hoạt động, false = Vé lịch sử.
     */
    private boolean isShowingActive = true;

    // --- Fields: UI Components ---
    private JTable table; // Bảng hiển thị danh sách vé
    private DefaultTableModel tableModel; // Model dữ liệu cho bảng
    private JButton btnActiveTickets; // Nút chuyển sang xem vé đang hoạt động
    private JButton btnUsedTickets; // Nút chuyển sang xem vé đã trả

    // --- Constructor ---
    public TicketManagementPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;

        setLayout(new BorderLayout());
        initComponents();

        // Mặc định hiển thị vé đang hoạt động khi mới mở
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
     * Tải dữ liệu vé lên bảng hiển thị.
     * 
     * @param isActive true: tải danh sách vé đang hoạt động (xe đang trong bãi).
     *                 false: tải danh sách vé đã trả (lịch sử ra vào).
     */
    private void loadData(boolean isActive) {
        this.isShowingActive = isActive;
        tableModel.setRowCount(0); // Xóa dữ liệu cũ trên bảng

        ArrayList<Ticket> list;
        if (isActive) {
            list = parkingLot.getActiveTicket();
            updateButtonState(true); // Cập nhật giao diện nút bấm
        } else {
            list = parkingLot.getUsedTicket();
            updateButtonState(false); // Cập nhật giao diện nút bấm
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

    /**
     * Cập nhật trạng thái (enable/disable, font style) của các nút bấm
     * để phản ánh chế độ xem hiện tại.
     */
    private void updateButtonState(boolean isActiveMode) {
        btnActiveTickets.setEnabled(!isActiveMode); // Nếu đang xem Active thì disable nút Active
        btnUsedTickets.setEnabled(isActiveMode); // Ngược lại

        // Highlight nút đang chọn (In đậm text)
        if (isActiveMode) {
            btnActiveTickets.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnUsedTickets.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        } else {
            btnActiveTickets.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btnUsedTickets.setFont(new Font("Segoe UI", Font.BOLD, 14));
        }
    }

    /**
     * Refresh lại dữ liệu hiện tại (dùng khi chuyển tab quay lại màn hình này).
     * Giữ nguyên chế độ xem (Active/Used) đang chọn.
     */
    public void refresh() {
        loadData(isShowingActive);
    }
}
