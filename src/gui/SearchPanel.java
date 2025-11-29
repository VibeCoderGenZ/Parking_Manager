package gui;

import logic.ParkingLot;
import javax.swing.*;
import java.awt.*;

/**
 * Panel tìm kiếm tổng hợp.
 * Chứa các tab con để tìm kiếm Vé, Xe, và Vị trí đỗ.
 */
public class SearchPanel extends JPanel {

    public SearchPanel(ParkingLot parkingLot) {
        setLayout(new BorderLayout());
        initComponents(parkingLot);
    }

    /**
     * Khởi tạo giao diện tìm kiếm.
     * Sử dụng JTabbedPane để chia thành các tab con cho từng loại đối tượng.
     * 
     * @param parkingLot Đối tượng logic dùng chung.
     */
    private void initComponents(ParkingLot parkingLot) {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Thêm các tab tìm kiếm chi tiết
        // Tab 1: Tìm kiếm thông tin Vé
        tabbedPane.addTab("Tìm Vé", new SearchTicketPanel(parkingLot));
        // Tab 2: Tìm kiếm thông tin Xe
        tabbedPane.addTab("Tìm Xe", new SearchVehiclePanel(parkingLot));
        // Tab 3: Tìm kiếm thông tin Vị trí đỗ
        tabbedPane.addTab("Tìm Vị Trí", new SearchSpotPanel(parkingLot));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
