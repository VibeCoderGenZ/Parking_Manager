# Parking Manager

> Ứng dụng desktop Java Swing để quản lý bãi gửi xe: xe, chỗ đỗ, vé vào/ra và tìm kiếm nhanh. Dữ liệu lưu bằng CSV, không cần cơ sở dữ liệu ngoài. Ứng dụng quản lý khách sạn được phát triển nhằm hỗ trợ thực hiện các tác vụ cơ bản để quản lý bãi gửi xe. Hỗ trợ lưu trữ dữ liệu thông qua đọc ghi file `csv` văn bản.

## Mục lục

- [Tổng quan](#tổng-quan)
- [Tính năng chính](#tính-năng-chính)
- [Công nghệ](#công-nghệ)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Bắt đầu nhanh](#bắt-đầu-nhanh)
- [Cách sử dụng](#cách-sử-dụng)
- [Định dạng mẫu dữ liệu CSV](#định-dạng-mẫu-dữ-liệu-csv)
- [Tài liệu](#tài-liệu)

## Tổng quan

Parking Manager được xây dựng cho nhu cầu **Quản lý bãi gửi xe** quy mô nhỏ đến trung bình. Ứng dụng tách biệt rõ phần giao diện (Swing) và phần nghiệp vụ (logic), xây dựng dựa trên phương pháp *Hướng đối tượng*, đảm bảo dễ bảo trì và mở rộng.

## Tính năng chính

- Quản lý xe: thêm/xóa, lưu thông tin chủ xe, hỗ trợ nhiều loại xe.
- Quản lý chỗ đỗ: theo dõi tình trạng trống/đang sử dụng, ràng buộc loại xe phù hợp.
- Quản lý vé: tạo vé vào, thu vé ra, ghi nhận thời gian vào/ra.
- Tìm kiếm nhanh: theo biển số, chủ xe, mã vé hoặc mã chỗ đỗ.
- Lưu/đọc dữ liệu tự động qua file CSV, không cần cài đặt DB.

## Công nghệ

- Ngôn ngữ: Java 8+ (khuyến nghị 11+).
- UI: Java Swing.
- Lưu trữ: File CSV.

## Cấu trúc dự án

```
Parking_Manager/
├── data/           # Dữ liệu CSV của ứng dụng
├── doc/            # Tài liệu, sơ đồ lớp
├── src/
│   ├── App.java                        # Entry point
│   ├── gui/                            # Tầng giao diện (Swing)
│   │   ├── MainFrame.java
│   │   ├── VehicleManagementPanel.java
│   │   ├── TicketManagementPanel.java
│   │   ├── SpotManagementPanel.java
│   │   ├── SearchPanel.java
│   │   ├── SearchVehiclePanel.java
│   │   ├── SearchTicketPanel.java
│   │   ├── SearchSpotPanel.java
│   │   ├── CreateTicketPanel.java
│   │   └── CollectTicketPanel.java
│   └── logic/                          # Tầng nghiệp vụ
│       ├── ParkingLot.java
│       ├── DataManager.java
│       ├── Vehicle.java
│       ├── ParkingSpot.java
│       ├── Ticket.java
│       └── VehicleType.java
└── README.md
```

## Bắt đầu nhanh

1. Yêu cầu: Java 8+ JDK. Không cần thư viện ngoài.
2. Build:

```bash
mkdir -p bin
javac -d bin src/**/*.java
```

3. Chạy ứng dụng:

```bash
java -cp bin App
```

## Cách sử dụng

- Khởi động ứng dụng, màn hình chính hiển thị thanh điều hướng.
- Tạo vé / Thu vé: cho xe vào/ra bãi đỗ, ghi nhận thời gian vào/ra.
- Danh sách Vé: xem danh sách vé, vé đã chưa thu, vé đã thu.
- Danh sách Xe: thêm/xóa xe, lưu chủ xe và số điện thoại.
- Danh sách Bãi Đỗ: xem danh sách chỗ đỗ, trạng thái trống/đang dùng, loại xe cho phép.
- Tìm kiếm: tra cứu nhanh theo biển số, chủ xe, mã vé hoặc mã chỗ đỗ.
- Thoát ứng dụng: dữ liệu tự động lưu lại vào các file CSV trong thư mục `data/`

## Định dạng mẫu dữ liệu CSV

### vehicles.csv

```
#licensePlate,#type,#ownerName,#ownerPhone
30A12345,CAR,Nguyen Van A,0912345678
```

### spots.csv

```
#spotID,#allowedType,#licensePlate,#occupied
1,CAR,30A12345,true
2,CAR,,false
```

### tickets.csv

```
#ticketID,#spotID,#licensePlate,#entryTime,#exitTime
1,1,30A12345,2025-12-09T10:30:00,
```

## Tài liệu

- Báo cáo chi tiết: [Google Docs](https://docs.google.com/document/d/1m4v-R8dojgZY1A2r_tjpGel5WZNoQn8GZccrVRe5f-E/edit)
