package controller;

/**
 * Lớp ngoại lệ tùy chỉnh để biểu thị các lỗi nghiệp vụ liên quan đến bãi đỗ xe.
 * Ví dụ: Cố gắng đỗ xe vào một chỗ đã có người, hoặc check-out một xe không có trong bãi.
 */
public class ParkingException extends Exception {

    public ParkingException(String message) {
        super(message);
    }
}
