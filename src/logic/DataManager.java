package logic;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class DataManager {
    // Đường dẫn đến file lưu trữ thông tin xe
    private static final String VEHICLE_FILE = "data/vehicles.csv";
    // Đường dẫn đến file lưu trữ thông tin chỗ đỗ
    private static final String SPOT_FILE = "data/spots.csv";
    // Đường dẫn đến file lưu trữ thông tin vé
    private static final String TICKET_FILE = "data/tickets.csv";

    public DataManager() {
        // Đảm bảo thư mục "data" tồn tại để tránh lỗi khi ghi file
        new File("data").mkdirs();
    }

    // --- PHẦN GHI (SAVE) ---
    // Lưu toàn bộ dữ liệu (xe, chỗ đỗ, vé) vào các file CSV tương ứng
    public void saveData(ArrayList<Vehicle> vehicles, ArrayList<ParkingSpot> spots, ArrayList<Ticket> tickets)
            throws IOException {
        saveList(VEHICLE_FILE, vehicles); // Lưu danh sách xe
        saveList(SPOT_FILE, spots); // Lưu danh sách chỗ đỗ
        saveList(TICKET_FILE, tickets); // Lưu danh sách vé
    }

    // Hàm generic (dùng chung) để ghi một danh sách bất kỳ vào file
    // <T> là kiểu dữ liệu của các phần tử trong danh sách (Vehicle, ParkingSpot,
    // Ticket)
    private <T> void saveList(String filename, ArrayList<T> list) throws IOException {
        // Sử dụng BufferedWriter để ghi file hiệu quả hơn
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        for (T item : list) {
            // Gọi phương thức toString() của đối tượng để lấy chuỗi định dạng CSV
            bw.write(item.toString());
            // Xuống dòng sau mỗi bản ghi
            bw.newLine();
        }
        // Đóng luồng ghi để giải phóng tài nguyên và đảm bảo dữ liệu được lưu
        bw.close();
    }

    // --- PHẦN ĐỌC (LOAD) ---

    // Đọc danh sách xe từ file CSV
    public ArrayList<Vehicle> loadVehicles() throws IOException {
        ArrayList<Vehicle> list = new ArrayList<>();
        File file = new File(VEHICLE_FILE);

        // Nếu file không tồn tại, trả về danh sách rỗng
        if (!file.exists())
            return list;

        // Sử dụng try-with-resources để tự động đóng BufferedReader sau khi dùng xong
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            // Đọc từng dòng trong file cho đến khi hết
            while ((line = br.readLine()) != null) {
                // Bỏ qua các dòng trống
                if (line.trim().isEmpty())
                    continue;

                // Tách dòng thành các phần bằng dấu phẩy (định dạng CSV)
                String[] parts = line.split(",");

                // Kiểm tra xem dòng có đủ dữ liệu không (ít nhất 4 trường cho Vehicle)
                if (parts.length >= 4) {
                    // Cắt bỏ khoảng trắng thừa ở đầu/cuối mỗi phần dữ liệu
                    String plate = parts[0].trim(); // Biển số
                    VehicleType type = VehicleType.valueOf(parts[1].trim()); // Loại xe (enum)
                    String owner = parts[2].trim(); // Chủ xe
                    String phone = parts[3].trim(); // Số điện thoại

                    // Tạo đối tượng Vehicle mới và thêm vào danh sách
                    list.add(new Vehicle(plate, type, owner, phone));
                }
            }
        }
        return list;
    }

    // Đọc danh sách chỗ đỗ từ file CSV
    public ArrayList<ParkingSpot> loadSpots() throws IOException {
        ArrayList<ParkingSpot> list = new ArrayList<>();
        File file = new File(SPOT_FILE);

        // Nếu file không tồn tại, trả về danh sách rỗng
        if (!file.exists())
            return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    // Phân tích dữ liệu từ chuỗi sang các kiểu dữ liệu tương ứng
                    int id = Integer.parseInt(parts[0].trim()); // ID chỗ đỗ
                    VehicleType type = VehicleType.valueOf(parts[1].trim()); // Loại xe được phép đỗ
                    String rawPlate = parts[2].trim(); // Biển số xe đang đỗ (nếu có)

                    // Xử lý trường hợp biển số là "null" (chuỗi) thành null (giá trị)
                    String plate = rawPlate.equals("null") ? null : rawPlate;

                    boolean occupied = Boolean.parseBoolean(parts[3].trim()); // Trạng thái có xe hay không

                    // Tạo đối tượng ParkingSpot và thêm vào danh sách
                    list.add(new ParkingSpot(id, type, plate, occupied));
                }
            }
        }
        return list;
    }

    // Đọc danh sách vé từ file CSV
    public ArrayList<Ticket> loadTickets() throws IOException {
        ArrayList<Ticket> list = new ArrayList<>();
        File file = new File(TICKET_FILE);

        // Nếu file không tồn tại, trả về danh sách rỗng
        if (!file.exists())
            return list;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    // Xử lý thời gian vào, nếu là "null" thì gán giá trị null
                    LocalDateTime entry = parts[3].equals("null") ? null : LocalDateTime.parse(parts[3]);
                    // Xử lý thời gian ra, nếu là "null" thì gán giá trị null
                    LocalDateTime exit = parts[4].equals("null") ? null : LocalDateTime.parse(parts[4]);

                    // Tạo đối tượng Ticket từ dữ liệu đọc được
                    // parts[0]: ID vé, parts[1]: ID chỗ đỗ, parts[2]: Biển số, parts[3]: Thời gian
                    // vào
                    list.add(new Ticket(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), parts[2],
                            entry, exit));
                }
            }
        }
        return list;
    }
}
