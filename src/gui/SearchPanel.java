package gui;

import logic.ParkingLot;
import javax.swing.*;
import java.awt.*;

public class SearchPanel extends JPanel {
    public SearchPanel(ParkingLot parkingLot) {
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tìm Vé", new SearchTicketPanel(parkingLot));
        tabbedPane.addTab("Tìm Xe", new SearchVehiclePanel(parkingLot));
        tabbedPane.addTab("Tìm Vị Trí", new SearchSpotPanel(parkingLot));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
