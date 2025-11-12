package gui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel navPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        setTitle("Hệ thống Quản lý Bãi xe - Dashboard");
        setSize(1280, 720);
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
        navPanel.setLayout(new GridLayout(8, 1, 10, 10));
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        navPanel.setBackground(new Color(220, 220, 220));

        JButton dailyTasksButton = new JButton("Check-in/Check-out");
        JButton monthlyTicketButton = new JButton("Quản Lý Vé Tháng");
        JButton managementButton = new JButton("Quản Lý Bãi Xe");
        JButton statsButton = new JButton("Lịch Sử Ra/Vào");
        JButton logoutButton = new JButton("Đăng xuất");

        dailyTasksButton.addActionListener(e -> cardLayout.show(contentPanel, "DAILY_TASKS"));
        monthlyTicketButton.addActionListener(e -> cardLayout.show(contentPanel, "MONTHLY_TICKET"));
        managementButton.addActionListener(e -> cardLayout.show(contentPanel, "MANAGEMENT"));
        statsButton.addActionListener(e -> cardLayout.show(contentPanel, "STATS"));

        logoutButton.addActionListener(e -> {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        });

        navPanel.add(dailyTasksButton);
        navPanel.add(monthlyTicketButton);
        navPanel.add(new JSeparator());
        navPanel.add(managementButton);
        navPanel.add(statsButton);
        navPanel.add(new JSeparator());
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
        dailyTasksPanel.addTab("Check-in", new CheckInPanel());
        dailyTasksPanel.addTab("Check-out", new CheckOutPanel());

        MonthlyTicketPanel monthlyTicketPanel = new MonthlyTicketPanel();
        ManagementPanel managementPanel = new ManagementPanel();
        StatsPanel statsPanel = new StatsPanel();

        contentPanel.add(welcomePanel, "WELCOME");
        contentPanel.add(dailyTasksPanel, "DAILY_TASKS");
        contentPanel.add(monthlyTicketPanel, "MONTHLY_TICKET");
        contentPanel.add(managementPanel, "MANAGEMENT");
        contentPanel.add(statsPanel, "STATS");

        cardLayout.show(contentPanel, "WELCOME");
    }
}
