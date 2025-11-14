package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel navPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("Hệ thống Quản lý Bãi xe - Dashboard");
        setSize(800, 600); // Tăng chiều cao để chứa nút mới
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createNavPanel();
        createContentPanel();

        add(navPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }

    private void createNavPanel() {
        navPanel = new JPanel();
        navPanel.setLayout(new GridLayout(8, 1, 10, 10)); // Tăng số hàng
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navPanel.setBackground(new Color(220, 220, 220));

        JButton dailyTasksButton = new JButton("Quản Lý Ra/Vào Bãi Xe");
        JButton monthlyTicketButton = new JButton("Quản Lý Vé Tháng");
        JButton managementButton = new JButton("Quản Lý Bãi Xe");
        JButton statsButton = new JButton("Lịch Sử Ra/Vào Bãi Xe");
        JButton customerInfoButton = new JButton("Thông Tin Khách Hàng"); // Nút mới
        JButton logoutButton = new JButton("Đăng xuất");

        dailyTasksButton.addActionListener(e -> cardLayout.show(contentPanel, "DAILY_TASKS"));
        monthlyTicketButton.addActionListener(e -> cardLayout.show(contentPanel, "MONTHLY_TICKET"));
        managementButton.addActionListener(e -> cardLayout.show(contentPanel, "MANAGEMENT"));
        statsButton.addActionListener(e -> cardLayout.show(contentPanel, "STATS"));
        customerInfoButton.addActionListener(e -> cardLayout.show(contentPanel, "CUSTOMER_INFO")); // Action cho nút mới

        logoutButton.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        navPanel.add(dailyTasksButton);
        navPanel.add(statsButton);
        navPanel.add(new JSeparator());
        navPanel.add(managementButton);
        navPanel.add(monthlyTicketButton);
        navPanel.add(customerInfoButton); // Thêm nút mới vào panel
        navPanel.add(new JSeparator());
        navPanel.add(logoutButton);
    }

    private void createContentPanel() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        JPanel welcomePanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Quản Lý Bãi Gửi Xe", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomePanel.add(welcomeLabel, BorderLayout.CENTER);

        JTabbedPane dailyTasksPanel = new JTabbedPane();
        dailyTasksPanel.addTab("Xe Vào", new CheckInPanel());
        dailyTasksPanel.addTab("Xe Ra", new CheckOutPanel());

        MonthlyTicketPanel monthlyTicketPanel = new MonthlyTicketPanel();
        ManagementPanel managementPanel = new ManagementPanel();
        StatsPanel statsPanel = new StatsPanel();
        CustomerInfoPanel customerInfoPanel = new CustomerInfoPanel(); // Khởi tạo panel mới

        contentPanel.add(welcomePanel, "WELCOME");
        contentPanel.add(dailyTasksPanel, "DAILY_TASKS");
        contentPanel.add(monthlyTicketPanel, "MONTHLY_TICKET");
        contentPanel.add(managementPanel, "MANAGEMENT");
        contentPanel.add(statsPanel, "STATS");
        contentPanel.add(customerInfoPanel, "CUSTOMER_INFO"); // Thêm panel mới vào card layout

        cardLayout.show(contentPanel, "WELCOME");
    }
}
