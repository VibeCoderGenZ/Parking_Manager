-- Tạo cơ sở dữ liệu nếu nó chưa tồn tại
CREATE DATABASE IF NOT EXISTS parking_manager;
USE parking_manager;

-- Bảng để lưu trữ các loại xe (ví dụ: Xe đạp, Xe máy, Ô tô)
CREATE TABLE vehicle_type (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL -- BICYCLE, BIKE, CAR
);

-- Chèn các loại xe mặc định
INSERT INTO vehicle_type (name) VALUES ('BICYCLE'), ('BIKE'), ('CAR');

-- Bảng để lưu trữ thông tin người dùng
CREATE TABLE users (
    username VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL
);

-- Bảng để lưu trữ thông tin xe
CREATE TABLE vehicles (
    license_plate VARCHAR(50) PRIMARY KEY,
    owner_name VARCHAR(255) NOT NULL,
    owner_phone VARCHAR(20),
    vehicle_type_id INT,
    FOREIGN KEY (vehicle_type_id) REFERENCES vehicle_type(id)
);

-- Bảng để lưu trữ các khu vực đậu xe
CREATE TABLE parking_zones (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) UNIQUE NOT NULL,
    allowed_vehicle_type_id INT,
    number_of_spots INT NOT NULL,
    FOREIGN KEY (allowed_vehicle_type_id) REFERENCES vehicle_type(id)
);

-- Bảng để lưu trữ các chỗ đậu xe riêng lẻ
CREATE TABLE parking_spots (
    id VARCHAR(100) PRIMARY KEY,
    zone_id INT,
    is_occupied BOOLEAN DEFAULT FALSE,
    license_plate VARCHAR(50) NULL, -- Xe hiện đang đậu
    FOREIGN KEY (zone_id) REFERENCES parking_zones(id),
    FOREIGN KEY (license_plate) REFERENCES vehicles(license_plate)
);

-- Bảng để lưu trữ thông tin vé
CREATE TABLE tickets (
    id INT PRIMARY KEY AUTO_INCREMENT,
    license_plate VARCHAR(50) NOT NULL,
    spot_id VARCHAR(100),
    entry_time DATETIME NOT NULL,
    exit_time DATETIME,
    price BIGINT,
    ticket_type VARCHAR(20) NOT NULL, -- 'per_hour', 'per_turn', 'per_month'
    expiry_date DATETIME, -- Chỉ dành cho vé tháng
    FOREIGN KEY (license_plate) REFERENCES vehicles(license_plate),
    FOREIGN KEY (spot_id) REFERENCES parking_spots(id)
);

-- Chèn dữ liệu người dùng mẫu
INSERT INTO users (username, password) VALUES ('admin', 'admin');
