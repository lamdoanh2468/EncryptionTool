//import controller.FileController;
import controller.TextController;
import view.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame view = null;
            try {
                view = new MainFrame();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            new TextController(view);
//            new FileController(view);
            view.setVisible(true);
        });
    }
}
