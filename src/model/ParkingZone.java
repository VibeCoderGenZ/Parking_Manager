package model;

import java.util.ArrayList;
import java.util.List;

public class ParkingZone {
    private final String name;
    private final VehicleType allowedType;
    private final List<ParkingSpot> parkingSpots;

    public ParkingZone(String name, int numberOfSpots, VehicleType allowedType) {
        this.name = name;
        this.allowedType = allowedType;
        this.parkingSpots = new ArrayList<>();
        for (int i = 1; i <= numberOfSpots; i++) {
            String spotId = name + "-" + i;
            this.parkingSpots.add(new ParkingSpot(spotId));
        }
    }

    public String getName() {
        return name;
    }

    public VehicleType getAllowedType() {
        return allowedType;
    }

    public List<ParkingSpot> getParkingSpots() {
        return parkingSpots;
    }

    public int getNumberOfSpots() {
        return this.parkingSpots.size();
    }

    public int getNumberOfAvailableSpots() {
        int availableSpots = 0;
        for (ParkingSpot spot : parkingSpots) {
            if (spot.isOccupied()) {
                availableSpots++;
            }
        }
        return availableSpots;
    }

    public void addSpot() {
        int nextSpotNumber = getNumberOfSpots() + 1;
        String newSpotId = this.name + "-" + nextSpotNumber;
        this.parkingSpots.add(new ParkingSpot(newSpotId));
    }

    public void removeSpot() {
        if (!this.parkingSpots.isEmpty()) {
            this.parkingSpots.removeLast();
        }
    }

    public boolean parkCheck(Vehicle vehicle) {
        if (vehicle.getType() != this.allowedType) {
            return false;
        }
        for (ParkingSpot spot : parkingSpots) {
            if (spot.isOccupied()) {
                return spot.park(vehicle);
            }
        }
        return false;
    }
}
