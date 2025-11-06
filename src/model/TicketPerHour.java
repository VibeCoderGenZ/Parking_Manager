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
            case BICYCLE -> 500; // Giá mỗi giờ cho xe đạp
            case BIKE -> 1000; // Giá mỗi giờ cho xe máy
            case CAR -> 10000; // Giá mỗi giờ cho ô tô
            default -> 0;
        };
    }

    public long getPrice() {
        LocalDateTime finalTime;
        if (getExitTime() == null) {
            finalTime = LocalDateTime.now();
        } else {
            finalTime = getExitTime();
        }

        Duration duration = Duration.between(getEntryTime(), finalTime);
        long totalMinutes = duration.toMinutes();

        long hours = (long) Math.ceil(totalMinutes / 60.0);

        return hours * getHourlyRate();
    }

}
