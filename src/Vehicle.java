public class Vehicle {
    private final String ownerName;
    private final String ownerPhone;
    private final String licensePlate;
    private final VehicleType type;

    public Vehicle(String ownerName, String ownerPhone, String licensePlate, VehicleType type) {
        this.ownerName = ownerName;
        this.ownerPhone = ownerPhone;
        this.licensePlate = licensePlate;
        this.type = type;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getType() {
        return type;
    }
}
