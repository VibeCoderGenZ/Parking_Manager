import java.time.LocalDateTime;

public class TicketPerMonth extends Ticket {

    private final LocalDateTime expiryDate;

    public TicketPerMonth(Vehicle vehicle) {
        super(vehicle);
        this.expiryDate = getEntryTime().plusMonths(1);
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
