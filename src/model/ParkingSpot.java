package model;

/**
 * Lớp Model đại diện cho một chỗ đỗ xe (Parking Spot).
 * Cấu trúc của lớp này phản ánh các cột trong bảng 'parking_spots' của database.
 */
public class ParkingSpot {

    private String id; // ID của chỗ đỗ (ví dụ: "A-1", "B-12")
    private int zoneId; // ID của khu vực mà chỗ đỗ này thuộc về
    private boolean isOccupied; // Trạng thái có xe đỗ hay không
    private String licensePlate; // Biển số xe đang đỗ (null nếu trống)

    // Constructor để tạo đối tượng từ dữ liệu đọc ra từ database
    public ParkingSpot(String id, int zoneId, boolean isOccupied, String licensePlate) {
        this.id = id;
        this.zoneId = zoneId;
        this.isOccupied = isOccupied;
        this.licensePlate = licensePlate;
    }
    
    // Constructor để tạo một đối tượng mới trước khi lưu vào DB
    public ParkingSpot(String id, int zoneId) {
        this.id = id;
        this.zoneId = zoneId;
        this.isOccupied = false; // Mặc định là trống
        this.licensePlate = null;
    }

    // --- Getters ---
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

    // --- Setters ---
    public void setId(String id) {
        this.id = id;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
}
