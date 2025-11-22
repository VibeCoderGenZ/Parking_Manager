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

    // Hàm dùng chung (Generic) để ghi danh sách bất kỳ
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
                    list.add(new Vehicle(parts[0], VehicleType.valueOf(parts[1]), parts[2], parts[3]));
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
                    String plate = parts[2].equals("null") ? null : parts[2];
                    list.add(new ParkingSpot(Integer.parseInt(parts[0]), VehicleType.valueOf(parts[1]), plate,
                            Boolean.parseBoolean(parts[3])));
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