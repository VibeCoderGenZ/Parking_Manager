package dao;

import model.Vehicle;
import model.VehicleType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    /**
     * Lấy thông tin một chiếc xe dựa vào biển số.
     *
     * @param licensePlate Biển số xe cần tìm.
     * @return Đối tượng Vehicle nếu tìm thấy, ngược lại trả về null.
     * @throws SQLException nếu có lỗi khi truy vấn.
     */
    public Vehicle getVehicleByLicensePlate(String licensePlate) throws SQLException {
        // Sử dụng JOIN để lấy cả tên của loại xe từ bảng vehicle_type
        String sql = "SELECT v.license_plate, v.owner_name, v.owner_phone, vt.name AS vehicle_type_name " +
                "FROM vehicles v " +
                "JOIN vehicle_type vt ON v.vehicle_type_id = vt.id " +
                "WHERE v.license_plate = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licensePlate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String ownerName = rs.getString("owner_name");
                    String ownerPhone = rs.getString("owner_phone");
                    // Chuyển đổi tên loại xe (String) về enum VehicleType
                    VehicleType type = VehicleType.valueOf(rs.getString("vehicle_type_name"));

                    return new Vehicle(ownerName, ownerPhone, licensePlate, type);
                }
            }
        }
        return null; // Không tìm thấy xe
    }

    /**
     * Lấy tất cả các xe trong database.
     *
     * @return Danh sách các đối tượng Vehicle.
     * @throws SQLException nếu có lỗi khi truy vấn.
     */
    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT v.license_plate, v.owner_name, v.owner_phone, vt.name AS vehicle_type_name " +
                "FROM vehicles v " +
                "JOIN vehicle_type vt ON v.vehicle_type_id = vt.id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String licensePlate = rs.getString("license_plate");
                String ownerName = rs.getString("owner_name");
                String ownerPhone = rs.getString("owner_phone");
                VehicleType type = VehicleType.valueOf(rs.getString("vehicle_type_name"));

                vehicles.add(new Vehicle(ownerName, ownerPhone, licensePlate, type));
            }
        }
        return vehicles;
    }

    /**
     * Thêm một chiếc xe mới vào database.
     *
     * @param vehicle Đối tượng Vehicle cần thêm.
     * @throws SQLException nếu có lỗi (ví dụ: biển số đã tồn tại).
     */
    public void addVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles(license_plate, owner_name, owner_phone, vehicle_type_id) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicle.getLicensePlate());
            pstmt.setString(2, vehicle.getOwnerName());
            pstmt.setString(3, vehicle.getOwnerPhone());
            // Chuyển đổi enum thành ID tương ứng trong database
            // Giả định ID trong bảng vehicle_type là 1, 2, 3 tương ứng với thứ tự của enum
            pstmt.setInt(4, vehicle.getType().ordinal() + 1);

            pstmt.executeUpdate();
        }
    }

    /**
     * Cập nhật thông tin của một chiếc xe (thường là tên hoặc SĐT chủ xe).
     *
     * @param vehicle Đối tượng Vehicle chứa thông tin mới.
     * @throws SQLException nếu có lỗi khi cập nhật.
     */
    public void updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET owner_name = ?, owner_phone = ?, vehicle_type_id = ? WHERE license_plate = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicle.getOwnerName());
            pstmt.setString(2, vehicle.getOwnerPhone());
            pstmt.setInt(3, vehicle.getType().ordinal() + 1);
            pstmt.setString(4, vehicle.getLicensePlate());

            pstmt.executeUpdate();
        }
    }

    /**
     * Xóa một chiếc xe khỏi database.
     *
     * @param licensePlate Biển số xe cần xóa.
     * @throws SQLException nếu có lỗi (ví dụ: xe đang được tham chiếu trong bảng vé).
     */
    public void deleteVehicle(String licensePlate) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE license_plate = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licensePlate);
            pstmt.executeUpdate();
        }
    }
}
