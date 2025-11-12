package dao;

import model.ParkingSpot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp DAO cho đối tượng ParkingSpot.
 */
public class ParkingSpotDAO {

    /**
     * Lấy tất cả các chỗ đỗ xe từ database, được sắp xếp theo thứ tự số tự nhiên.
     */
    public List<ParkingSpot> getAllParkingSpots() throws SQLException {
        List<ParkingSpot> spots = new ArrayList<>();
        // Sắp xếp theo phần chữ trước, sau đó sắp xếp theo phần số
        String sql = "SELECT * FROM parking_spots ORDER BY SUBSTRING_INDEX(id, '-', 1), CAST(SUBSTRING_INDEX(id, '-', -1) AS UNSIGNED)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                spots.add(mapResultSetToParkingSpot(rs));
            }
        }
        return spots;
    }
    
    /**
     * Lấy tất cả các chỗ đỗ xe thuộc về một khu vực cụ thể, được sắp xếp theo thứ tự số tự nhiên.
     */
    public List<ParkingSpot> getParkingSpotsByZone(int zoneId) throws SQLException {
        List<ParkingSpot> spots = new ArrayList<>();
        // Sắp xếp theo phần số của ID
        String sql = "SELECT * FROM parking_spots WHERE zone_id = ? ORDER BY CAST(SUBSTRING_INDEX(id, '-', -1) AS UNSIGNED)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, zoneId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    spots.add(mapResultSetToParkingSpot(rs));
                }
            }
        }
        return spots;
    }

    // --- CÁC PHƯƠNG THỨC KHÁC GIỮ NGUYÊN ---

    public void deleteParkingSpot(String spotId) throws SQLException {
        String sql = "DELETE FROM parking_spots WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spotId);
            pstmt.executeUpdate();
        }
    }

    public ParkingSpot getParkingSpotById(String spotId) throws SQLException {
        String sql = "SELECT * FROM parking_spots WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spotId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToParkingSpot(rs);
                }
            }
        }
        return null;
    }

    public void updateParkingSpot(ParkingSpot spot) throws SQLException {
        String sql = "UPDATE parking_spots SET is_occupied = ?, license_plate = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, spot.isOccupied());
            pstmt.setString(2, spot.getLicensePlate());
            pstmt.setString(3, spot.getId());
            pstmt.executeUpdate();
        }
    }
    
    public void addParkingSpot(ParkingSpot spot) throws SQLException {
        String sql = "INSERT INTO parking_spots(id, zone_id, is_occupied, license_plate) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spot.getId());
            pstmt.setInt(2, spot.getZoneId());
            pstmt.setBoolean(3, spot.isOccupied());
            pstmt.setString(4, spot.getLicensePlate());
            pstmt.executeUpdate();
        }
    }

    private ParkingSpot mapResultSetToParkingSpot(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        int zoneId = rs.getInt("zone_id");
        boolean isOccupied = rs.getBoolean("is_occupied");
        String licensePlate = rs.getString("license_plate");
        return new ParkingSpot(id, zoneId, isOccupied, licensePlate);
    }
}
