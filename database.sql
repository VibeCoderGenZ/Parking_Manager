-- =================================================================
-- Script khởi tạo cơ sở dữ liệu cho ứng dụng Parking Manager
-- Phiên bản chuẩn hóa, sử dụng Khóa thay thế (Surrogate Key) cho vehicle_type.
-- =================================================================

-- Xóa các bảng theo thứ tự ngược lại của sự phụ thuộc
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS parking_spots;
DROP TABLE IF EXISTS parking_zones;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS vehicle_type;
DROP TABLE IF EXISTS users;

-- =================================================================
-- Bảng `users`: Lưu trữ thông tin đăng nhập
-- =================================================================
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- =================================================================
-- Bảng `vehicle_type`: Bảng tham chiếu cho các loại phương tiện
-- Sử dụng Khóa thay thế (id) để tối ưu hiệu năng và bảo trì.
-- =================================================================
CREATE TABLE vehicle_type (
    id INT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE -- 'BICYCLE', 'BIKE', 'CAR'
);

-- =================================================================
-- Bảng `vehicles`: Lưu trữ thông tin về các phương tiện
-- `vehicle_type_id` là khóa ngoại tham chiếu đến bảng `vehicle_type`.
-- =================================================================
CREATE TABLE vehicles (
    license_plate VARCHAR(20) PRIMARY KEY NOT NULL,
    owner_name VARCHAR(255) NOT NULL,
    owner_phone VARCHAR(20),
    vehicle_type_id INT NOT NULL,
    FOREIGN KEY (vehicle_type_id) REFERENCES vehicle_type(id)
);

-- =================================================================
-- Bảng `parking_zones`: Lưu trữ thông tin về các khu vực đỗ xe
-- `allowed_vehicle_type` lưu trực tiếp tên loại xe để đơn giản hóa logic của khu vực.
-- =================================================================
CREATE TABLE parking_zones (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    allowed_vehicle_type VARCHAR(50) NOT NULL, -- 'BICYCLE', 'BIKE', 'CAR'
    number_of_spots INT NOT NULL DEFAULT 0
);

-- =================================================================
-- Bảng `parking_spots`: Lưu trữ thông tin về từng chỗ đỗ xe cụ thể
-- =================================================================
CREATE TABLE parking_spots (
    id VARCHAR(50) PRIMARY KEY NOT NULL,
    zone_id INT NOT NULL,
    is_occupied BOOLEAN NOT NULL DEFAULT FALSE,
    license_plate VARCHAR(20),
    FOREIGN KEY (zone_id) REFERENCES parking_zones(id)
);

-- =================================================================
-- Bảng `tickets`: Lưu trữ lịch sử ra vào và thông tin vé tháng
-- =================================================================
CREATE TABLE tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL,
    spot_id VARCHAR(50),
    ticket_type VARCHAR(50) NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    expiry_date TIMESTAMP,
    FOREIGN KEY (license_plate) REFERENCES vehicles(license_plate),
    FOREIGN KEY (spot_id) REFERENCES parking_spots(id)
);

-- =================================================================
-- Thêm dữ liệu mẫu
-- =================================================================

-- Thêm tài khoản admin mặc định
INSERT INTO users (username, password) VALUES ('admin', 'admin');

-- Thêm các loại xe (ID phải khớp với thứ tự của Enum VehicleType + 1)
INSERT INTO vehicle_type (id, name) VALUES (1, 'BICYCLE');
INSERT INTO vehicle_type (id, name) VALUES (2, 'BIKE');
INSERT INTO vehicle_type (id, name) VALUES (3, 'CAR');

-- Tạo 2 khu vực đỗ xe
INSERT INTO parking_zones (name, allowed_vehicle_type, number_of_spots) VALUES ('Khu A (Xe máy)', 'BIKE', 10);
INSERT INTO parking_zones (name, allowed_vehicle_type, number_of_spots) VALUES ('Khu B (Ô tô)', 'CAR', 5);

-- Tạo chỗ đỗ cho Khu A (id=1)
INSERT INTO parking_spots (id, zone_id) VALUES
('A-01', 1), ('A-02', 1), ('A-03', 1), ('A-04', 1), ('A-05', 1),
('A-06', 1), ('A-07', 1), ('A-08', 1), ('A-09', 1), ('A-10', 1);

-- Tạo chỗ đỗ cho Khu B (id=2)
INSERT INTO parking_spots (id, zone_id) VALUES
('B-01', 2), ('B-02', 2), ('B-03', 2), ('B-04', 2), ('B-05', 2);

-- Thêm một xe mẫu đã đăng ký
INSERT INTO vehicles (license_plate, owner_name, owner_phone, vehicle_type_id)
VALUES ('29A-12345', 'Nguyễn Văn A', '0987654321', 2); -- 2 là ID của 'BIKE'

-- SELECT 'Database setup complete.' AS status;
