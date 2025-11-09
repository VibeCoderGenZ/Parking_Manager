package model;

import java.time.LocalDateTime;

public class TicketPerMonth extends Ticket {

    private final LocalDateTime expiryDate;

    public TicketPerMonth(Vehicle vehicle) {
        super(vehicle);
        this.expiryDate = getEntryTime().plusMonths(1);
        calculateFinalPrice(); // Giá vé tháng là cố định, tính luôn khi tạo
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    @Override
    public void calculateFinalPrice() {
        VehicleType type = getVehicle().getType();
        long finalPrice = switch (type) {
            case BICYCLE -> 50000;
            case BIKE -> 100000;
            case CAR -> 1000000;
            default -> 0;
        };
        setPrice(finalPrice);
    }
}
