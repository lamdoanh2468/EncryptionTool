import controller.CipherController;
import model.CipherModel;
import view.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CipherModel model = new CipherModel();
            MainFrame   view  = new MainFrame();
            new CipherController(model, view);
            view.setVisible(true);
        });
    }
}
