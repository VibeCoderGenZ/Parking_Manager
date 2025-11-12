package dao;

import model.ParkingZone;
import model.VehicleType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO cho đối tượng ParkingZone.
 */
public class ParkingZoneDAO {

    /**
     * Lấy một khu vực đỗ xe bằng ID của nó.
     * @param zoneId ID của khu vực cần tìm.
     * @return Đối tượng ParkingZone nếu tìm thấy, ngược lại là null.
     * @throws SQLException nếu có lỗi khi truy vấn.
     */
    public ParkingZone getZoneById(int zoneId) throws SQLException {
        String sql = "SELECT pz.id, pz.name, pz.number_of_spots, vt.name AS vehicle_type_name " +
                     "FROM parking_zones pz " +
                     "JOIN vehicle_type vt ON pz.allowed_vehicle_type_id = vt.id " +
                     "WHERE pz.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, zoneId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToParkingZone(rs);
                }
            }
        }
        return null;
    }

    public List<ParkingZone> getAllParkingZones() throws SQLException {
        List<ParkingZone> zones = new ArrayList<>();
        String sql = "SELECT pz.id, pz.name, pz.number_of_spots, vt.name AS vehicle_type_name " +
                     "FROM parking_zones pz " +
                     "JOIN vehicle_type vt ON pz.allowed_vehicle_type_id = vt.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                zones.add(mapResultSetToParkingZone(rs));
            }
        }
        return zones;
    }

    public void addParkingZone(ParkingZone zone) throws SQLException {
        String sql = "INSERT INTO parking_zones(name, allowed_vehicle_type_id, number_of_spots) VALUES(?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, zone.getName());
            pstmt.setInt(2, getVehicleTypeId(zone.getAllowedVehicleType(), conn));
            pstmt.setInt(3, zone.getNumberOfSpots());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        zone.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    private int getVehicleTypeId(VehicleType type, Connection conn) throws SQLException {
        String sql = "SELECT id FROM vehicle_type WHERE name = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type.name());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                } else {
                    throw new SQLException("VehicleType '" + type.name() + "' not found in database.");
                }
            }
        }
    }

    private ParkingZone mapResultSetToParkingZone(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        int numberOfSpots = rs.getInt("number_of_spots");
        VehicleType allowedType = VehicleType.valueOf(rs.getString("vehicle_type_name"));
        return new ParkingZone(id, name, allowedType, numberOfSpots);
    }
}
