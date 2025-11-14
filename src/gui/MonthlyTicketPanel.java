package gui;

import controller.ParkingController;
import model.Ticket;
import model.Vehicle;
import model.VehicleType;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class MonthlyTicketPanel extends JPanel {

    private final ParkingController parkingController;

    private JTextField licensePlateField;
    private JButton checkVehicleButton;
    private JPanel newVehiclePanel;
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
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Dòng 1: Nhập biển số
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Biển số xe:"), gbc);
        licensePlateField = new JTextField(15);
        gbc.gridx = 1; formPanel.add(licensePlateField, gbc);
        checkVehicleButton = new JButton("Kiểm Tra Xe");
        gbc.gridx = 2; formPanel.add(checkVehicleButton, gbc);

        // Dòng 2: Thông báo trạng thái
        vehicleInfoLabel = new JLabel("Nhập biển số và bấm 'Kiểm Tra'.");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3; formPanel.add(vehicleInfoLabel, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Dòng 3: Panel thông tin xe mới (được tạo ở đây)
        newVehiclePanel = new JPanel(new GridLayout(0, 2, 5, 5));
        newVehiclePanel.setBorder(BorderFactory.createTitledBorder("Thông tin xe mới"));
        ownerNameField = new JTextField(15);
        ownerPhoneField = new JTextField(15);
        vehicleTypeComboBox = new JComboBox<>(VehicleType.values());
        newVehiclePanel.add(new JLabel("Tên chủ xe:"));
        newVehiclePanel.add(ownerNameField);
        newVehiclePanel.add(new JLabel("SĐT chủ xe:"));
        newVehiclePanel.add(ownerPhoneField);
        newVehiclePanel.add(new JLabel("Loại xe:"));
        newVehiclePanel.add(vehicleTypeComboBox);
        newVehiclePanel.setVisible(false); // Ẩn ban đầu

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; formPanel.add(newVehiclePanel, gbc);
        gbc.gridwidth = 1; // Reset gridwidth

        // Dòng 4: Số tháng đăng ký
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Số tháng đăng ký:"), gbc);
        monthsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 12, 1));
        gbc.gridx = 1; formPanel.add(monthsSpinner, gbc);

        // Dòng 5: Nút đăng ký
        registerButton = new JButton("Đăng Ký Vé Tháng");
        gbc.gridx = 1; gbc.gridy = 4; gbc.anchor = GridBagConstraints.CENTER; formPanel.add(registerButton, gbc);

        checkVehicleButton.addActionListener(e -> handleCheckVehicle());
        registerButton.addActionListener(e -> handleRegister());

        add(formPanel, BorderLayout.NORTH);
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
                newVehiclePanel.setVisible(false);
            } else {
                vehicleInfoLabel.setText("Phát hiện xe mới! Vui lòng nhập thông tin bên dưới.");
                vehicleInfoLabel.setForeground(new Color(218, 165, 32));
                newVehiclePanel.setVisible(true);
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
            if (newVehiclePanel.isVisible()) {
                String ownerName = ownerNameField.getText().trim();
                String ownerPhone = ownerPhoneField.getText().trim();
                if (ownerName.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Tên chủ xe không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                VehicleType vehicleType = (VehicleType) vehicleTypeComboBox.getSelectedItem();
                vehicle = new Vehicle(ownerName, ownerPhone, licensePlate, vehicleType);
            } else {
                vehicle = parkingController.getVehicle(licensePlate);
                if (vehicle == null) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin xe. Vui lòng bấm 'Kiểm Tra Xe' lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Ticket newMonthlyTicket = parkingController.registerMonthlyTicket(vehicle, numberOfMonths);

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String successMessage = String.format(
                "Đăng ký vé tháng thành công!\n\nBiển số: %s\nHiệu lực đến ngày: %s",
                newMonthlyTicket.getLicensePlate(),
                newMonthlyTicket.getExpiryDate().format(dateFormatter)
            );
            JOptionPane.showMessageDialog(this, successMessage, "Thành Công", JOptionPane.INFORMATION_MESSAGE);
            
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
