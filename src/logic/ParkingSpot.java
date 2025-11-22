package logic;

public class ParkingSpot {
    private final int spotID;
    private final VehicleType allowedType;
    private String licensePlate;
    private boolean occupied;

    public ParkingSpot(int spotID, VehicleType allowedType, String licensePlate, boolean occupied) {
        this.spotID = spotID;
        this.allowedType = allowedType;
        this.licensePlate = licensePlate;
        this.occupied = occupied;
    }

    // Getter/Setter
    public int getSpotID() {
        return spotID;
    }

    public VehicleType getAllowedType() {
        return allowedType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    @Override
    public String toString() {
        return spotID + "," + allowedType + "," + licensePlate + "," + occupied;
    }

}
