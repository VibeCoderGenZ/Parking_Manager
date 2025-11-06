package model;

import java.time.LocalDateTime;

public class Ticket {

    private static int nextId = 1;
    private final int id;
    private final Vehicle vehicle;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket(Vehicle vehicle) {
        this.id = nextId++;
        this.vehicle = vehicle;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
    }

    public int getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime() {
        this.exitTime = LocalDateTime.now();
    }

}
