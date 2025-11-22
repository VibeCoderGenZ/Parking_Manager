package logic;

public class Vehicle {
    private final String licensePlate;
    private final VehicleType type;
    private final String ownerName;
    private final String ownerPhone;

    public Vehicle(String licensePlate, VehicleType type, String ownerName, String ownerPhone) {
        this.licensePlate = licensePlate;
        this.type = type;
        this.ownerName = chuanHoaHoTen(ownerName);
        this.ownerPhone = chuanHoaSoDienThoai(ownerPhone);
    }

    // Hàm chuẩn hóa xâu họ tên
    public static String chuanHoaHoTen(String hoTen) {
        if (hoTen == null)
            return "";
        String trimmed = hoTen.trim();
        if (trimmed.isEmpty())
            return "";
        String[] a = trimmed.split("\\s+");
        StringBuilder result = new StringBuilder();
        for (String x : a) {
            if (x.isEmpty())
                continue;
            result.append(Character.toUpperCase(x.charAt(0)))
                    .append(x.substring(1).toLowerCase())
                    .append(" ");
        }
        return result.toString().trim();
    }

    // Hàm chuẩn hóa số điện thoại (bỏ dấu cách)
    public static String chuanHoaSoDienThoai(String soDienThoai) {
        return soDienThoai.replaceAll("\\s+", "");
    }

    // Getter/Setter
    public String getLicensePlate() {
        return licensePlate;
    }

    public VehicleType getType() {
        return type;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    @Override
    public String toString() {
        return licensePlate + "," + type + "," + ownerName + "," + ownerPhone;
    }
}
