package gui;

import controller.ParkingController;
import controller.ParkingException;
import model.Ticket;
import model.Vehicle;
import model.VehicleType;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class CheckInPanel extends JPanel {

    private final ParkingController parkingController;
    private JTextField licensePlateField;
    private JTextField spotIdField;
    private JButton checkInButton;

    public CheckInPanel() {
        this.parkingController = new ParkingController();
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Check-in Xe Vào Bãi"));
        initAndLayoutComponents();
        attachActions();
    }

    private void initAndLayoutComponents() {
        licensePlateField = new JTextField(15);
        spotIdField = new JTextField(15);
        checkInButton = new JButton("Xác nhận Check-in");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.LINE_END;

        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Biển số xe:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; add(licensePlateField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.LINE_END; add(new JLabel("Mã chỗ đỗ:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.LINE_START; add(spotIdField, gbc);

        gbc.gridx = 1; gbc.gridy = 2; gbc.anchor = GridBagConstraints.CENTER; add(checkInButton, gbc);
    }

    private void attachActions() {
        checkInButton.addActionListener(e -> handleCheckIn());
    }

    private void handleCheckIn() {
        String licensePlate = licensePlateField.getText().trim().toUpperCase();
        String spotId = spotIdField.getText().trim();

        if (licensePlate.isEmpty() || spotId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ biển số và mã chỗ đỗ.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Vehicle vehicle = parkingController.getVehicle(licensePlate);
            if (vehicle == null) {
                if (!promptForNewVehicleInfo(licensePlate)) {
                    return;
                }
            }

            Ticket createdTicket = parkingController.checkIn(licensePlate, spotId);

            String successMessage;
            if (createdTicket.getPrice() == 0) {
                 successMessage = String.format(
                    "XE VÉ THÁNG ĐÃ VÀO BÃI!\n\nBiển số: %s\nChỗ đỗ: %s\nThời gian vào: %s",
                    createdTicket.getLicensePlate(), createdTicket.getSpotId(),
                    createdTicket.getEntryTime().toLocalTime().toString()
                );
                 JOptionPane.showMessageDialog(this, successMessage, "Xe Vé Tháng", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 successMessage = String.format(
                    "Check-in xe vãng lai thành công!\n\nVé số: %d\nBiển số: %s\nChỗ đỗ: %s\nLoại vé: %s",
                    createdTicket.getId(), createdTicket.getLicensePlate(), createdTicket.getSpotId(),
                    createdTicket.getTicketType()
                );
                 JOptionPane.showMessageDialog(this, successMessage, "Check-in Thành Công", JOptionPane.INFORMATION_MESSAGE);
            }
            
            licensePlateField.setText("");
            spotIdField.setText("");

        } catch (ParkingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Nghiệp Vụ", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tương tác với cơ sở dữ liệu: " + ex.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private boolean promptForNewVehicleInfo(String licensePlate) {
        JTextField ownerNameField = new JTextField();
        JTextField ownerPhoneField = new JTextField();
        JComboBox<VehicleType> vehicleTypeComboBox = new JComboBox<>(VehicleType.values());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Biển số:"));
        panel.add(new JLabel(licensePlate));
        panel.add(new JLabel("Tên chủ xe:"));
        panel.add(ownerNameField);
        panel.add(new JLabel("SĐT chủ xe:"));
        panel.add(ownerPhoneField);
        panel.add(new JLabel("Loại xe:"));
        panel.add(vehicleTypeComboBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Phát hiện xe vãng lai mới", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String ownerName = ownerNameField.getText().trim();
            if (ownerName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên chủ xe không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            String ownerPhone = ownerPhoneField.getText().trim();
            VehicleType vehicleType = (VehicleType) vehicleTypeComboBox.getSelectedItem();
            
            Vehicle newVehicle = new Vehicle(ownerName, ownerPhone, licensePlate, vehicleType);
            
            try {
                parkingController.addNewVehicle(newVehicle);
                JOptionPane.showMessageDialog(this, "Đã thêm thông tin xe mới thành công!");
                return true;
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu thông tin xe mới: " + e.getMessage(), "Lỗi Database", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
}
