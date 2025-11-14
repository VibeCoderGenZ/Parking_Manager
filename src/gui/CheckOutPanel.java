package gui;

import controller.ParkingController;
import controller.ParkingException;
import model.Ticket;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class CheckOutPanel extends JPanel {

    private final ParkingController parkingController;

    private JTextField licensePlateField;
    private JButton checkOutButton;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");


    public CheckOutPanel() {
        this.parkingController = new ParkingController();

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Cho Xe Ra Bãi"));

        initAndLayoutComponents();

        attachActions();
    }

    private void initAndLayoutComponents() {
        licensePlateField = new JTextField(15);
        checkOutButton = new JButton("Xác nhận ra bãi");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.LINE_END;

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Biển số xe cần ra bãi:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        add(licensePlateField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        add(checkOutButton, gbc);
    }

    private void attachActions() {
        checkOutButton.addActionListener(e -> handleCheckOut());
    }

    private void handleCheckOut() {
        String licensePlate = licensePlateField.getText().trim();

        if (licensePlate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập biển số xe.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Ticket updatedTicket = parkingController.checkOut(licensePlate);

            String successMessage = String.format(
                "Check-out thành công!\n\nBiển số: %s\nThời gian vào: %s\nThời gian ra: %s",
                updatedTicket.getLicensePlate(),
                updatedTicket.getEntryTime().format(formatter),
                updatedTicket.getExitTime().format(formatter)
            );
            JOptionPane.showMessageDialog(this, successMessage, "Check-out Thành Công", JOptionPane.INFORMATION_MESSAGE);

            licensePlateField.setText("");

        } catch (ParkingException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi Nghiệp Vụ", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tương tác với cơ sở dữ liệu.", "Lỗi Database", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
