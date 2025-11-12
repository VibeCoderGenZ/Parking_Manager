package controller;

import dao.ParkingSpotDAO;
import dao.TicketDAO;
import dao.VehicleDAO;
import dao.ParkingZoneDAO;
import model.*;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class ParkingController {

    private final VehicleDAO vehicleDAO;
    private final TicketDAO ticketDAO;
    private final ParkingSpotDAO parkingSpotDAO;
    private final ParkingZoneDAO parkingZoneDAO;

    public ParkingController() {
        this.vehicleDAO = new VehicleDAO();
        this.ticketDAO = new TicketDAO();
        this.parkingSpotDAO = new ParkingSpotDAO();
        this.parkingZoneDAO = new ParkingZoneDAO();
    }

    public void addNewVehicle(Vehicle vehicle) throws SQLException {
        vehicleDAO.addVehicle(vehicle);
    }

    public Ticket registerMonthlyTicket(Vehicle vehicle, int numberOfMonths) throws SQLException {
        if (vehicleDAO.getVehicleByLicensePlate(vehicle.getLicensePlate()) == null) {
            vehicleDAO.addVehicle(vehicle);
        }
        Ticket monthlyTicket = new Ticket(vehicle.getLicensePlate(), null, TicketType.PER_MONTH);
        long pricePerMonth = getMonthlyPrice(vehicle.getType());
        monthlyTicket.setPrice(pricePerMonth * numberOfMonths);
        monthlyTicket.setExpiryDate(LocalDateTime.now().plusMonths(numberOfMonths));
        ticketDAO.addTicket(monthlyTicket);
        return monthlyTicket;
    }

    public Ticket checkIn(String licensePlate, String spotId) throws SQLException, ParkingException {
        if (ticketDAO.getActiveTicketByLicensePlate(licensePlate) != null) {
            throw new ParkingException("Xe có biển số " + licensePlate + " đã ở trong bãi.");
        }
        boolean isMonthly = ticketDAO.findActiveMonthlyTicket(licensePlate) != null;
        ParkingSpot spot = parkingSpotDAO.getParkingSpotById(spotId);
        if (spot == null) {
            throw new ParkingException("Mã chỗ đỗ '" + spotId + "' không tồn tại.");
        }
        if (spot.isOccupied()) {
            throw new ParkingException("Chỗ đỗ '" + spotId + "' đã có xe.");
        }
        Vehicle vehicle = vehicleDAO.getVehicleByLicensePlate(licensePlate);
        if (vehicle == null) {
            throw new ParkingException("Xe vãng lai chưa có thông tin. Vui lòng đăng ký xe mới.");
        }
        ParkingZone zone = parkingZoneDAO.getZoneById(spot.getZoneId());
        if (zone == null) {
            throw new ParkingException("Lỗi nội bộ: Không tìm thấy thông tin khu vực.");
        }
        if (vehicle.getType() != zone.getAllowedVehicleType()) {
            throw new ParkingException(String.format("Loại xe không hợp lệ! Xe %s (%s) không thể đỗ ở khu vực %s (dành cho %s).",
                    licensePlate, vehicle.getType(), zone.getName(), zone.getAllowedVehicleType()));
        }
        TicketType ticketTypeForThisTurn = TicketType.PER_TURN;
        Ticket newTurnTicket = new Ticket(licensePlate, spotId, ticketTypeForThisTurn);
        newTurnTicket.setPrice(isMonthly ? 0 : calculatePrice(newTurnTicket));
        ticketDAO.addTicket(newTurnTicket);
        spot.setOccupied(true);
        spot.setLicensePlate(licensePlate);
        parkingSpotDAO.updateParkingSpot(spot);
        return newTurnTicket;
    }

    public Ticket checkOut(String licensePlate) throws SQLException, ParkingException {
        Ticket ticket = ticketDAO.getActiveTicketByLicensePlate(licensePlate);
        if (ticket == null) {
            throw new ParkingException("Không tìm thấy xe có biển số " + licensePlate + " trong bãi.");
        }
        ticket.setExitTime(LocalDateTime.now());
        ticketDAO.updateTicket(ticket);
        ParkingSpot spot = parkingSpotDAO.getParkingSpotById(ticket.getSpotId());
        if (spot != null) {
            spot.setOccupied(false);
            spot.setLicensePlate(null);
            parkingSpotDAO.updateParkingSpot(spot);
        }
        return ticket;
    }

    private long calculatePrice(Ticket ticket) throws SQLException, ParkingException {
        Vehicle vehicle = vehicleDAO.getVehicleByLicensePlate(ticket.getLicensePlate());
        if (vehicle == null) {
            throw new ParkingException("Lỗi nội bộ: Không tìm thấy thông tin xe.");
        }
        return switch (ticket.getTicketType()) {
            case PER_TURN -> switch (vehicle.getType()) {
                case BICYCLE -> 2000;
                case BIKE -> 5000;
                case CAR -> 30000;
            };
            case PER_MONTH -> getMonthlyPrice(vehicle.getType());
        };
    }

    private long getMonthlyPrice(VehicleType type) {
        return switch (type) {
            case BICYCLE -> 50000;
            case BIKE -> 100000;
            case CAR -> 1000000;
        };
    }

    public Vehicle getVehicle(String licensePlate) throws SQLException {
        return vehicleDAO.getVehicleByLicensePlate(licensePlate);
    }

    public void addSpotsToZone(ParkingZone zone, int numberOfSpots) throws SQLException {
        int maxSpotNumber = parkingSpotDAO.getParkingSpotsByZone(zone.getId()).stream().map(spot -> spot.getId().substring(spot.getId().lastIndexOf('-') + 1)).mapToInt(Integer::parseInt).max().orElse(0);
        for (int i = 1; i <= numberOfSpots; i++) {
            String spotId = zone.getName() + "-" + (maxSpotNumber + i);
            ParkingSpot newSpot = new ParkingSpot(spotId, zone.getId());
            parkingSpotDAO.addParkingSpot(newSpot);
        }
    }

    public void deleteParkingSpot(String spotId) throws SQLException, ParkingException {
        ParkingSpot spot = parkingSpotDAO.getParkingSpotById(spotId);
        if (spot == null) throw new ParkingException("Chỗ đỗ không tồn tại.");
        if (spot.isOccupied()) throw new ParkingException("Không thể xóa chỗ đỗ đang có xe.");
        if (ticketDAO.hasHistoryForSpot(spotId))
            throw new ParkingException("Không thể xóa chỗ đỗ đã có trong lịch sử.");
        parkingSpotDAO.deleteParkingSpot(spotId);
    }

    public void createNewZoneAndSpots(String zoneName, VehicleType allowedType, int numberOfSpots) throws SQLException, ParkingException {
        // Kiểm tra xem tên khu vực đã tồn tại chưa
        // Đây là một kiểm tra đơn giản, có thể cần phức tạp hơn nếu có nhiều khu vực cùng tên
        List<ParkingZone> existingZones = parkingZoneDAO.getAllParkingZones();
        for (ParkingZone zone : existingZones) {
            if (zone.getName().equalsIgnoreCase(zoneName)) {
                throw new ParkingException("Tên khu vực '" + zoneName + "' đã tồn tại.");
            }
        }

        ParkingZone newZone = new ParkingZone(zoneName, allowedType, numberOfSpots);
        parkingZoneDAO.addParkingZone(newZone);
        for (int i = 1; i <= numberOfSpots; i++) {
            String spotId = zoneName + "-" + i;
            ParkingSpot newSpot = new ParkingSpot(spotId, newZone.getId());
            parkingSpotDAO.addParkingSpot(newSpot);
        }
    }
}
