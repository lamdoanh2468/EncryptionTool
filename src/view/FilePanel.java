package view;

import controller.FileController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class FilePanel extends JPanel {

    public final JLabel filePathLabel;
    public final JButton chooseFileBtn;
    public final JButton encryptFileBtn;
    public final JButton decryptFileBtn;
    public final JLabel statusLabel;
    private File selectedFile;

    FileController fileController;
    FileSelectorPanel fileSelectorPanel;

    public FilePanel(FileController fileController) {
        this.fileController = fileController;
        setLayout(new BorderLayout(0, 16));
        setBackground(MainFrame.BG_PANEL);
        setBorder(new EmptyBorder(20, 16, 20, 16));

        // Khởi tạo FileSelectorPanel (phần thuật toán + key)
        fileSelectorPanel = new FileSelectorPanel(fileController);

        // ====================== TOP: CHỌN FILE ======================
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

        JPanel fileRow = new JPanel(new BorderLayout(10, 0));
        fileRow.setOpaque(false);
        fileRow.add(filePathLabel, BorderLayout.CENTER);
        fileRow.add(chooseFileBtn, BorderLayout.EAST);

        JPanel topWrapper = new JPanel(new BorderLayout(0, 6));
        topWrapper.setOpaque(false);
        topWrapper.add(titleLabel, BorderLayout.NORTH);
        topWrapper.add(fileRow, BorderLayout.CENTER);

        // ====================== CENTER: FileSelectorPanel (AES/DES + Key) ======================
        // ====================== SOUTH: Nút mã hóa + Status ======================
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnPanel.setOpaque(false);

        encryptFileBtn = makeActionButton("🔒  Mã hóa File", MainFrame.BLUE_BTN);
        decryptFileBtn = makeActionButton("🔓  Giải mã File", new Color(60, 180, 120));

        encryptFileBtn.setPreferredSize(new Dimension(180, 44));
        decryptFileBtn.setPreferredSize(new Dimension(180, 44));

        btnPanel.add(encryptFileBtn);
        btnPanel.add(decryptFileBtn);

        // Panel chứa cả nút + status (để SOUTH gọn gàng)
        JPanel southPanel = new JPanel(new BorderLayout(0, 12));
        southPanel.setOpaque(false);
        southPanel.add(btnPanel, BorderLayout.NORTH);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        statusLabel.setForeground(MainFrame.TXT_MUTED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        southPanel.add(statusLabel, BorderLayout.SOUTH);

        // ====================== THÊM VÀO LAYOUT ======================
        add(topWrapper, BorderLayout.NORTH);
        add(fileSelectorPanel, BorderLayout.CENTER);   // ← FileSelectorPanel giờ nằm đúng vị trí
        add(southPanel, BorderLayout.SOUTH);

        // ====================== Wire choose file ======================
        chooseFileBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Chọn file để mã hóa / giải mã");
            int result = fc.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fc.getSelectedFile();
                filePathLabel.setText(selectedFile.getAbsolutePath());
                filePathLabel.setForeground(MainFrame.TXT_MAIN);
            }
        });

        // TODO: Thêm listener cho nút Encrypt/Decrypt (sẽ kết nối với FileController)
        // encryptFileBtn.addActionListener(e -> fileController.encryptFile(...));
        // decryptFileBtn.addActionListener(e -> fileController.decryptFile(...));
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

    public File getSelectedFile() {
        return selectedFile;
    }

    public void setStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setForeground(success ? new Color(40, 160, 80) : new Color(200, 60, 60));
    }
}