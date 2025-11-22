package gui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import logic.ParkingLot;

public class MainFrame extends JFrame {

    private JPanel navPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private ParkingLot parkingLot;

    public MainFrame() {
        // 1. Khởi tạo dữ liệu logic
        try {
            parkingLot = new ParkingLot();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc dữ liệu: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }

        // 2. Cài đặt cơ bản
        setTitle("Hệ thống Quản lý Bãi xe - Dashboard");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 3. Tạo giao diện
        createNavPanel();
        createContentPanel();

        add(navPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // 4. Xử lý sự kiện đóng cửa sổ để lưu dữ liệu
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                saveAndExit();
            }
        });
    }

    private void createNavPanel() {
        navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(7, 1, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navPanel.setBackground(new Color(220, 220, 220));

        JButton dailyTasksButton = new JButton("Xuất Vé / Thu Vé");
        JButton vehicleButton = new JButton("Quản Lý Phương Tiện");
        JButton managementButton = new JButton("Quản Lý Bãi Xe");
        JButton statsButton = new JButton("Quản Lý Vé");
        JButton logoutButton = new JButton("Đăng xuất");

        dailyTasksButton.addActionListener(e -> cardLayout.show(contentPanel, "DAILY_TASKS"));
        vehicleButton.addActionListener(e -> cardLayout.show(contentPanel, "VEHICLE"));
        managementButton.addActionListener(e -> cardLayout.show(contentPanel, "MANAGEMENT"));
        statsButton.addActionListener(e -> cardLayout.show(contentPanel, "STATS"));

        logoutButton.addActionListener(e -> saveAndExit());

        navPanel.add(dailyTasksButton);
        navPanel.add(statsButton);
        navPanel.add(new JSeparator());
        navPanel.add(vehicleButton);
        navPanel.add(managementButton);
        navPanel.add(new JSeparator());
        navPanel.add(logoutButton);
    }

    private void createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Panel Chào mừng
        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Chào mừng đến với Hệ thống Quản lý Bãi Gửi Xe", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        // Panel Tác vụ hàng ngày (TabbedPane)
        JTabbedPane dailyTasksPanel = new JTabbedPane();
        dailyTasksPanel.addTab("Xuất Vé (Xe Vào)", createPlaceholderPanel("Chức năng Xuất Vé cho xe vào bãi"));
        dailyTasksPanel.addTab("Thu Vé (Xe Ra)", createPlaceholderPanel("Chức năng Thu Vé và tính tiền khi xe ra"));

        // Các Panel chức năng khác (Placeholder)
        JPanel vehiclePanel = createPlaceholderPanel("Chức năng Quản lý Phương Tiện (Thêm/Sửa/Xóa Xe)");
        JPanel managementPanel = createPlaceholderPanel("Chức năng Quản lý Bãi Xe (Thêm/Sửa/Xóa Chỗ Đỗ)");
        JPanel statsPanel = createPlaceholderPanel("Chức năng Quản lý Vé & Lịch sử");

        // Thêm vào CardLayout
        contentPanel.add(welcomePanel, "WELCOME");
        contentPanel.add(dailyTasksPanel, "DAILY_TASKS");
        contentPanel.add(vehiclePanel, "VEHICLE");
        contentPanel.add(managementPanel, "MANAGEMENT");
        contentPanel.add(statsPanel, "STATS");

        // Mặc định hiện trang Welcome
        cardLayout.show(contentPanel, "WELCOME");
    }

    // Hàm tạo Panel tạm thời (Placeholder)
    private JPanel createPlaceholderPanel(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        JLabel label = new JLabel(title);
        label.setFont(new Font("Arial", Font.ITALIC, 18));
        label.setForeground(Color.GRAY);
        panel.add(label);
        return panel;
    }

    // Hàm lưu dữ liệu và thoát
    private void saveAndExit() {
        try {
            if (parkingLot != null) {
                parkingLot.saveAllData();
            }
            System.out.println("Dữ liệu đã được lưu. Thoát chương trình.");
            System.exit(0);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
    }
}