package model;

import java.time.LocalDateTime;

public abstract class Ticket {

    private static int nextId = 1;
    private final int id;
    private final Vehicle vehicle;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private long price; // Thêm trường price

    public Ticket(Vehicle vehicle) {
        this.id = nextId++;
        this.vehicle = vehicle;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.price = 0; // Khởi tạo giá bằng 0
    }

    // --- Các getters cũ ---
    public int getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public long getPrice() { return price; }

    public void setExitTime() {
        this.exitTime = LocalDateTime.now();
    }
    
    public void setPrice(long price) {
        this.price = price;
    }

    // Phương thức trừu tượng để các lớp con định nghĩa cách tính giá
    public abstract void calculateFinalPrice();
}
