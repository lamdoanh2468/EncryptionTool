package view;

import controller.FileController;
import model.file.AFileCipher;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.security.NoSuchAlgorithmException;

public class FileSelectorPanel extends JPanel {
    private static final String[] SYMMETRIC_ALGOS = {"AES", "DES"};

    public final JComboBox<String> typeCombo;
    public final JComboBox<String> algoCombo;
    public final JComboBox<String> modeCombo;
    public final JComboBox<String> paddingCombo;
    public final JComboBox<Integer> keySizeCombo;
    public final JTextArea keyArea;

    private final CardLayout keyCardLayout = new CardLayout();
    private final JPanel keyCardPanel = new JPanel(keyCardLayout);

    public JButton genButton;
    public JButton copyButton;
    public JButton importButton;
    public JButton exportButton;
    public FileController fileController;

    public FileSelectorPanel(FileController fileController) {
        this.fileController = fileController;

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        // ==================== TOP ROW ====================
        JPanel topRow = new JPanel(new GridLayout(1, 2, 14, 0));
        topRow.setOpaque(false);

        typeCombo = createDropdown(new String[]{"Đối xứng", "Bất đối xứng"});
        algoCombo = createDropdown(SYMMETRIC_ALGOS);

        topRow.add(wrapLabeled("LOẠI MÃ HÓA", typeCombo));
        topRow.add(wrapLabeled("THUẬT TOÁN", algoCombo));
        add(topRow, BorderLayout.NORTH);


        // ==================== KEY CARD ====================
        keyCardPanel.setOpaque(false);
        keyArea = makeKeyArea();

        // Khởi tạo 3 combo
        keySizeCombo = new JComboBox<>();
        modeCombo = createDropdown(new String[]{});
        paddingCombo = createDropdown(new String[]{});

        keyCardPanel.add(buildSymmetricKeyCard(), "Symmetric");
        add(keyCardPanel, BorderLayout.CENTER);

        // Initial state
        onTypeChanged();
        onAlgoChanged();

        // ==================== LISTENERS ====================
        typeCombo.addActionListener(e -> onTypeChanged());
        algoCombo.addActionListener(e -> onAlgoChanged());
        modeCombo.addActionListener(e -> updateCipherConfig());
        paddingCombo.addActionListener(e -> updateCipherConfig());
    }

