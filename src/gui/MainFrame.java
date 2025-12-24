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
    /**
     * Đối tượng quản lý toàn bộ logic của hệ thống (Singleton-like trong phạm vi
     * MainFrame).
     */
    private ParkingLot parkingLot;

    // --- Fields: UI Components ---
    private JPanel navPanel; // Panel bên trái chứa các nút điều hướng
    private JPanel contentPanel; // Panel ở giữa hiển thị nội dung chính
    private CardLayout cardLayout; // Layout quản lý việc chuyển đổi giữa các màn hình chức năng

    // --- Fields: Functional Panels ---
    // Các màn hình chức năng được khởi tạo một lần và tái sử dụng
    private VehicleManagementPanel vehiclePanel;
    private TicketManagementPanel ticketPanel;
    private SpotManagementPanel spotPanel;
    private SearchPanel searchPanel;

    // --- Constructor ---
    public MainFrame() {
        // 1. Khởi tạo Logic (Load dữ liệu từ file)
        initLogic();

        // 2. Cấu hình cửa sổ chính (Title, Size, Close Operation)
        initFrameSettings();

        // 3. Khởi tạo giao diện (Navigation + Content Panels)
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
     * Tạo panel điều hướng bên trái (Navigation Bar).
     * Chứa các nút để chuyển đổi giữa các chức năng.
     */
    private void initNavPanel() {
        navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(7, 1, 10, 10)); // 7 hàng, 1 cột, khoảng cách 10px
        navPanel.setPreferredSize(new Dimension(200, 0)); // Rộng 200px cố định
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navPanel.setBackground(new Color(240, 240, 240)); // Màu nền xám nhẹ

        // Tạo các nút chức năng tương ứng với yêu cầu
        createNavButton("Tạo Vé / Thu Vé", "FUNCTION_1");
        createNavButton("Danh sách Vé", "FUNCTION_2");
        navPanel.add(new JLabel()); // Khoảng trống (Vị trí 3 - Placeholder)
        createNavButton("Danh sách Xe", "FUNCTION_4");
        createNavButton("Danh sách Bãi Đỗ", "FUNCTION_5");
        navPanel.add(new JLabel()); // Khoảng trống (Vị trí 6 - Placeholder)
        createNavButton("Tìm Kiếm nâng cao", "FUNCTION_7");
    }

    /**
     * Helper method để tạo nút điều hướng và gắn sự kiện chuyển tab.
     *
     * @param text     Tên hiển thị trên nút.
     * @param cardName Tên định danh của màn hình (Card Name) trong CardLayout.
     */
    private void createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        btn.addActionListener(e -> {
            // Refresh dữ liệu cho các panel cần thiết trước khi hiển thị để đảm bảo tính
            // nhất quán
            refreshPanelData(cardName);
            // Chuyển đổi màn hình hiển thị
            cardLayout.show(contentPanel, cardName);
        });

        navPanel.add(btn);
    }

    /**
     * Tạo panel nội dung chính sử dụng CardLayout.
     * Khởi tạo tất cả các màn hình chức năng và thêm vào CardLayout.
     */
    private void initContentPanel() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // --- Khởi tạo các màn hình chức năng ---

        // Màn hình 4: Quản lý xe (Cần tạo trước để truyền callback vào
        // CreateTicketPanel)
        vehiclePanel = new VehicleManagementPanel(parkingLot);
        contentPanel.add(vehiclePanel, "FUNCTION_4");

        // Màn hình 1: Tạo vé / Thu vé (Sử dụng TabbedPane để gộp 2 chức năng con)
        JTabbedPane ticketTabPane = new JTabbedPane();
        ticketTabPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Tab 1: Tạo vé (Có callback chuyển sang màn hình thêm xe nếu xe chưa tồn tại)
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

        // Màn hình 5: Quản lý bãi xe (Chỗ đỗ)
        spotPanel = new SpotManagementPanel(parkingLot);
        contentPanel.add(spotPanel, "FUNCTION_5");

        // Màn hình 7: Tìm kiếm
        searchPanel = new SearchPanel(parkingLot);
        contentPanel.add(searchPanel, "FUNCTION_7");

        // Màn hình chờ (Placeholder) khi mới mở ứng dụng
        JLabel placeholder = new JLabel("Chào mừng đến với Parking Manager", SwingConstants.CENTER);
        placeholder.setFont(new Font("Segoe UI", Font.BOLD, 24));
        placeholder.setForeground(Color.GRAY);
        contentPanel.add(placeholder, "PLACEHOLDER");

        // Mặc định hiện màn hình chờ
        cardLayout.show(contentPanel, "PLACEHOLDER");
    }

    /**
     * Làm mới dữ liệu của panel khi chuyển tab.
     * (Do CardLayout không tự refresh, ta cần gọi thủ công để cập nhật dữ liệu mới
     * nhất).
     *
     * @param cardName Tên màn hình sắp được hiển thị.
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
            case "FUNCTION_7":
                if (searchPanel != null)
                    searchPanel.refreshCurrentTab();
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
                "Bạn muốn đóng ứng dụng?",
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
