package model;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private final List<ParkingZone> parkingZones;


    public ParkingLot() {
        this.parkingZones = new ArrayList<>();
    }

    public List<ParkingZone> getParkingZones() {
        return parkingZones;
    }

    public void addZone(ParkingZone zone) {
        this.parkingZones.add(zone);
    }
}
