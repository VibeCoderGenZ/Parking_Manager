package model;

import java.time.LocalDateTime;

public class Ticket {

    private int id;
    private final String licensePlate;
    private final String spotId;
    private final TicketType ticketType;
    private final LocalDateTime entryTime;

    private LocalDateTime exitTime;
    private LocalDateTime expiryDate;

    /**
     * Constructor để tạo một vé mới.
     */
    public Ticket(String licensePlate, String spotId, TicketType ticketType) {
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.ticketType = ticketType;
        this.entryTime = LocalDateTime.now();
    }

    /**
     * Constructor đầy đủ, dùng để tạo đối tượng từ dữ liệu database.
     */
    public Ticket(int id, String licensePlate, String spotId, TicketType ticketType,
                  LocalDateTime entryTime, LocalDateTime exitTime, LocalDateTime expiryDate) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.ticketType = ticketType;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.expiryDate = expiryDate;
    }

    // Getters
    public int getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getSpotId() { return spotId; }
    public TicketType getTicketType() { return ticketType; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public LocalDateTime getExpiryDate() { return expiryDate; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
}