    private JPanel buildSymmetricKeyCard() {
        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setOpaque(false);

        JPanel configPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        configPanel.setOpaque(false);

        configPanel.add(wrapLabeled("KÍCH THƯỚC KHÓA", keySizeCombo));
        configPanel.add(wrapLabeled("MODE OF OPERATION", modeCombo));
        configPanel.add(wrapLabeled("PADDING", paddingCombo));

        JLabel keyLabel = new JLabel("KHÓA");
        keyLabel.setName("cipherAlgo");
        keyLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        keyLabel.setForeground(MainFrame.TXT_LABEL);

        JScrollPane scroll = new JScrollPane(keyArea);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        JPanel keyContent = new JPanel(new BorderLayout(0, 8));
        keyContent.setOpaque(false);
        keyContent.add(configPanel, BorderLayout.NORTH);
        keyContent.add(keyLabel, BorderLayout.CENTER);
        keyContent.add(scroll, BorderLayout.SOUTH);

        card.add(keyContent, BorderLayout.CENTER);
        card.add(buildKeyButtonRow(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildKeyButtonRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(18, 0, 0, 0));

        genButton = createButton("Tạo khóa", MainFrame.ACCENT);
        genButton.addActionListener(e -> {
            String algo = (String) algoCombo.getSelectedItem();
            AFileCipher cipher = fileController.getCipher(algo);
            Integer keySize = (Integer) keySizeCombo.getSelectedItem();
            try {
                fileController.genKey(cipher, keySize, keyArea);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        });
        row.add(genButton);

        copyButton = createButton("Sao chép khóa", new Color(99, 102, 241));
        copyButton.addActionListener(e -> {
          fileController.copyKey(keyArea);
        });
        row.add(copyButton);

        importButton = createButton("Nhập khóa từ file", new Color(75, 85, 99));
        importButton.addActionListener(e -> {
            String algo = (String) algoCombo.getSelectedItem();
            AFileCipher cipher = fileController.getCipher(algo);
            fileController.importKey(cipher,keyArea);
        });
        row.add(importButton);

        exportButton = createButton("Xuất khóa ra file", new Color(16, 185, 129));
        exportButton.addActionListener(e -> {
            try {
                fileController.exportKey();
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        });
        row.add(exportButton);


        return row;
    }

    private void onTypeChanged() {
        String type = (String) typeCombo.getSelectedItem();
        if ("Bất đối xứng".equals(type)) {
            algoCombo.removeAllItems();
            algoCombo.addItem("RSA");
        } else {
            if (algoCombo.getItemCount() != SYMMETRIC_ALGOS.length) {
                algoCombo.removeAllItems();
                for (String a : SYMMETRIC_ALGOS) algoCombo.addItem(a);
            }
        }
        keyCardLayout.show(keyCardPanel, "Symmetric");
        onAlgoChanged();
    }

    private void onAlgoChanged() {
        String algo = (String) algoCombo.getSelectedItem();
        if (algo == null) return;

        AFileCipher cipher = fileController.getCipher(algo);

        // 1. Key Size
        keySizeCombo.removeAllItems();
        for (Integer size : cipher.getKeySizes()) {
            keySizeCombo.addItem(size);
        }
        if (keySizeCombo.getItemCount() > 0) {
            keySizeCombo.setSelectedIndex(keySizeCombo.getItemCount() - 1); // chọn size lớn nhất mặc định
        }

        // 2. Mode
        modeCombo.removeAllItems();
        for (String m : cipher.getSupportedModes()) {
            modeCombo.addItem(m);
        }
        if (modeCombo.getItemCount() > 0) modeCombo.setSelectedIndex(0);

        // 3. Padding
        paddingCombo.removeAllItems();
        for (String p : cipher.getSupportedPaddings()) {
            paddingCombo.addItem(p);
        }
        if (paddingCombo.getItemCount() > 0) paddingCombo.setSelectedIndex(0);

        updateKeyDescription(algo);
        updateCipherConfig();   // đồng bộ ngay vào cipher
    }

    private void updateCipherConfig() {
        String algo = (String) algoCombo.getSelectedItem();
        if (algo == null) return;
        AFileCipher cipher = fileController.getCipher(algo);

        String selectedMode = (String) modeCombo.getSelectedItem();
        String selectedPadding = (String) paddingCombo.getSelectedItem();

        if (selectedMode != null) cipher.setMode(selectedMode);
        if (selectedPadding != null) cipher.setPadding(selectedPadding);
    }

    private void updateKeyDescription(String algo) {
        String text = switch (algo) {
            case "AES" -> "KEY — 128/192/256 bit";
            case "DES" -> "KEY — 64 bit";
            case "RSA" -> "KEY — Public / Private Key";
            default -> "KEY";
        };
        selectCipherAlgo(keyCardPanel, "cipherAlgo", text);
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
        JTextArea ta = new JTextArea(4, 30);
        ta.setFont(new Font("Monospaced", Font.PLAIN, 12));
        ta.setBackground(MainFrame.BG_INPUT);
        ta.setForeground(MainFrame.TXT_MAIN);
        ta.setCaretColor(MainFrame.TXT_MAIN);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(false);
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
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
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), getModel().isRollover() ? 25 : 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(base);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 26));
        return btn;
    }

    private JComboBox<String> createDropdown(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBackground(MainFrame.BG_INPUT);
        cb.setForeground(MainFrame.TXT_MAIN);
        cb.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        cb.setPreferredSize(new Dimension(180, 32));
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

}