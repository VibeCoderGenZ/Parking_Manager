package gui;

import logic.ParkingLot;
import javax.swing.*;
import java.awt.*;

/**
 * Panel tìm kiếm tổng hợp.
 * Chứa các tab con để tìm kiếm Vé, Xe, và Vị trí đỗ.
 */
public class SearchPanel extends JPanel {

    private SearchTicketPanel searchTicketPanel;
    private SearchVehiclePanel searchVehiclePanel;
    private SearchSpotPanel searchSpotPanel;
    private JTabbedPane tabbedPane;

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
        tabbedPane = new JTabbedPane();

        // Thêm các tab tìm kiếm chi tiết
        // Tab 1: Tìm kiếm thông tin Vé
        searchTicketPanel = new SearchTicketPanel(parkingLot);
        tabbedPane.addTab("Tìm Vé", searchTicketPanel);
        // Tab 2: Tìm kiếm thông tin Xe
        searchVehiclePanel = new SearchVehiclePanel(parkingLot);
        tabbedPane.addTab("Tìm Xe", searchVehiclePanel);
        // Tab 3: Tìm kiếm thông tin Vị trí đỗ
        searchSpotPanel = new SearchSpotPanel(parkingLot);
        tabbedPane.addTab("Tìm Vị Trí", searchSpotPanel);

        tabbedPane.addChangeListener(e -> {
            refreshSelectedTab();
        });

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void refreshSelectedTab() {
        if (tabbedPane == null)
            return;

        Component selected = tabbedPane.getSelectedComponent();
        if (selected == searchTicketPanel && searchTicketPanel != null) {
            searchTicketPanel.refreshResults();
        } else if (selected == searchVehiclePanel && searchVehiclePanel != null) {
            searchVehiclePanel.refreshResults();
        } else if (selected == searchSpotPanel && searchSpotPanel != null) {
            searchSpotPanel.refreshResults();
        }
    }

    public void refreshCurrentTab() {
        refreshSelectedTab();
    }

    public void refreshTicketTab() {
        refreshCurrentTab();
    }
}
