package model;

public class ParkingSpot {
    private final String spotId;
    private Vehicle vehicle;

    public ParkingSpot(String spotId) {
        this.spotId = spotId;
        this.vehicle = null;
    }

    public String getSpotId() {
        return spotId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public boolean isOccupied() {
        return vehicle == null;
    }

    public boolean park(Vehicle vehicle) {
        if (isOccupied()) {
            this.vehicle = vehicle;
            return true;
        }
        return false;
    }

    public void vacate() {
        this.vehicle = null;
    }
}
