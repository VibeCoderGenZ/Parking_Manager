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
        navPanel.setPreferredSize(new Dimension(150, 0)); // Chiều rộng 150px
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

        // Chức năng 4: Quản lý xe (Tạo trước để truyền callback vào CreateTicketPanel)
        VehicleManagementPanel vehiclePanel = new VehicleManagementPanel(parkingLot);
        contentPanel.add(vehiclePanel, "FUNCTION_4");

        // Chức năng 1: Tạo vé / Thu vé (Dùng Tab để chứa 2 panel con)
        JTabbedPane ticketTabPane = new JTabbedPane();
        ticketTabPane.addTab("Tạo Vé", new CreateTicketPanel(parkingLot, () -> {
            // Callback khi người dùng muốn thêm xe mới từ màn hình tạo vé
            vehiclePanel.loadData();
            cardLayout.show(contentPanel, "FUNCTION_4");
        }));
        ticketTabPane.addTab("Thu Vé", new CollectTicketPanel(parkingLot));
        contentPanel.add(ticketTabPane, "FUNCTION_1");

        // Chức năng 2: Quản lý vé
        TicketManagementPanel ticketPanel = new TicketManagementPanel(parkingLot);
        contentPanel.add(ticketPanel, "FUNCTION_2");

        // Chức năng 5: Quản lý bãi xe (Vị trí đỗ)
        SpotManagementPanel spotPanel = new SpotManagementPanel(parkingLot);
        contentPanel.add(spotPanel, "FUNCTION_5");

        // Chức năng 7: Tìm kiếm
        SearchPanel searchPanel = new SearchPanel(parkingLot);
        contentPanel.add(searchPanel, "FUNCTION_7");

        // Tạo 7 nút chức năng (Tạm để tên placeholder)
        for (int i = 1; i <= 7; i++) {
            if (i == 3 || i == 6) {
                // Thay thế vị trí 3 và 6 bằng khoảng trống
                navPanel.add(new JLabel());
            } else {
                String btnText = "Chức năng " + i;
                if (i == 1) {
                    btnText = "Tạo Vé / Thu vé";
                } else if (i == 2) {
                    btnText = "Quản Lý Vé";
                } else if (i == 4) {
                    btnText = "Quản Lý Xe";
                } else if (i == 5) {
                    btnText = "Quản Lý Bãi Xe";
                } else if (i == 7) {
                    btnText = "Tìm Kiếm";
                }
                JButton btn = new JButton(btnText);

                // Gắn sự kiện bấm nút
                final int funcIndex = i;
                btn.addActionListener(e -> {
                    if (funcIndex == 1) {
                        cardLayout.show(contentPanel, "FUNCTION_1");
                    } else if (funcIndex == 2) {
                        ticketPanel.refresh();
                        cardLayout.show(contentPanel, "FUNCTION_2");
                    } else if (funcIndex == 4) {
                        vehiclePanel.loadData();
                        cardLayout.show(contentPanel, "FUNCTION_4");
                    } else if (funcIndex == 5) {
                        spotPanel.loadData();
                        cardLayout.show(contentPanel, "FUNCTION_5");
                    } else if (funcIndex == 7) {
                        cardLayout.show(contentPanel, "FUNCTION_7");
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
