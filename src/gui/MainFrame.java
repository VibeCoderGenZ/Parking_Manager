package gui;

import javax.swing.*;
import java.awt.*;

import logic.ParkingLot;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MainFrame extends JFrame {

    private JPanel navPanel;
    private JPanel contentPanel;
    private ParkingLot parkingLot;

    public MainFrame() {
        // 1. Tự khởi tạo Logic ngay trong MainFrame
        try {
            this.parkingLot = new ParkingLot();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc dữ liệu: " + e.getMessage());
            // Nếu lỗi nặng quá có thể thoát luôn hoặc tạo data rỗng
        }

        setTitle("Parking Manager");
        setSize(800, 600);
        // Thay đổi hành vi đóng mặc định để xử lý lưu file thủ công
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        // 2. Tự xử lý sự kiện tắt app để lưu dữ liệu
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (parkingLot != null) {
                        parkingLot.saveAllData();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                System.exit(0);
            }
        });

        // Sử dụng BorderLayout cho layout chính
        setLayout(new BorderLayout());

        // --- 1. Navigation Panel (Bên trái) ---
        navPanel = new JPanel();
        // Grid layout 7 hàng, 1 cột để chứa 7 nút chức năng
        navPanel.setLayout(new GridLayout(7, 1, 10, 10));
        navPanel.setPreferredSize(new Dimension(200, 0)); // Chiều rộng 200px
        navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding xung quanh

        // --- 2. Content Panel (Ở giữa) ---
        contentPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout); // Dùng CardLayout để dễ chuyển đổi giao diện con
        contentPanel.setBackground(Color.WHITE);

        // Thêm một label tạm để hiển thị vùng nội dung
        JLabel placeholder = new JLabel("Khu vực hiển thị chức năng chính", SwingConstants.CENTER);
        contentPanel.add(placeholder, "PLACEHOLDER");

        // -- Setup các panel chức năng --
        // Chức năng 1: Tạo vé / Thu vé (Dùng Tab để chứa 2 panel con)
        JTabbedPane ticketTabPane = new JTabbedPane();
        ticketTabPane.addTab("Tạo Vé", new CreateTicketPanel(parkingLot));
        ticketTabPane.addTab("Thu Vé", new CollectTicketPanel(parkingLot));
        contentPanel.add(ticketTabPane, "FUNCTION_1");

        // Tạo 7 nút chức năng (Tạm để tên placeholder)
        for (int i = 1; i <= 7; i++) {
            if (i == 3 || i == 6) {
                // Thay thế vị trí 3 và 6 bằng khoảng trống
                navPanel.add(new JLabel());
            } else {
                String btnText = "Chức năng " + i;
                if (i == 1) {
                    btnText = "Tạo Vé / Thu vé";
                }
                JButton btn = new JButton(btnText);

                // Gắn sự kiện bấm nút
                final int funcIndex = i;
                btn.addActionListener(e -> {
                    if (funcIndex == 1) {
                        cardLayout.show(contentPanel, "FUNCTION_1");
                    } else {
                        // Các chức năng khác chưa có thì hiện placeholder
                        cardLayout.show(contentPanel, "PLACEHOLDER");
                    }
                });

                navPanel.add(btn);
            }
        }

        // Thêm các panel vào Frame
        add(navPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
}
