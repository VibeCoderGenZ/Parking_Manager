package gui;

import logic.ParkingLot;
import javax.swing.*;
import java.awt.*;

public class SearchTicketPanel extends JPanel {
    private ParkingLot parkingLot;

    public SearchTicketPanel(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
        setLayout(new BorderLayout());
        add(new JLabel("Khu vực tìm kiếm Vé", SwingConstants.CENTER), BorderLayout.CENTER);
    }
}
