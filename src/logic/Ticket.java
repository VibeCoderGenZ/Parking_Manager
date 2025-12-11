package logic;

import java.time.LocalDateTime;

public class Ticket {
    private final int ticketID;
    private final int spotID;
    private final String licensePlate;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;

    public Ticket(int ticketID, int spotID, String licensePlate, LocalDateTime entryTime, LocalDateTime exitTime) {
        this.ticketID = ticketID;
        this.spotID = spotID;
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
    }

    public int getTicketID() {
        return ticketID;
    }

    public int getSpotID() {
        return spotID;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    @Override
    public String toString() {
        return ticketID + "," + spotID + "," + licensePlate + "," + entryTime + "," + exitTime;
    }

}
