package dao;

import model.ParkingZone;
import model.VehicleType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParkingZoneDAO {

    public ParkingZone getZoneById(int zoneId) throws SQLException {
        String sql = "SELECT * FROM parking_zones WHERE id = ?";
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
        String sql = "SELECT * FROM parking_zones";
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
        String sql = "INSERT INTO parking_zones(name, allowed_vehicle_type, number_of_spots) VALUES(?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, zone.getName());
            pstmt.setString(2, zone.getAllowedVehicleType().name());
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

    private ParkingZone mapResultSetToParkingZone(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String name = rs.getString("name");
        VehicleType allowedType = VehicleType.valueOf(rs.getString("allowed_vehicle_type"));
        int numberOfSpots = rs.getInt("number_of_spots");
        return new ParkingZone(id, name, allowedType, numberOfSpots);
    }
}
