package model;

import java.time.LocalDateTime;

/**
 * Lớp Model đại diện cho một vé xe.
 * Cấu trúc của lớp này phản ánh trực tiếp các cột trong bảng 'tickets' của database.
 */
public class Ticket {

    private int id; // ID của vé, sẽ được gán bởi database (AUTO_INCREMENT)
    private final String licensePlate; // Biển số xe, không thể thay đổi
    private final String spotId;       // Vị trí đỗ xe
    private final TicketType ticketType; // Loại vé (PER_HOUR, PER_TURN, PER_MONTH)
    private final LocalDateTime entryTime; // Thời gian vào bãi
    
    private LocalDateTime exitTime;    // Thời gian ra khỏi bãi
    private LocalDateTime expiryDate;  // Ngày hết hạn (chỉ dành cho vé tháng)
    private long price;                // Giá vé cuối cùng

    // Constructor để tạo một vé mới trước khi lưu vào DB (chưa có ID)
    public Ticket(String licensePlate, String spotId, TicketType ticketType) {
        this.licensePlate = licensePlate;
        this.spotId = spotId;
        this.ticketType = ticketType;
        this.entryTime = LocalDateTime.now();
        
        // Nếu là vé tháng, đặt ngày hết hạn
        if (ticketType == TicketType.PER_MONTH) {
            this.expiryDate = this.entryTime.plusMonths(1);
        }
    }

    // Constructor đầy đủ để tạo đối tượng từ dữ liệu DB (đã có ID)
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

    // --- Getters ---
    public int getId() { return id; }
    public String getLicensePlate() { return licensePlate; }
    public String getSpotId() { return spotId; }
    public TicketType getTicketType() { return ticketType; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public long getPrice() { return price; }

    // --- Setters ---
    // ID chỉ nên được set một lần bởi DAO sau khi lưu vào DB
    public void setId(int id) { this.id = id; } 
    public void setExitTime(LocalDateTime exitTime) { this.exitTime = exitTime; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
    public void setPrice(long price) { this.price = price; }
}
