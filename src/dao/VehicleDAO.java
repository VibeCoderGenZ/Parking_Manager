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

    public Vehicle getVehicleByLicensePlate(String licensePlate) throws SQLException {
        String sql = "SELECT v.license_plate, v.owner_name, v.owner_phone, vt.name AS vehicle_type_name " +
                     "FROM vehicles v " +
                     "JOIN vehicle_type vt ON v.vehicle_type_id = vt.id " +
                     "WHERE v.license_plate = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licensePlate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        }
        return null;
    }

    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT v.license_plate, v.owner_name, v.owner_phone, vt.name AS vehicle_type_name " +
                     "FROM vehicles v " +
                     "JOIN vehicle_type vt ON v.vehicle_type_id = vt.id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        return vehicles;
    }

    public List<Vehicle> getVehiclesByOwnerName(String ownerName) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT v.license_plate, v.owner_name, v.owner_phone, vt.name AS vehicle_type_name " +
                     "FROM vehicles v " +
                     "JOIN vehicle_type vt ON v.vehicle_type_id = vt.id " +
                     "WHERE LOWER(v.owner_name) LIKE LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + ownerName + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        }
        return vehicles;
    }

    public void addVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles(license_plate, owner_name, owner_phone, vehicle_type_id) VALUES(?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicle.getLicensePlate());
            pstmt.setString(2, vehicle.getOwnerName());
            pstmt.setString(3, vehicle.getOwnerPhone());
            pstmt.setInt(4, vehicle.getType().ordinal() + 1); 
            pstmt.executeUpdate();
        }
    }

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

    public void deleteVehicle(String licensePlate) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE license_plate = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, licensePlate);
            pstmt.executeUpdate();
        }
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        String licensePlate = rs.getString("license_plate");
        String ownerName = rs.getString("owner_name");
        String ownerPhone = rs.getString("owner_phone");
        VehicleType type = VehicleType.valueOf(rs.getString("vehicle_type_name"));
        return new Vehicle(ownerName, ownerPhone, licensePlate, type);
    }
}
