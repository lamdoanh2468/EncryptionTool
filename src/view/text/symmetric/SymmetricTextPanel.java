package view.text.symmetric;

import controller.text.TextController;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SymmetricTextPanel extends JPanel {

    public final JTextArea keyArea;
    private final JLabel keyHintLabel;
    private final TextController textController;
    private final JComboBox<String> algoCombo;
    private JButton genButton;
    private JButton copyButton;
    private JButton importButton;
    private JButton exportButton;
    private JButton removeButton;

    public SymmetricTextPanel(TextController textController, JComboBox<String> algoCombo) {
        this.textController = textController;
        this.algoCombo = algoCombo;

        setLayout(new BorderLayout(8, 0));
        setOpaque(false);

        keyArea = makeKeyArea();
        keyHintLabel = new JLabel("KHÓA");
        keyHintLabel.setName("cipherAlgo");
        keyHintLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        keyHintLabel.setForeground(MainFrame.TXT_LABEL);

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setOpaque(false);
        left.add(keyHintLabel, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(keyArea);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        left.add(scroll, BorderLayout.CENTER);

        JPanel btnCol = buildButtonColumn();

        add(left, BorderLayout.CENTER);
        add(btnCol, BorderLayout.EAST);
    }

    private JTextArea makeKeyArea() {
        JTextArea ta = new JTextArea(2, 10);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setBackground(MainFrame.BG_INPUT);
        ta.setForeground(MainFrame.TXT_MAIN);
        ta.setCaretColor(MainFrame.TXT_MAIN);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(false);
        ta.setBorder(new EmptyBorder(6, 8, 6, 8));
        ta.setEditable(false);
        return ta;
    }

    private JPanel buildButtonColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setBorder(new EmptyBorder(18, 0, 0, 0));

        genButton = createButton("Tạo khóa", MainFrame.ACCENT);
        genButton.addActionListener(e -> {
            String algo = (String) algoCombo.getSelectedItem();
            textController.generateKey(textController.getCipher(algo), keyArea);
        });

        copyButton = createButton("Sao chép khóa", new Color(70, 70, 70));
        copyButton.addActionListener(e -> textController.copyKey(keyArea));

        importButton = createButton("Nhập khóa từ file", Color.GREEN);
        importButton.addActionListener(e -> textController.importKey(keyArea));

        exportButton = createButton("Xuất khóa", new Color(70, 70, 70));
        exportButton.addActionListener(e -> textController.exportKey(keyArea, "txt"));

        removeButton = createButton("Xóa khóa", Color.RED);
        removeButton.addActionListener(e -> textController.removeKeyArea(keyArea));

        col.add(genButton);
        col.add(Box.createVerticalStrut(12));
        col.add(copyButton);
        col.add(Box.createVerticalStrut(12));
        col.add(importButton);
        col.add(Box.createVerticalStrut(12));
        col.add(exportButton);
        col.add(Box.createVerticalStrut(12));
        col.add(removeButton);
        col.add(Box.createVerticalGlue());

        return col;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isRollover() ? color.brighter() : color;
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), getModel().isRollover() ? 25 : 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(base);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 26));
        btn.setMaximumSize(new Dimension(110, 26));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    public void configureForAlgorithm(String algo) {
        String hint = switch (algo) {
            case "Caesar" -> "KHÓA — Số nguyên (0–25)";
            case "Affine" -> "KHÓA — Hai số a và b (a nguyên tố cùng 26)";
            case "Vigenere" -> "KHÓA — Chuỗi chữ cái (a–z)";
            case "Hill" -> "KHÓA — Ma trận 2×2";
            case "Thay thế" -> "KHÓA — Chuỗi 26 ký tự A–Z không trùng";
            case "Hoán vị" -> "KHÓA — Thứ tự cột (số, cách nhau dấu phẩy)";
            default -> "KHÓA";
        };
        keyHintLabel.setText(hint);
        keyArea.setText("");
    }
}