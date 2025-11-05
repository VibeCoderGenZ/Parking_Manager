public class ParkingSpot {
    private final String spotId;
    private final VehicleType allowedType;
    private Vehicle vehicle;

    public ParkingSpot(String spotId, VehicleType allowedType) {
        this.spotId = spotId;
        this.allowedType = allowedType;
        this.vehicle = null;
    }

    public String getSpotId() {
        return spotId;
    }

    public VehicleType getAllowedType() {
        return allowedType;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public boolean isOccupied() {
        return vehicle != null;
    }

    public boolean park(Vehicle vehicle) {
        if (isOccupied() && vehicle.getType() == this.allowedType) {
            this.vehicle = vehicle;
            return true;
        }
        return false;
    }

    public void vacate() {
        this.vehicle = null;
    }
}
