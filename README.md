# Parking Manager

## Giới thiệu

Parking Manager là một hệ thống quản lý bãi đỗ xe được thiết kế để tối ưu hóa việc quản lý không gian đỗ xe, thông tin khách hàng và các giao dịch liên quan. Hệ thống này giúp tự động hóa nhiều tác vụ thủ công, nâng cao hiệu quả hoạt động và cải thiện trải nghiệm người dùng.

## Chức năng chính

*   **Quản lý bãi đỗ xe:**
    *   Theo dõi trạng thái các vị trí đỗ xe (trống/đã chiếm).
    *   Quản lý thông tin chi tiết về từng vị trí đỗ xe.
    *   Cập nhật sơ đồ bãi đỗ xe trực quan.

*   **Quản lý khách hàng:**
    *   Lưu trữ thông tin khách hàng (tên, biển số xe, thông tin liên hệ).
    *   Quản lý lịch sử đỗ xe của từng khách hàng.
    *   Hỗ trợ đăng ký và hủy đăng ký khách hàng.

*   **Quản lý giao dịch:**
    *   Ghi nhận thời gian vào/ra của xe.
    *   Tính toán phí đỗ xe dựa trên thời gian và biểu giá.
    *   Quản lý các phương thức thanh toán.
    *   Tạo báo cáo doanh thu và giao dịch.

*   **Hệ thống báo cáo:**
    *   Báo cáo tình trạng bãi đỗ xe theo thời gian thực.
    *   Báo cáo doanh thu hàng ngày/tháng/năm.
    *   Báo cáo thống kê khách hàng và tần suất sử dụng.

*   **Bảo mật:**
    *   Hệ thống phân quyền người dùng (admin, nhân viên).
    *   Đảm bảo an toàn dữ liệu khách hàng và giao dịch.

## Công nghệ sử dụng

Dự án này được phát triển sử dụng các công nghệ sau:

*   **Ngôn ngữ lập trình:** Java
*   **Giao diện người dùng:** Java Swing
*   **Cơ sở dữ liệu:** MySQL
*   **Quản lý mã nguồn:** GitHub

## Hướng dẫn cài đặt và sử dụng

Để cài đặt và chạy dự án Parking Manager, bạn cần thực hiện các bước sau:

### 1. Yêu cầu hệ thống

*   **Java Development Kit (JDK):** Phiên bản 8 trở lên.
*   **MySQL Server:** Phiên bản 5.7 trở lên.
*   **IDE:** IntelliJ IDEA, Eclipse hoặc NetBeans (khuyến nghị).
*   **Git:** Để clone mã nguồn.

### 2. Clone dự án

Mở Terminal hoặc Command Prompt và clone dự án từ GitHub:

```bash
git clone https://github.com/VibeCoderGenZ/Parking_Manager
```

### 3. Cài đặt cơ sở dữ liệu MySQL

1.  **Tạo cơ sở dữ liệu:**
    Mở MySQL Workbench hoặc công cụ quản lý MySQL khác và tạo một cơ sở dữ liệu mới. Ví dụ: `parking_manager_db`.
    ```sql
    CREATE DATABASE parking_manager;
    ```
2.  **Nhập lược đồ (schema):**
    Tìm file `database.sql` trong thư mục dự án. File này chứa các câu lệnh SQL để tạo bảng và dữ liệu ban đầu. Chạy các câu lệnh này trên cơ sở dữ liệu `parking_manager` vừa tạo.

### 4. Cấu hình kết nối cơ sở dữ liệu

1.  Mở dự án trong IDE của bạn (ví dụ: IntelliJ IDEA).
2.  Tìm file cấu hình kết nối cơ sở dữ liệu. Thường là một file `.properties` hoặc một phần trong mã nguồn Java (ví dụ: trong một lớp `DatabaseConnection` hoặc `DBUtil`).
3.  Cập nhật thông tin kết nối cơ sở dữ liệu cho phù hợp với cài đặt MySQL của bạn:
    *   **URL:** `jdbc:mysql://localhost:3306/parking_manager_db` (thay `localhost:3306` nếu MySQL của bạn chạy trên cổng hoặc host khác)
    *   **Username:** `root` (hoặc username MySQL của bạn)
    *   **Password:** `your_mysql_password` (hoặc password MySQL của bạn)

### 5. Xây dựng và chạy ứng dụng

1.  **Mở dự án trong IDE:**
    *   **IntelliJ IDEA:** Mở IDEA, chọn `File` -> `Open`, điều hướng đến thư mục `Parking_Manager` đã clone và chọn nó.
2.  **Cài đặt các thư viện phụ thuộc:**
    Bạn thêm tất các thư viện được sử dụng trong lib trong `Project Structure -> Libraries.`.
3.  **Chạy ứng dụng:**
    Tìm lớp chính (thường là lớp có phương thức `main(String[] args)`) và chạy nó. Ở đây là: `src/Main.java`.

Sau khi hoàn thành các bước trên, ứng dụng Parking Manager sẽ khởi động và bạn có thể bắt đầu sử dụng.
