package model;

public class ParkingZone {

    private int id;
    private String name;
    private VehicleType allowedVehicleType;
    private int numberOfSpots;

    public ParkingZone(int id, String name, VehicleType allowedVehicleType, int numberOfSpots) {
        this.id = id;
        this.name = name;
        this.allowedVehicleType = allowedVehicleType;
        this.numberOfSpots = numberOfSpots;
    }
    
    public ParkingZone(String name, VehicleType allowedVehicleType, int numberOfSpots) {
        this.name = name;
        this.allowedVehicleType = allowedVehicleType;
        this.numberOfSpots = numberOfSpots;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public VehicleType getAllowedVehicleType() {
        return allowedVehicleType;
    }

    public int getNumberOfSpots() {
        return numberOfSpots;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAllowedVehicleType(VehicleType allowedVehicleType) {
        this.allowedVehicleType = allowedVehicleType;
    }

    public void setNumberOfSpots(int numberOfSpots) {
        this.numberOfSpots = numberOfSpots;
    }
}
