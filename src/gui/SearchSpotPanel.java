package gui;

import logic.ParkingLot;
import javax.swing.*;
import java.awt.*;

public class SearchSpotPanel extends JPanel {
    private ParkingLot parkingLot;

    public SearchSpotPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout());
        add(new JLabel("Khu vực tìm kiếm Vị Trí", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
