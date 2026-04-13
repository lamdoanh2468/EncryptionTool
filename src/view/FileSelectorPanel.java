package view;

import controller.FileController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FileSelectorPanel extends JPanel {
    private static final String[] SYMMETRIC_ALGOS = {"AES", "DES"};

    public final JComboBox<String> typeCombo;
    public final JComboBox<String> algoCombo;
    public final JComboBox<Integer> keySizeCombo;
    public final JTextArea keyArea;

    private final CardLayout keyCardLayout = new CardLayout();
    private final JPanel keyCardPanel = new JPanel(keyCardLayout);

    public JButton genButton;
    public JButton importButton;
    public JButton exportButton;

    public FileController fileController;

    public FileSelectorPanel(FileController fileController) {
        this.fileController = fileController;

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        // --- Row 1: Type + Algorithm ---
        JPanel topRow = new JPanel(new GridLayout(1, 2, 14, 0));
        topRow.setOpaque(false);

        typeCombo = createDropdown(new String[]{"Đối xứng", "Bất đối xứng"});
        algoCombo = createDropdown(SYMMETRIC_ALGOS);

        topRow.add(wrapLabeled("LOẠI MÃ HÓA", typeCombo));
        topRow.add(wrapLabeled("THUẬT TOÁN", algoCombo));
        add(topRow, BorderLayout.NORTH);

        // --- Key panel ---
        keyCardPanel.setOpaque(false);
        keyArea = makeKeyArea();
        keySizeCombo = new JComboBox<>(new Integer[]{128, 192, 256});
        keySizeCombo.setSelectedItem(256);

        keyCardPanel.add(buildSymmetricKeyCard(), "Symmetric");
        // TODO: nếu cần card Asymmetric riêng thì add ở đây
        add(keyCardPanel, BorderLayout.CENTER);

        // --- Listeners ---
        typeCombo.addActionListener(e -> onTypeChanged());
        algoCombo.addActionListener(e -> onAlgoChanged());

        // Initial state
        onTypeChanged();
        onAlgoChanged();
    }

    private JPanel buildSymmetricKeyCard() {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setOpaque(false);

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setOpaque(false);

        // Key size (chỉ hiển thị cho AES)
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        sizePanel.setOpaque(false);
        JLabel sizeLabel = new JLabel("Key Size (bits):");
        sizeLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        sizeLabel.setForeground(MainFrame.TXT_LABEL);
        sizePanel.add(sizeLabel);
        sizePanel.add(keySizeCombo);

        JLabel keyAlgo = new JLabel("KEY");
        keyAlgo.setName("cipherAlgo");
        keyAlgo.setFont(new Font("SansSerif", Font.BOLD, 10));
        keyAlgo.setForeground(MainFrame.TXT_LABEL);

        JScrollPane scroll = new JScrollPane(keyArea);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        JPanel keyContent = new JPanel(new BorderLayout(0, 6));
        keyContent.setOpaque(false);
        keyContent.add(sizePanel, BorderLayout.NORTH);
        keyContent.add(keyAlgo, BorderLayout.CENTER);
        keyContent.add(scroll, BorderLayout.SOUTH);

        left.add(keyContent, BorderLayout.CENTER);

        card.add(left, BorderLayout.CENTER);
        card.add(buildKeyButtonCol(), BorderLayout.EAST);
        return card;
    }

    private JPanel buildKeyButtonCol() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setBorder(new EmptyBorder(18, 0, 0, 0));

        genButton = createButton("Tạo khóa", MainFrame.ACCENT);
        genButton.addActionListener(e -> {
            String algo = (String) algoCombo.getSelectedItem();
            int keySize = "AES".equals(algo) ? (int) keySizeCombo.getSelectedItem() : 64; // DES
//            fileController.generateKey(algo, keySize, keyArea);
        });
        col.add(genButton);
        col.add(Box.createVerticalStrut(15));

        JButton cpy = createButton("Sao chép khóa", new Color(70, 70, 70));
//        cpy.addActionListener(e -> fileController.copyKey(keyArea));
        col.add(cpy);
        col.add(Box.createVerticalStrut(15));

        importButton = createButton("Nhập khóa từ file", new Color(70, 70, 70));
//        importButton.addActionListener(e -> fileController.importKey(keyArea));
        col.add(importButton);
        col.add(Box.createVerticalStrut(15));

        exportButton = createButton("Xuất khóa", new Color(70, 70, 70));
//        exportButton.addActionListener(e -> fileController.exportKey(keyArea, "key"));
        col.add(exportButton);

        col.add(Box.createVerticalGlue());
        return col;
    }

    private void onTypeChanged() {
        String type = (String) typeCombo.getSelectedItem();
        if ("Bất đối xứng".equals(type)) {
            algoCombo.removeAllItems();
            algoCombo.addItem("RSA");
            keyCardLayout.show(keyCardPanel, "Symmetric"); // tạm dùng card symmetric (có thể mở rộng sau)
        } else {
            if (algoCombo.getItemCount() != SYMMETRIC_ALGOS.length) {
                algoCombo.removeAllItems();
                for (String a : SYMMETRIC_ALGOS) algoCombo.addItem(a);
            }
            keyCardLayout.show(keyCardPanel, "Symmetric");
            onAlgoChanged();
        }
    }

    private void onAlgoChanged() {
        String algo = (String) algoCombo.getSelectedItem();
        if (algo == null) return;

        keySizeCombo.setEnabled("AES".equals(algo));
        updateKeyHint(algo);
    }

    private void updateKeyHint(String algo) {
        String algorithm = switch (algo) {
            case "AES" -> "KEY — 128/192/256 bit (hex hoặc Base64)";
            case "DES" -> "KEY — 64 bit (8 bytes)";
            case "RSA" -> "KEY — Public / Private Key";
            default -> "KEY";
        };
        selectCipherAlgo(keyCardPanel, "cipherAlgo", algorithm);
    }

    private void selectCipherAlgo(Container c, String name, String text) {
        for (Component comp : c.getComponents()) {
            if (name.equals(comp.getName()) && comp instanceof JLabel lbl) {
                lbl.setText(text);
                keyArea.setText("");
                return;
            }
            if (comp instanceof Container sub) selectCipherAlgo(sub, name, text);
        }
    }

    private JTextArea makeKeyArea() {
        JTextArea ta = new JTextArea(3, 25);
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

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isRollover() ? color.brighter() : color;
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(),
                        getModel().isRollover() ? 25 : 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(base);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(),
                        (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 26));
        btn.setMaximumSize(new Dimension(100, 26));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    private JComboBox<String> createDropdown(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBackground(MainFrame.BG_INPUT);
        cb.setForeground(MainFrame.TXT_MAIN);
        cb.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        cb.setPreferredSize(new Dimension(200, 32));
        return cb;
    }

    private JPanel wrapLabeled(String labelText, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(MainFrame.TXT_LABEL);
        p.add(label, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    // Getter tiện lợi khi kết nối với FilePanel
    public String getSelectedAlgorithm() {
        return (String) algoCombo.getSelectedItem();
    }

    public int getSelectedKeySize() {
        return (int) keySizeCombo.getSelectedItem();
    }

    public String getKeyText() {
        return keyArea.getText().trim();
    }
}