package view.text;

import controller.text.TextController;
import org.jetbrains.annotations.NotNull;
import view.MainFrame;
import view.file.FilePanelUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TextSelectorPanel extends JPanel {
    private static final String[] SYMMETRIC_ALGOS =
            {"Caesar", "Affine", "Vigenere", "Hill", "Thay thế", "Hoán vị"};
    public final JComboBox<String> typeCombo;
    public final JComboBox<String> algoCombo;
    // Traditional symmetric keys
    public final JTextArea keyArea;
    // Asymmetric keys
    public JTextArea publicArea;
    public JTextArea privateArea;
    private final CardLayout keyCardLayout = new CardLayout();
    private final JPanel keyCardPanel = new JPanel(keyCardLayout);

    public JButton genButton;
    public JButton importButton;
    public JButton exportButton;

    public TextController textController;

    public TextSelectorPanel(TextController textController) {
        this.textController = textController;

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        // Row 1
        JPanel topRow = new JPanel(new GridLayout(1, 2, 14, 0));
        topRow.setOpaque(false);
        typeCombo = createDropdown(new String[]{"Đối xứng", "Bất đối xứng", "Hàm Băm"});
        algoCombo = createDropdown(SYMMETRIC_ALGOS);
        topRow.add(wrapLabeled("LOẠI MÃ HÓA", typeCombo));
        topRow.add(wrapLabeled("THUẬT TOÁN", algoCombo));
        add(topRow, BorderLayout.NORTH);

        // Key manager cards
        keyCardPanel.setOpaque(false);
        keyCardPanel.setPreferredSize(new Dimension(0, 130));
        keyCardPanel.setMinimumSize(new Dimension(0, 130));
        keyArea = makeKeyArea();

        keyCardPanel.add(buildSymmetricKeyCard(), "Symmetric");
        keyCardPanel.add(buildAsymmetricKeyCard(), "Asymmetric");
        keyCardPanel.add(buildHashCard(), "Hash");
        add(keyCardPanel, BorderLayout.CENTER);

        // Listeners
        typeCombo.addActionListener(e -> onTypeChanged());
        algoCombo.addActionListener(e -> onAlgoChanged());

        // Initial state
        onTypeChanged();
        onAlgoChanged();
    }

    private JPanel buildHashCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);

        JLabel hashInfo = new JLabel("Hàm băm không cần khóa");
        hashInfo.setFont(new Font("SansSerif", Font.BOLD, 16));
        hashInfo.setForeground(Color.RED);
        hashInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea helpText = buildHashTextHelp();

        card.add(hashInfo);
        card.add(helpText);
        return card;
    }

    @NotNull
    private static JTextArea buildHashTextHelp() {
        JTextArea helpText = new JTextArea(
                "- Ấn 'BĂM VĂN BẢN' để tạo mã băm mới.\n" +
                        "- Dán mã băm vào ô 'Kết quả' rồi ấn 'KIỂM TRA' để kiểm tra khớp với văn bản gốc."
        );
        helpText.setEditable(false);
        helpText.setOpaque(false);
        helpText.setLineWrap(true);
        helpText.setWrapStyleWord(true);
        helpText.setFont(new Font("SansSerif", Font.BOLD, 15));
        helpText.setBorder(new EmptyBorder(10, 0, 0, 0));
        return helpText;
    }

    private JPanel buildSymmetricKeyCard() {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setOpaque(false);

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setOpaque(false);

        JLabel keyAlgo = new JLabel("KEY");
        keyAlgo.setName("cipherAlgo");
        keyAlgo.setFont(new Font("SansSerif", Font.BOLD, 10));
        keyAlgo.setForeground(MainFrame.TXT_LABEL);

        JScrollPane scroll = new JScrollPane(keyArea);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        left.add(keyAlgo, BorderLayout.NORTH);
        left.add(scroll, BorderLayout.CENTER);

        card.add(left, BorderLayout.CENTER);
        card.add(buildKeyButtonCol(), BorderLayout.EAST);
        return card;
    }
    private JPanel buildAsymmetricKeyCard() {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setOpaque(false);

        // PUBLIC KEY
        JPanel publicPanel = new JPanel(new BorderLayout(0, 4));
        publicPanel.setOpaque(false);
        JLabel lblPublic = new JLabel("PUBLIC KEY");
        lblPublic.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblPublic.setForeground(MainFrame.TXT_LABEL);

        publicArea = makeKeyArea();
        publicArea.setRows(3);
        JScrollPane scrollPub = new JScrollPane(publicArea);

        publicPanel.add(lblPublic, BorderLayout.NORTH);
        publicPanel.add(scrollPub, BorderLayout.CENTER);

        // PRIVATE KEY
        JPanel privatePanel = new JPanel(new BorderLayout(0, 4));
        privatePanel.setOpaque(false);
        JLabel lblPrivate = new JLabel("PRIVATE KEY");
        lblPrivate.setFont(new Font("SansSerif", Font.BOLD, 10));
        lblPrivate.setForeground(MainFrame.TXT_LABEL);

        privateArea = makeKeyArea();
        privateArea.setRows(3);
        JScrollPane scrollPri = new JScrollPane(privateArea);

        privatePanel.add(lblPrivate, BorderLayout.NORTH);
        privatePanel.add(scrollPri, BorderLayout.CENTER);

        // BUTTON COLUMN
        JPanel btnCol = new JPanel();
        btnCol.setLayout(new BoxLayout(btnCol, BoxLayout.Y_AXIS));
        btnCol.setOpaque(false);
        btnCol.setBorder(new EmptyBorder(18, 0, 0, 0));

        // Tạo cặp khóa
        JButton btnGen = createButton("Tạo cặp khóa", MainFrame.ACCENT);
        btnGen.addActionListener(e -> textController.genKeyPair(publicArea, privateArea));
        btnCol.add(btnGen);
        btnCol.add(Box.createVerticalStrut(6));

        // Copy Public
        JButton btnCopyPub = createButton("Copy Public", new Color(70, 70, 70));
        btnCopyPub.addActionListener(e -> textController.copyKey(publicArea));
        btnCol.add(btnCopyPub);
        btnCol.add(Box.createVerticalStrut(6));

        // Copy Private
        JButton btnCopyPri = createButton("Copy Private", new Color(70, 70, 70));
        btnCopyPri.addActionListener(e -> textController.copyKey(privateArea));
        btnCol.add(btnCopyPri);
        btnCol.add(Box.createVerticalStrut(6));

        // Import Public
        JButton btnImportPub = createButton("Import Public", new Color(70, 70, 70));
        btnImportPub.addActionListener(e -> textController.importKey(publicArea));
        btnCol.add(btnImportPub);
        btnCol.add(Box.createVerticalStrut(6));

        // Import Private
        JButton btnImportPri = createButton("Import Private", new Color(70, 70, 70));
        btnImportPri.addActionListener(e -> textController.importKey(privateArea));
        btnCol.add(btnImportPri);

        // Main layout
        JPanel main = new JPanel(new GridLayout(1, 2, 8, 0));
        main.setOpaque(false);
        main.add(publicPanel);
        main.add(privatePanel);

        card.add(main, BorderLayout.CENTER);
        card.add(btnCol, BorderLayout.EAST);

        return card;
    }


    //  Key buttons

    private JPanel buildKeyButtonCol() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setBorder(new EmptyBorder(18, 0, 0, 0)); // align with textarea (below label)

        //Generate button
        genButton = createButton("Tạo khóa", MainFrame.ACCENT);
        genButton.addActionListener(e -> {
            String algo = (String) algoCombo.getSelectedItem();
            textController.generateKey(textController.getCipher(algo), keyArea);
        });
        col.add(genButton);
        col.add(Box.createVerticalStrut(15));

        //Copy button
        JButton cpy = createButton("Sao chép khóa", new Color(70, 70, 70));
        cpy.addActionListener(e ->
                textController.copyKey(keyArea));
        col.add(cpy);
        col.add(Box.createVerticalStrut(15));

        //Import Button
        importButton = createButton("Nhập khóa từ file ", new Color(70, 70, 70));
        importButton.addActionListener(e -> textController.importKey(keyArea));
        col.add(importButton);
        col.add(Box.createVerticalStrut(15));

        //Export Button
        exportButton = createButton("Xuất khóa", new Color(70, 70, 70));
        exportButton.addActionListener(e -> textController.exportKey(keyArea, "txt"));
        col.add(exportButton);


        col.add(Box.createVerticalGlue());
        return col;
    }


    private void onTypeChanged() {
        String type = (String) typeCombo.getSelectedItem();

        if ("Hàm Băm".equals(type)) {
            // Hash mode
            algoCombo.removeAllItems();
            algoCombo.addItem("MD5");
            algoCombo.addItem("SHA-1");
            algoCombo.addItem("SHA-256");
            algoCombo.addItem("SHA-512");
            algoCombo.addItem("SHA3-256");
            keyCardLayout.show(keyCardPanel, "Hash");
            textController.toggleOutputArea(true);

        } else if ("Bất đối xứng".equals(type)) {
            algoCombo.removeAllItems();
            algoCombo.addItem("RSA");
            keyCardLayout.show(keyCardPanel, "Asymmetric");

        } else {
            // Đối xứng
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

        String currentType = (String) typeCombo.getSelectedItem();

        if ("Hàm Băm".equals(currentType)) {
            keyCardLayout.show(keyCardPanel, "Hash");
            return;
        }

        keyCardLayout.show(keyCardPanel, "Symmetric");
        updateKeyHint(algo);
    }

    private void updateKeyHint(String algo) {
        String algorithm = switch (algo) {
            case "Caesar" -> "KHÓA  —  Số nguyên (0–25)";
            case "Affine" -> "KHÓA  —  Hai số a và b (a phải nguyên tố cùng nhau với 26)";
            case "Vigenere" -> "KHÓA  —  Chuỗi chữ cái (a–z)";
            case "Hill" -> "KHÓA  —  Ma trận 2×2 (các phần tử là số)";
            case "Thay thế" -> "KHÓA  —  Chuỗi 26 ký tự A–Z không trùng lặp";
            case "Hoán vị" -> "KHÓA  —  Thứ tự cột (các số, cách nhau bằng dấu phẩy)";
            default -> "KHÓA";
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

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
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
        btn.setPreferredSize(new Dimension(90, 24));
        btn.setMaximumSize(new Dimension(90, 24));
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
        return FilePanelUI.createLabeledFieldPanel(labelText, comp);
    }
}