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

    private void initComponents(ParkingLot parkingLot) {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Thêm các tab tìm kiếm chi tiết
        tabbedPane.addTab("Tìm Vé", new SearchTicketPanel(parkingLot));
        tabbedPane.addTab("Tìm Xe", new SearchVehiclePanel(parkingLot));
        tabbedPane.addTab("Tìm Vị Trí", new SearchSpotPanel(parkingLot));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
