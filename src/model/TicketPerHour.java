package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class TicketPerHour extends Ticket {

    public TicketPerHour(Vehicle vehicle) {
        super(vehicle);
    }

    private long getHourlyRate() {
        VehicleType type = getVehicle().getType();
        return switch (type) {
            case BICYCLE -> 500;
            case BIKE -> 1000;
            case CAR -> 10000;
            default -> 0;
        };
    }

    @Override
    public void calculateFinalPrice() {
        if (getExitTime() == null) {
            // Chỉ tính giá khi đã có thời gian ra
            return;
        }

        Duration duration = Duration.between(getEntryTime(), getExitTime());
        long totalMinutes = duration.toMinutes();

        long hours = (long) Math.ceil(totalMinutes / 60.0);

        long finalPrice = hours * getHourlyRate();
        setPrice(finalPrice); // Cập nhật giá vào trường price của lớp cha
    }
}
