import gui.LoginFrame;

import javax.swing.*;

public class main {
    public static void main(String[] args) {
        // Chạy giao diện trên Event Dispatch Thread (EDT) để đảm bảo an toàn luồng cho Swing
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginFrame().setVisible(true);
            }
        });
    }

}
