import gui.MainFrame;
import javax.swing.SwingUtilities;

public class ParkingManager {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
