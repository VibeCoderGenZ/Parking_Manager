package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import logic.ParkingLot;

/**
 * MainFrame - Cửa sổ chính của ứng dụng.
 * Chức năng:
 * 1. Khởi tạo và giữ instance duy nhất của ParkingLot (Logic).
 * 2. Quản lý điều hướng (Navigation) giữa các chức năng.
 * 3. Xử lý vòng đời ứng dụng (Lưu dữ liệu khi tắt).
 */
public class MainFrame extends JFrame {

    // --- Fields: Logic ---
    private ParkingLot parkingLot;

    // --- Fields: UI Components ---
    private JPanel navPanel; // Panel bên trái chứa nút bấm
    private JPanel contentPanel; // Panel ở giữa hiển thị nội dung
    private CardLayout cardLayout; // Layout quản lý chuyển đổi màn hình

    // --- Fields: Functional Panels ---
    private VehicleManagementPanel vehiclePanel;
    private TicketManagementPanel ticketPanel;
    private SpotManagementPanel spotPanel;
    private SearchPanel searchPanel;

    // --- Constructor ---
    public MainFrame() {
        // 1. Khởi tạo Logic
        initLogic();

        // 2. Cấu hình cửa sổ chính
        initFrameSettings();

        // 3. Khởi tạo giao diện
        initComponents();
    }

    // --- Initialization Methods ---

    /**
     * Khởi tạo logic nghiệp vụ (ParkingLot) và tải dữ liệu.
     */
    private void initLogic() {
        try {
            this.parkingLot = new ParkingLot();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc dữ liệu: " + e.getMessage(), "Lỗi Khởi Động",
                    JOptionPane.ERROR_MESSAGE);
            // Có thể cân nhắc thoát app nếu dữ liệu lỗi nghiêm trọng
        }
    }

    /**
     * Cấu hình các thuộc tính cơ bản của JFrame.
     */
    private void initFrameSettings() {
        setTitle("Parking Manager - Hệ Thống Quản Lý Bãi Đỗ Xe");
        setSize(1280, 720); // Kích thước mặc định lớn hơn chút cho thoải mái
        setLocationRelativeTo(null); // Căn giữa màn hình
        setLayout(new BorderLayout());

        // Xử lý sự kiện đóng cửa sổ để lưu dữ liệu
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performExit();
            }
        });
    }

    /**
     * Khởi tạo các thành phần giao diện (Navigation + Content).
     */
    private void initComponents() {
        // 1. Navigation Panel (Bên trái)
        initNavPanel();

        // 2. Content Panel (Ở giữa)
        initContentPanel();

        // Thêm vào Frame
        add(navPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Tạo panel điều hướng bên trái.
     */
    private void initNavPanel() {
        navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(7, 1, 10, 10)); // 7 hàng, 1 cột, khoảng cách 10px
        navPanel.setPreferredSize(new Dimension(200, 0)); // Rộng 200px
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navPanel.setBackground(new Color(240, 240, 240)); // Màu nền nhẹ

        // Tạo các nút chức năng
        createNavButton("1. Tạo Vé / Thu Vé", "FUNCTION_1");
        createNavButton("2. Quản Lý Vé", "FUNCTION_2");
        navPanel.add(new JLabel()); // Khoảng trống (Vị trí 3)
        createNavButton("4. Quản Lý Xe", "FUNCTION_4");
        createNavButton("5. Quản Lý Bãi Xe", "FUNCTION_5");
        navPanel.add(new JLabel()); // Khoảng trống (Vị trí 6)
        createNavButton("7. Tìm Kiếm", "FUNCTION_7");
    }

    /**
     * Helper để tạo nút điều hướng và gắn sự kiện.
     */
    private void createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btn.addActionListener(e -> {
            // Refresh dữ liệu cho các panel cần thiết trước khi hiển thị
            refreshPanelData(cardName);
            cardLayout.show(contentPanel, cardName);
        });

        navPanel.add(btn);
    }

    /**
     * Tạo panel nội dung chính (CardLayout).
     */
    private void initContentPanel() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // --- Khởi tạo các màn hình chức năng ---

        // Màn hình 4: Quản lý xe (Cần tạo trước để truyền vào CreateTicketPanel)
        vehiclePanel = new VehicleManagementPanel(parkingLot);
        contentPanel.add(vehiclePanel, "FUNCTION_4");

        // Màn hình 1: Tạo vé / Thu vé (TabbedPane)
        JTabbedPane ticketTabPane = new JTabbedPane();
        ticketTabPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Tab 1: Tạo vé (Có callback chuyển sang màn hình thêm xe)
        CreateTicketPanel createTicketPanel = new CreateTicketPanel(parkingLot, () -> {
            vehiclePanel.loadData(); // Refresh lại danh sách xe
            cardLayout.show(contentPanel, "FUNCTION_4"); // Chuyển sang màn hình quản lý xe
        });

        // Tab 2: Thu vé
        CollectTicketPanel collectTicketPanel = new CollectTicketPanel(parkingLot);

        ticketTabPane.addTab("Tạo Vé (Vào Bãi)", createTicketPanel);
        ticketTabPane.addTab("Thu Vé (Xuất Bãi)", collectTicketPanel);
        contentPanel.add(ticketTabPane, "FUNCTION_1");

        // Màn hình 2: Quản lý vé
        ticketPanel = new TicketManagementPanel(parkingLot);
        contentPanel.add(ticketPanel, "FUNCTION_2");

        // Màn hình 5: Quản lý bãi xe
        spotPanel = new SpotManagementPanel(parkingLot);
        contentPanel.add(spotPanel, "FUNCTION_5");

        // Màn hình 7: Tìm kiếm
        searchPanel = new SearchPanel(parkingLot);
        contentPanel.add(searchPanel, "FUNCTION_7");

        // Màn hình chờ (Placeholder)
        JLabel placeholder = new JLabel("Chào mừng đến với Parking Manager", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.BOLD, 24));
        placeholder.setForeground(Color.GRAY);
        contentPanel.add(placeholder, "PLACEHOLDER");

        // Mặc định hiện màn hình chờ
        cardLayout.show(contentPanel, "PLACEHOLDER");
    }

    /**
     * Làm mới dữ liệu của panel khi chuyển tab.
     * (Do CardLayout không tự refresh, ta cần gọi thủ công)
     */
    private void refreshPanelData(String cardName) {
        switch (cardName) {
            case "FUNCTION_2":
                if (ticketPanel != null)
                    ticketPanel.refresh();
                break;
            case "FUNCTION_4":
                if (vehiclePanel != null)
                    vehiclePanel.loadData();
                break;
            case "FUNCTION_5":
                if (spotPanel != null)
                    spotPanel.loadData();
                break;
            // Các chức năng khác nếu cần refresh thì thêm vào đây
        }
    }

    // --- Actions ---

    /**
     * Thực hiện lưu dữ liệu và thoát ứng dụng.
     */
    private void performExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn thoát?\nDữ liệu sẽ được tự động lưu.",
                "Xác nhận thoát",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (parkingLot != null) {
                    parkingLot.saveAllData();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + ex.getMessage());
            }
            System.exit(0);
        }
    }
}
