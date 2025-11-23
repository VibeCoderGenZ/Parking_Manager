package gui;

import logic.ParkingLot;
import javax.swing.*;
import java.awt.*;

public class SearchVehiclePanel extends JPanel {
    private ParkingLot parkingLot;

    public SearchVehiclePanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout());
        add(new JLabel("Khu vực tìm kiếm Xe", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
