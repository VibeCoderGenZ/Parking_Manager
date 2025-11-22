package logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.io.IOException;

public class ParkingLot {
    private DataManager dataManager;
    private ArrayList<Vehicle> vehicles;
    private ArrayList<ParkingSpot> spots;
    private ArrayList<Ticket> tickets;

    public ParkingLot() throws IOException {
        this.dataManager = new DataManager();
        this.vehicles = dataManager.loadVehicles();
        this.spots = dataManager.loadSpots();
        this.tickets = dataManager.loadTickets();
    }

    /*
     * **************************************************************************
     * *
     * Method for Vehicles
     * Tìm Kiếm
     * Thêm/Xóa
     * *
     * **************************************************************************
     */

    // Trả về danh sách phương tiện
    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    // Tìm xe theo biển số xe
    public Vehicle getVehicleByLicensePlate(String licensePlate) {
        if (licensePlate == null)
            return null;
        String target = licensePlate.trim();
        for (Vehicle v : vehicles) {
            String plate = v.getLicensePlate();
            if (plate != null && plate.equalsIgnoreCase(target)) {
                return v;
            }
        }
        return null;
    }

    // Tìm tất cả xe theo họ tên chủ xe (chuẩn hóa, không phân biệt hoa thường)
    public ArrayList<Vehicle> getVehicleByOwnerName(String ownerName) {
        ArrayList<Vehicle> result = new ArrayList<>();
        if (ownerName == null)
            return result;
        String trimmed = ownerName.trim();
        if (trimmed.isEmpty())
            return result;
        String target = Vehicle.chuanHoaHoTen(trimmed);
        for (Vehicle v : vehicles) {
            String name = v.getOwnerName();
            if (name != null && name.equalsIgnoreCase(target)) {
                result.add(v);
            }
        }
        return result;
    }

    // Thêm/Xóa phương tiện
    public boolean addVehicle(String licensePlate, VehicleType type, String ownerName, String ownerPhone) {
        if (licensePlate == null || type == null || ownerName == null || ownerPhone == null)
            return false;
        String lp = licensePlate.trim();
        if (lp.isEmpty())
            return false;
        if (ownerName.trim().isEmpty())
            return false;
        if (getVehicleByLicensePlate(lp) != null)
            return false;
        vehicles.add(new Vehicle(lp, type, ownerName, ownerPhone));
        return true; // Thêm thành công
    }

    public boolean removeVehicle(String licensePlate) {
        if (licensePlate == null)
            return false;
        Vehicle v = getVehicleByLicensePlate(licensePlate);
        if (v == null)
            return false;
        // Chặn nếu đang đỗ
        if (getSpotByLicensePlate(licensePlate) != null)
            return false;
        // Chặn nếu còn vé active (dữ liệu lệch)
        Ticket t = getTicketByLicensePlate(licensePlate);
        if (t != null && t.getExitTime() == null)
            return false;
        vehicles.remove(v);
        return true;
    }

    /*
     * **************************************************************************
     * *
     * Methods for Spots
     * Tìm kiếm
     * Thêm/xóa
     * *
     * **************************************************************************
     */

    // Trả về danh sách vị trí đỗ
    public ArrayList<ParkingSpot> getSpots() {
        return spots;
    }

    // Tìm vị trí dựa theo tên
    public ParkingSpot getSpotBySpotID(int spotID) {
        for (ParkingSpot spot : spots) {
            if (spot.getSpotID() == spotID) {
                return spot;
            }
        }
        return null;
    }

    // Tìm tất cả chỗ đỗ phù hợp với loại xe
    public ArrayList<ParkingSpot> getSpotByAllowedVehicle(VehicleType type) {
        ArrayList<ParkingSpot> result = new ArrayList<>();
        if (type == null)
            return result;
        for (ParkingSpot spot : spots) {
            if (spot.getAllowedType() == type) {
                result.add(spot);
            }
        }
        return result;
    }

    // Tìm tất cả chỗ đỗ theo trạng thái (occupied = true: đang có xe, false: trống)
    public ArrayList<ParkingSpot> getSpotsByOccupancy(boolean occupied) {
        ArrayList<ParkingSpot> result = new ArrayList<>();
        for (ParkingSpot spot : spots) {
            if (spot.isOccupied() == occupied) {
                result.add(spot);
            }
        }
        return result;
    }

    // danh sách chỗ đang có xe
    public ArrayList<ParkingSpot> getOccupiedSpots() {
        return getSpotsByOccupancy(true);
    }

    // danh sách chỗ trống
    public ArrayList<ParkingSpot> getEmptySpots() {
        return getSpotsByOccupancy(false);
    }

    // Tìm chỗ đỗ trống đầu tiên phù hợp với loại xe
    private ParkingSpot findFirstAvailableSpot(VehicleType type) {
        if (type == null)
            return null;
        for (ParkingSpot spot : spots) {
            if (!spot.isOccupied() && spot.getAllowedType() == type) {
                return spot;
            }
        }
        return null;
    }

    // Tìm vị trí đang có xe theo biển số xe
    public ParkingSpot getSpotByLicensePlate(String licensePlate) {
        if (licensePlate == null)
            return null;
        String target = licensePlate.trim();
        for (ParkingSpot spot : getOccupiedSpots()) {
            String plate = spot.getLicensePlate();
            if (plate != null && plate.equalsIgnoreCase(target)) {
                return spot;
            }
        }
        return null;
    }

