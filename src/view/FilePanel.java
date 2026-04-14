package view;

import controller.FileController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FilePanel extends JPanel {

    public final JLabel filePathLabel;
    public final JButton chooseFileBtn;
    public final JButton encryptFileBtn;
    public final JButton decryptFileBtn;
    public final JLabel statusLabel;
    FileController fileController;
    FileSelectorPanel fileSelectorPanel;
    private File selectedFile;

    public FilePanel(FileController fileController) throws Exception {
        this.fileController = fileController;
        setLayout(new BorderLayout(0, 16));
        setBackground(MainFrame.BG_PANEL);
        setBorder(new EmptyBorder(20, 16, 20, 16));

        this.fileSelectorPanel = new FileSelectorPanel(fileController);

        JLabel titleLabel = new JLabel("CHỌN FILE");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        titleLabel.setForeground(MainFrame.TXT_LABEL);

        filePathLabel = new JLabel("Chưa chọn file nào...");
        filePathLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        filePathLabel.setForeground(MainFrame.TXT_MUTED);
        filePathLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        filePathLabel.setBackground(MainFrame.BG_INPUT);
        filePathLabel.setOpaque(true);

        chooseFileBtn = makeActionButton("Chọn file...", MainFrame.ACCENT);
        chooseFileBtn.addActionListener(
                e -> selectedFile = fileController.chooseFile(filePathLabel)
        );

        JPanel fileRow = new JPanel(new BorderLayout(10, 0));
        fileRow.setOpaque(false);
        fileRow.add(filePathLabel, BorderLayout.CENTER);
        fileRow.add(chooseFileBtn, BorderLayout.EAST);

        JPanel topWrapper = new JPanel(new BorderLayout(0, 6));
        topWrapper.setOpaque(false);
        topWrapper.add(titleLabel, BorderLayout.NORTH);
        topWrapper.add(fileRow, BorderLayout.CENTER);


        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnPanel.setOpaque(false);

        encryptFileBtn = makeActionButton("Mã hóa File", MainFrame.BLUE_BTN);
        encryptFileBtn.addActionListener(e -> {

            String algo = (String) fileSelectorPanel.algoCombo.getSelectedItem();
            try {
                fileController.encryptFile(fileController.getCipher(algo), selectedFile);
            } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                     NoSuchAlgorithmException | IOException | BadPaddingException | InvalidKeyException ex) {
                throw new RuntimeException(ex);
            }
        });
        decryptFileBtn = makeActionButton("Giải mã File", new Color(60, 180, 120));
        decryptFileBtn.addActionListener(e -> {
            String algo = (String) fileSelectorPanel.algoCombo.getSelectedItem();
            try {
                fileController.decryptFile(fileController.getCipher(algo), selectedFile);
            } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException |
                     NoSuchAlgorithmException | IOException | BadPaddingException | InvalidKeyException ex) {
                throw new RuntimeException(ex);
            }
        });
        encryptFileBtn.setPreferredSize(new Dimension(180, 44));
        decryptFileBtn.setPreferredSize(new Dimension(180, 44));

        btnPanel.add(encryptFileBtn);
        btnPanel.add(decryptFileBtn);

        JPanel southPanel = new JPanel(new BorderLayout(0, 12));
        southPanel.setOpaque(false);
        southPanel.add(btnPanel, BorderLayout.NORTH);

        statusLabel = new JLabel("Vui lòng chọn file để tiếp tục");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        statusLabel.setForeground(MainFrame.TXT_MUTED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        add(topWrapper, BorderLayout.NORTH);
        add(fileSelectorPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);

        chooseFileBtn.addActionListener(e -> {

        });

    }

    private JButton makeActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed() ? color.darker()
                        : getModel().isRollover() ? color.brighter() : color;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 36));
        return btn;
    }

}