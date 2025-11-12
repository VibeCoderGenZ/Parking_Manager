package model;

/**
 * Lớp Model đại diện cho một khu vực đỗ xe (Parking Zone).
 * Cấu trúc của lớp này phản ánh các cột trong bảng 'parking_zones' của database.
 */
public class ParkingZone {

    private int id; // ID của khu vực, được gán bởi database (AUTO_INCREMENT)
    private String name; // Tên khu vực (ví dụ: "Khu A", "Hầm B1")
    private VehicleType allowedVehicleType; // Loại xe được phép đỗ
    private int numberOfSpots; // Tổng số chỗ trong khu vực này

    // Constructor để tạo đối tượng từ dữ liệu đọc ra từ database
    public ParkingZone(int id, String name, VehicleType allowedVehicleType, int numberOfSpots) {
        this.id = id;
        this.name = name;
        this.allowedVehicleType = allowedVehicleType;
        this.numberOfSpots = numberOfSpots;
    }
    
    // Constructor để tạo một đối tượng mới trước khi lưu vào DB (chưa có ID)
    public ParkingZone(String name, VehicleType allowedVehicleType, int numberOfSpots) {
        this.name = name;
        this.allowedVehicleType = allowedVehicleType;
        this.numberOfSpots = numberOfSpots;
    }

    // --- Getters ---
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

    // --- Setters ---
    // ID chỉ nên được set bởi DAO sau khi lưu vào DB
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