    // Thêm/Xóa vị trí
    public boolean addSpot(int spotID, VehicleType allowedType, String licensePlate, boolean occupied) {
        if (allowedType == null)
            return false;
        if (getSpotBySpotID(spotID) != null)
            return false;
        String plate = (licensePlate == null) ? null : licensePlate.trim();
        if (occupied) {
            if (plate == null || plate.isEmpty())
                return false;
            // Kiểm tra xe có tồn tại không
            Vehicle v = getVehicleByLicensePlate(plate);
            if (v == null)
                return false;
            // Kiểm tra loại xe có khớp không
            if (v.getType() != allowedType)
                return false;
            // Kiểm tra không trùng biển số với spot khác
            if (getSpotByLicensePlate(plate) != null)
                return false;
        } else {
            plate = null;
        }
        spots.add(new ParkingSpot(spotID, allowedType, plate, occupied));
        return true;
    }

    /*
     * **************************************************************************
     * *
     * Method for Tickets
     * Tìm Kiếm
     * Thêm
     * Ra/Vào
     * *
     * **************************************************************************
     */

    // Trả về danh sách vé đỗ
    public ArrayList<Ticket> getTickets() {
        return tickets;
    }

    // Trả về danh sách vé đang hoạt động (chưa ra)
    public ArrayList<Ticket> getActiveTicket() {
        ArrayList<Ticket> result = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getExitTime() == null) {
                result.add(ticket);
            }
        }
        return result;
    }

    // Trả về danh sách vé đã sử dụng (đã ra)
    public ArrayList<Ticket> getUsedTicket() {
        ArrayList<Ticket> result = new ArrayList<>();
        for (Ticket ticket : tickets) {
            if (ticket.getExitTime() != null) {
                result.add(ticket);
            }
        }
        return result;
    }

    // Tìm vé theo mã vé
    public Ticket getTicketByTicketID(int ticketID) {
        for (Ticket ticket : tickets) {
            if (ticket.getTicketID() == ticketID) {
                return ticket;
            }
        }
        return null;
    }

    // Tìm vé theo biển số xe
    public Ticket getTicketByLicensePlate(String licensePlate) {
        if (licensePlate == null)
            return null;
        String target = licensePlate.trim();
        Ticket used = null;
        for (Ticket ticket : tickets) {
            String plate = ticket.getLicensePlate();
            if (plate != null && plate.equalsIgnoreCase(target)) {
                if (ticket.getExitTime() == null) {
                    return ticket; // ưu tiên vé active
                } else if (used == null) {
                    used = ticket;
                }
            }
        }
        return used;
    }

    // Thêm vé mới
    public boolean addTicket(int ticketID, int spotID, String licensePlate, LocalDateTime entryTime,
            LocalDateTime exitTime) {
        if (licensePlate == null || entryTime == null)
            return false;
        String plate = licensePlate.trim();
        if (plate.isEmpty())
            return false;
        if (getTicketByTicketID(ticketID) != null)
            return false;
        // Kiểm tra xe có tồn tại không
        if (getVehicleByLicensePlate(plate) == null)
            return false;
        ParkingSpot spot = getSpotBySpotID(spotID);
        if (spot == null)
            return false;
        // Nếu vé active thì spot phải occupied và khớp biển số
        if (exitTime == null) {
            if (!spot.isOccupied())
                return false;
            String spotPlate = spot.getLicensePlate();
            if (spotPlate == null || !spotPlate.equalsIgnoreCase(plate))
                return false;
        }
        tickets.add(new Ticket(ticketID, spotID, plate, entryTime, exitTime));
        return true;
    }

    // Đỗ phương tiện
    public boolean parkVehicleAuto(String licensePlate) {
        if (licensePlate == null || licensePlate.isBlank())
            return false;
        String plate = licensePlate.trim();
        if (plate.isEmpty())
            return false;
        Vehicle v = getVehicleByLicensePlate(plate);
        if (v == null)
            return false;
        if (getSpotByLicensePlate(plate) != null)
            return false;
        Ticket t = getTicketByLicensePlate(plate);
        if (t != null && t.getExitTime() == null)
            return false;
        ParkingSpot spot = findFirstAvailableSpot(v.getType());
        if (spot == null)
            return false;
        spot.setOccupied(true);
        spot.setLicensePlate(plate);
        int newTicketID = tickets.size() + 1;
        if (!addTicket(newTicketID, spot.getSpotID(), plate, LocalDateTime.now(), null)) {
            spot.setOccupied(false);
            spot.setLicensePlate(null);
            return false;
        }
        return true;
    }

    // Lấy phương tiện ra
    public boolean retrieveVehicle(String licensePlate) {
        if (licensePlate == null || licensePlate.isBlank())
            return false;
        String plate = licensePlate.trim();
        if (plate.isEmpty())
            return false;
        ParkingSpot spot = getSpotByLicensePlate(plate);
        Ticket ticket = getTicketByLicensePlate(plate);
        if (spot == null || ticket == null)
            return false;
        if (!spot.isOccupied())
            return false;
        if (ticket.getExitTime() != null)
            return false;
        spot.setOccupied(false);
        spot.setLicensePlate(null);
        ticket.setExitTime(LocalDateTime.now());
        return true;
    }

    // Các method reset
    public void resetVehicles() {
        vehicles.clear();
    }

    public void resetTickets() {
        tickets.clear();
    }

    public void resetSpots() {
        spots.clear();
    }

    // Lưu dữ liệu
    public void saveData() throws IOException {
        dataManager.saveData(vehicles, spots, tickets);
    }
}