package logic;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DataManager {
    private static final String VEHICLE_FILE = "data/vehicles.csv";
    private static final String SPOT_FILE = "data/spots.csv";
    private static final String TICKET_FILE = "data/tickets.csv";

    public DataManager() {
        // Đảm bảo thư mục data tồn tại
        new File("data").mkdirs();
    }

    // --- PHẦN GHI (SAVE) ---
    public void saveData(ArrayList<Vehicle> vehicles, ArrayList<ParkingSpot> spots, ArrayList<Ticket> tickets)
            throws IOException {
        saveList(VEHICLE_FILE, vehicles);
        saveList(SPOT_FILE, spots);
        saveList(TICKET_FILE, tickets);
    }

    // Hàm dùng chung để ghi danh sách bất kỳ
    private <T> void saveList(String filename, ArrayList<T> list) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        for (T item : list) {
            bw.write(item.toString());
            bw.newLine();
        }
        bw.close();
    }

    // --- PHẦN ĐỌC (LOAD) ---

    public ArrayList<Vehicle> loadVehicles() throws IOException {
        ArrayList<Vehicle> list = new ArrayList<>();
        File file = new File(VEHICLE_FILE);
        if (!file.exists())
            return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    // Thêm .trim() vào tất cả các phần để xóa khoảng trắng thừa
                    String plate = parts[0].trim();
                    VehicleType type = VehicleType.valueOf(parts[1].trim());
                    String owner = parts[2].trim();
                    String phone = parts[3].trim();

                    list.add(new Vehicle(plate, type, owner, phone));
                }
            }
        }
        return list;
    }

    public ArrayList<ParkingSpot> loadSpots() throws IOException {
        ArrayList<ParkingSpot> list = new ArrayList<>();
        File file = new File(SPOT_FILE);
        if (!file.exists())
            return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    // SỬA: Thêm .trim()
                    int id = Integer.parseInt(parts[0].trim());
                    VehicleType type = VehicleType.valueOf(parts[1].trim());
                    String rawPlate = parts[2].trim();
                    String plate = rawPlate.equals("null") ? null : rawPlate;
                    boolean occupied = Boolean.parseBoolean(parts[3].trim());

                    list.add(new ParkingSpot(id, type, plate, occupied));
                }
            }
        }
        return list;
    }

    public ArrayList<Ticket> loadTickets() throws IOException {
        ArrayList<Ticket> list = new ArrayList<>();
        File file = new File(TICKET_FILE);
        if (!file.exists())
            return list;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    LocalDateTime exit = parts[4].equals("null") ? null : LocalDateTime.parse(parts[4]);
                    list.add(new Ticket(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2],
                            LocalDateTime.parse(parts[3]), exit));
                }
            }
        }
        return list;
    }
}
