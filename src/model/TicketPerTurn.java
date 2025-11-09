package model;

public class TicketPerTurn extends Ticket {

    public TicketPerTurn(Vehicle vehicle) {
        super(vehicle);
    }

    @Override
    public void calculateFinalPrice() {
        VehicleType type = getVehicle().getType();
        long finalPrice = switch (type) {
            case BICYCLE -> 2000;
            case BIKE -> 5000;
            case CAR -> 30000;
            default -> 0;
        };
        setPrice(finalPrice);
    }
}
