public class TicketPerTurn extends Ticket {

    public TicketPerTurn(Vehicle vehicle) {
        super(vehicle);
    }

    public long getPrice() {
        VehicleType type = getVehicle().getType();
        return switch (type) {
            case BICYCLE -> 2000; // Giá vé xe đạp
            case BIKE -> 5000; // Giá vé xe máy
            case CAR -> 30000; // Giá vé ô tô
            default -> 0;
        };
    }
}