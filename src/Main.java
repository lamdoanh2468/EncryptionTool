//import controller.FileController;
import controller.TextController;
import view.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame view = new MainFrame();
            new TextController(view);
//            new FileController(view);
            view.setVisible(true);
        });
    }
}
