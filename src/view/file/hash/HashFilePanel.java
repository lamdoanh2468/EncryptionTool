package view.file.hash;

import controller.file.HashFileController;
import controller.file.FileController;
import view.MainFrame;
import view.file.FilePanelUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HashFilePanel extends JPanel {

    public final JButton hashFileBtn;
    public final JTextArea resultArea;
    public final JButton copyBtn;
    public final JButton saveBtn;

    private final JLabel infoLabel;
    public final FileController fileController;
    public final HashFileController hashFileController;

    public HashFilePanel(FileController fileController, HashFileController hashFileController) {
        this.fileController = fileController;
        this.hashFileController = hashFileController;

        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
        setBorder(new EmptyBorder(10, 0, 10, 0));

        // Thông báo
        infoLabel = new JLabel("Hàm băm không cần khóa - Chọn thuật toán ở trên rồi bấm 'Băm File'");
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        infoLabel.setForeground(Color.RED);
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Nút băm
        hashFileBtn = createActionButton("Băm File", MainFrame.ACCENT);
        hashFileBtn.setPreferredSize(new Dimension(160, 40));
        hashFileBtn.addActionListener(e -> hashFileController.hashSelectedFile());
        // Kết quả
        resultArea = new JTextArea(6, 40);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setBackground(MainFrame.BG_INPUT);
        resultArea.setForeground(MainFrame.TXT_MAIN);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        // Nút Copy & Save
        copyBtn = FilePanelUI.createOutlineButton("Sao chép giá trị băm", MainFrame.ACCENT);

        copyBtn.addActionListener(e -> hashFileController.copyHashResult());
        saveBtn = FilePanelUI.createOutlineButton("Lưu giá trị băm", Color.BLACK);
        saveBtn.addActionListener(e -> hashFileController.saveHashToFile());

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(copyBtn);
        buttonRow.add(saveBtn);

        // Layout
        JPanel centerPanel = new JPanel(new BorderLayout(0, 8));
        centerPanel.setOpaque(false);
        centerPanel.add(infoLabel, BorderLayout.NORTH);
        centerPanel.add(scroll, BorderLayout.CENTER);
        centerPanel.add(buttonRow, BorderLayout.SOUTH);

        add(hashFileBtn, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JButton createActionButton(String text, Color color) {
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
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void setResult(String hashResult) {
        resultArea.setText(hashResult);
    }

    public void clearResult() {
        resultArea.setText("");
    }


    public void configureForAlgorithm(String algo) {
        if (algo == null || algo.isEmpty()) {
            infoLabel.setText("Hàm băm không cần khóa - Chọn thuật toán ở trên rồi bấm 'Băm File'");
            infoLabel.setForeground(Color.RED);
            return;
        }

        infoLabel.setText("Hàm băm: " + algo + " — Không cần khóa");
        infoLabel.setForeground(new Color(220, 38, 38));
        clearResult();
    }
}