# Quản Lý Bãi Gửi Xe

## 1. Giới thiệu

Đây là dự án xây dựng một phần mềm nhằm hỗ trợ quy trình quản lý tại các bãi gửi xe truyền thống. Hệ thống giúp thay thế
việc ghi vé giấy, sổ sách thủ công bằng các công cụ máy tính dễ sử dụng hướng đến đến người dùng là bảo vê, nhân viên
khu bãi.

Dự án được xây dựng bằng ngôn ngữ **Java** kết hợp với **CSDL MySQL**

## 2. Mục tiêu

- **Tăng cường an ninh:** Giảm thiểu rủi ro trộm cắp, mất xe, hoặc tráo đổi vé xe.
- **Minh bạch tài chính:** Quản lý doanh thu chặt chẽ, tránh thất thoát tiền bạc.
- **Tối ưu vận hành:** Tăng tốc độ xử lý xe vào/ra, giảm ùn tắc.
- **Hiện đại hóa dịch vụ:** Cung cấp trải nghiệm chuyên nghiệp và tiện lợi cho khách hàng.

## 3. Chức năng chi tiết

Hệ thống bao gồm các nhóm chức năng chính, được phân quyền theo vai trò người dùng (Quản trị viên và Nhân viên).

### 3.1. Chức năng dành cho Nhân viên (Bảo vệ)

Đây là các chức năng nghiệp vụ cốt lõi được sử dụng hàng ngày tại cổng vào/ra.

- **Quản lý Xe vào:**

    - Ghi nhận thông tin xe: Biển số, loại xe (xe máy, ô tô), chủ xe, thông tin chủ xe.
    - Ghi nhận thời gian xe vào bãi.
    - Hệ thống tự động cấp một mã vé (ID) duy nhất cho xe.

- **Quản lý Xe ra:**

    - Nhập thông tin biển số xe ra, thời gian xe ra.
    - Hệ thống tự động tính toán chi phí gửi xe dựa trên bảng giá đã cài đặt (theo giờ, theo ngày, theo loại xe).

- **Tra cứu nhanh:**

    - Tìm kiếm xe trong bãi theo biển số.
    - Kiểm tra trạng thái của một vé/thẻ cụ thể.

### 3.2. Chức năng dành cho Quản trị viên (Admin)

Đây là các chức năng quản lý, cấu hình và báo cáo, yêu cầu quyền truy cập cao hơn.

- **Quản Lý Bãi Đỗ**

    - Thêm, xóa, tạo vị trí bãi đỗ theo khu, theo chỗ.
    - Tự động tạo tên chỗ đỗ theo thứ tự danh sách.

- **Quản lý Bảng giá (Cấu hình):**

    - Thêm, xóa, sửa các loại giá vé.
    - Cấu hình giá vé theo loại xe (xe máy, ô tô 4 chỗ, ô tô 7 chỗ...).

- **Quản lý Vé tháng:**

    - Đăng ký vé tháng cho khách hàng (thông tin chủ xe, biển số, ngày bắt đầu, ngày kết thúc).
    - Gia hạn hoặc hủy vé tháng.
    - Tra cứu danh sách vé tháng còn hạn/hết hạn.

- **Báo cáo & Thống kê:**
    - Xem lịch sử chi tiết các lượt xe ra/vào.
    - Thống kê số lượng xe đang có trong bãi (sức chứa).
    - Thống kê doanh thu trong thời gian.

## 4. Công nghệ sử dụng

- **Ngôn ngữ lập trình:** **Java** (Java Swing hoặc JavaFX cho giao diện Desktop).
- **Hệ quản trị CSDL:** **MySQL**.
- **IDE:** NetBeans, IntelliJ IDEA hoặc Eclipse.
- **Thư viện:**
    - `mysql-connector-java`: Để kết nối Java với MySQL.
    - `jcalendar`: Để chọn ngày/tháng trong giao diện.
