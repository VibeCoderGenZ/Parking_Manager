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
    private long price;

    public Ticket(String licensePlate, String spotId, TicketType ticketType) {
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.ticketType = ticketType;
        this.entryTime = LocalDateTime.now();
        
        if (ticketType == TicketType.PER_MONTH) {
            this.expiryDate = this.entryTime.plusMonths(1);
        }
    }

    public Ticket(int id, String licensePlate, String spotId, TicketType ticketType, 
                  LocalDateTime entryTime, LocalDateTime exitTime, LocalDateTime expiryDate, long price) {
        this.id = id;
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.ticketType = ticketType;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.expiryDate = expiryDate;
        this.price = price;
    }

    public int getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getSpotId() { return spotId; }
    public TicketType getTicketType() { return ticketType; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public long getPrice() { return price; }

    public void setId(int id) { this.id = id; } 
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public void setPrice(long price) { this.price = price; }
}
