package dao;

import model.Ticket;
import model.TicketType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO {

    public boolean hasHistoryForSpot(String spotId) throws SQLException {
        String sql = "SELECT 1 FROM tickets WHERE spot_id = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, spotId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Ticket findActiveMonthlyTicket(String licensePlate) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE license_plate = ? AND ticket_type = 'PER_MONTH' AND expiry_date > NOW() ORDER BY expiry_date DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, licensePlate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }
        return null;
    }

    public void addTicket(Ticket ticket) throws SQLException {
        String sql = "INSERT INTO tickets(license_plate, spot_id, entry_time, ticket_type, expiry_date, price) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ticket.getLicensePlate());
            pstmt.setString(2, ticket.getSpotId());
            pstmt.setObject(3, ticket.getEntryTime());
            pstmt.setString(4, ticket.getTicketType().name());
            pstmt.setObject(5, ticket.getExpiryDate());
            pstmt.setLong(6, ticket.getPrice());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        ticket.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
    }

    public void updateTicket(Ticket ticket) throws SQLException {
        String sql = "UPDATE tickets SET exit_time = ?, price = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, ticket.getExitTime());
            pstmt.setLong(2, ticket.getPrice());
            pstmt.setInt(3, ticket.getId());
            pstmt.executeUpdate();
        }
    }

    public Ticket getActiveTicketByLicensePlate(String licensePlate) throws SQLException {
        String sql = "SELECT * FROM tickets WHERE license_plate = ? AND exit_time IS NULL";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, licensePlate);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTicket(rs);
                }
            }
        }
        return null;
    }

    public List<Ticket> getAllTickets() throws SQLException {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets ORDER BY entry_time DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tickets.add(mapResultSetToTicket(rs));
            }
        }
        return tickets;
    }

    private Ticket mapResultSetToTicket(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String licensePlate = rs.getString("license_plate");
        String spotId = rs.getString("spot_id");
        TicketType ticketType = TicketType.valueOf(rs.getString("ticket_type"));
        LocalDateTime entryTime = rs.getObject("entry_time", LocalDateTime.class);
        LocalDateTime exitTime = rs.getObject("exit_time", LocalDateTime.class);
        LocalDateTime expiryDate = rs.getObject("expiry_date", LocalDateTime.class);
        long price = rs.getLong("price");
        return new Ticket(id, licensePlate, spotId, ticketType, entryTime, exitTime, expiryDate, price);
    }
}
