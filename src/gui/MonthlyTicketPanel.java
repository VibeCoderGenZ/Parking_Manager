package gui;

import controller.ParkingController;
import model.Ticket;
import model.Vehicle;
import model.VehicleType;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Panel quản lý vé tháng với giao diện động.
 */
public class MonthlyTicketPanel extends JPanel {

    private final ParkingController parkingController;

    // Components
    private JTextField licensePlateField;
    private JButton checkVehicleButton;
    private JPanel newVehiclePanel; // Panel ẩn/hiện cho xe mới
    private JTextField ownerNameField;
    private JTextField ownerPhoneField;
    private JComboBox<VehicleType> vehicleTypeComboBox;
    private JSpinner monthsSpinner;
    private JButton registerButton;
    private JLabel vehicleInfoLabel;

    public MonthlyTicketPanel() {
        this.parkingController = new ParkingController();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Đăng Ký / Gia Hạn Vé Tháng"));
        initUI();
    }

    private void initUI() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.LINE_START;

        // --- Hàng 1: Nhập và kiểm tra biển số ---
        licensePlateField = new JTextField(15);
        checkVehicleButton = new JButton("Kiểm Tra Xe");
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Biển số xe:"), gbc);
        gbc.gridx = 1; formPanel.add(licensePlateField, gbc);
        gbc.gridx = 2; formPanel.add(checkVehicleButton, gbc);

        // --- Hàng 2: Hiển thị thông tin xe đã có ---
        vehicleInfoLabel = new JLabel("Nhập biển số và bấm 'Kiểm Tra'.");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; formPanel.add(vehicleInfoLabel, gbc);

        // --- Panel ẩn cho xe mới ---
        createNewVehiclePanel();
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; formPanel.add(newVehiclePanel, gbc);

        // --- Hàng 3: Chọn số tháng ---
        monthsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; formPanel.add(new JLabel("Số tháng đăng ký:"), gbc);
        gbc.gridx = 1; formPanel.add(monthsSpinner, gbc);

        // --- Hàng 4: Nút đăng ký ---
        registerButton = new JButton("Đăng Ký Vé Tháng");
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(registerButton, gbc);

        // Gắn hành động
        checkVehicleButton.addActionListener(e -> handleCheckVehicle());
        registerButton.addActionListener(e -> handleRegister());

        add(formPanel, BorderLayout.NORTH);
    }

    private void createNewVehiclePanel() {
        newVehiclePanel = new JPanel(new GridLayout(0, 2, 5, 5));
        newVehiclePanel.setBorder(BorderFactory.createTitledBorder("Thông tin xe mới"));
        ownerNameField = new JTextField(15);
        ownerPhoneField = new JTextField(15);
        vehicleTypeComboBox = new JComboBox<>(VehicleType.values());
        newVehiclePanel.add(new JLabel("Tên chủ xe:")); newVehiclePanel.add(ownerNameField);
        newVehiclePanel.add(new JLabel("SĐT chủ xe:")); newVehiclePanel.add(ownerPhoneField);
        newVehiclePanel.add(new JLabel("Loại xe:")); newVehiclePanel.add(vehicleTypeComboBox);
        newVehiclePanel.setVisible(false); // Ẩn đi lúc đầu
    }

    private void handleCheckVehicle() {
        String licensePlate = licensePlateField.getText().trim().toUpperCase();
        if (licensePlate.isEmpty()) {
            vehicleInfoLabel.setText("Vui lòng nhập biển số xe.");
            newVehiclePanel.setVisible(false);
            return;
        }
        try {
            Vehicle vehicle = parkingController.getVehicle(licensePlate);
            if (vehicle != null) {
                vehicleInfoLabel.setText(String.format("Xe đã đăng ký. Chủ xe: %s, Loại: %s", vehicle.getOwnerName(), vehicle.getType()));
                vehicleInfoLabel.setForeground(Color.BLUE);
                newVehiclePanel.setVisible(false); // Ẩn panel xe mới
            } else {
                vehicleInfoLabel.setText("Phát hiện xe mới! Vui lòng nhập thông tin bên dưới.");
                vehicleInfoLabel.setForeground(new Color(218, 165, 32)); // Dark Golden Rod
                newVehiclePanel.setVisible(true); // Hiện panel xe mới
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi kiểm tra thông tin xe.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRegister() {
        String licensePlate = licensePlateField.getText().trim().toUpperCase();
        int numberOfMonths = (Integer) monthsSpinner.getValue();

        if (licensePlate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập và kiểm tra biển số xe trước.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Vehicle vehicle;
            // Nếu panel xe mới đang hiện, nghĩa là chúng ta cần tạo xe mới
            if (newVehiclePanel.isVisible()) {
                String ownerName = ownerNameField.getText().trim();
                if (ownerName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên chủ xe không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String ownerPhone = ownerPhoneField.getText().trim();
                VehicleType vehicleType = (VehicleType) vehicleTypeComboBox.getSelectedItem();
                vehicle = new Vehicle(ownerName, ownerPhone, licensePlate, vehicleType);
            } else {
                // Nếu không, lấy thông tin xe đã có từ DB
                vehicle = parkingController.getVehicle(licensePlate);
                if (vehicle == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin xe. Vui lòng bấm 'Kiểm Tra Xe' lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Gọi controller để đăng ký
            Ticket newMonthlyTicket = parkingController.registerMonthlyTicket(vehicle, numberOfMonths);

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String successMessage = String.format(
                "Đăng ký vé tháng thành công!\n\nBiển số: %s\nSố tiền: %s\nHiệu lực đến ngày: %s",
                newMonthlyTicket.getLicensePlate(),
                currencyFormatter.format(newMonthlyTicket.getPrice()),
                newMonthlyTicket.getExpiryDate().format(dateFormatter)
            );
            JOptionPane.showMessageDialog(this, successMessage, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            
            // Reset form
            licensePlateField.setText("");
            newVehiclePanel.setVisible(false);
            vehicleInfoLabel.setText("Nhập biển số và bấm 'Kiểm Tra'.");
            vehicleInfoLabel.setForeground(Color.BLACK);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đăng ký vé tháng: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
