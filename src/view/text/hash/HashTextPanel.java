package view.text.hash;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HashTextPanel extends JPanel {

    public HashTextPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);

        JLabel hashInfo = new JLabel("Hàm băm không cần khóa");
        hashInfo.setFont(new Font("SansSerif", Font.BOLD, 16));
        hashInfo.setForeground(Color.RED);
        hashInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea helpText = new JTextArea(
                "- Ấn 'BĂM VĂN BẢN' để tạo mã băm mới."

        );
        helpText.setEditable(false);
        helpText.setOpaque(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setFont(new Font("SansSerif", Font.BOLD, 14));
        helpText.setBorder(new EmptyBorder(10, 0, 0, 0));
        helpText.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(hashInfo);
        add(helpText);
        add(Box.createVerticalGlue());
    }
}