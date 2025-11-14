package model;

public class ParkingSpot {

    private final String id;
    private final int zoneId;
    private boolean isOccupied;
    private String licensePlate;

    public ParkingSpot(String id, int zoneId, boolean isOccupied, String licensePlate) {
        this.id = id;
        this.zoneId = zoneId;
        this.isOccupied = isOccupied;
        this.licensePlate = licensePlate;
    }
    
    public ParkingSpot(String id, int zoneId) {
        this.id = id;
        this.zoneId = zoneId;
        this.isOccupied = false;
        this.licensePlate = null;
    }

    public String getId() {
        return id;
    }

    public int getZoneId() {
        return zoneId;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
