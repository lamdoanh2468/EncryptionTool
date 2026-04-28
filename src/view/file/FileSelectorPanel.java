package view.file;

import controller.FileController;
import model.file.AFileAsymCipher;
import model.file.AFileSymCipher;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class FileSelectorPanel extends JPanel {
    private static final String[] SYMMETRIC_ALGOS = {"AES", "DES"};
    private static final String[] ASYMMETRIC_ALGOS = {"RSA"};
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


    // Key fields for symmetric encryption
    private JTextArea publicKeyArea;
    private JTextArea privateKeyArea;
    private JTextArea symKeyArea;
    private JPanel symKeySizeWrapper;
    private JPanel symPaddingWrapper;

    // Key fields for asymmetric encryption
    private JComboBox<String> asymSymAlgoCombo;
    private JComboBox<String> asymPaddingCombo;
    private JComboBox<String> rsaPaddingCombo;
    private JComboBox<Integer> rsaKeySizeCombo;
    private JComboBox<String> rsaModeCombo;
    private JComboBox<Integer> hybridSymKeySizeCombo;
    private JComboBox<String> hybridSymModeCombo;

    public FileSelectorPanel(FileController fileController) {
        this.fileController = fileController;

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        // Top row
        JPanel topRow = new JPanel(new GridLayout(1, 2, 14, 0));
        topRow.setOpaque(false);


        typeCombo = createDropdown(new String[]{"Đối xứng", "Bất đối xứng"});
        algoCombo = createDropdown(SYMMETRIC_ALGOS);

        topRow.add(createLabeledFieldPanel("LOẠI MÃ HÓA", typeCombo));
        topRow.add(createLabeledFieldPanel("THUẬT TOÁN", algoCombo));
        add(topRow, BorderLayout.NORTH);


        // Key card
        keyCardPanel.setOpaque(false);
        keyArea = createKeyTextArea();

        // Init 3 combo
        keySizeCombo = new JComboBox<>();
        keySizeCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        keySizeCombo.setBackground(MainFrame.BG_INPUT);
        keySizeCombo.setForeground(MainFrame.TXT_MAIN);
        keySizeCombo.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        keySizeCombo.setPreferredSize(new Dimension(180, 32));

        modeCombo = createDropdown(new String[]{});
        paddingCombo = createDropdown(new String[]{});

        keyCardPanel.add(createSymmetricKeyPanel(), "Symmetric");
        keyCardPanel.add(createAsymmetricKeyPanel(), "Asymmetric");

        add(keyCardPanel, BorderLayout.CENTER);

        // Handle default selection
        handleEncryptionTypeChange();

        // Add action listeners to symmetric
        typeCombo.addActionListener(e -> handleEncryptionTypeChange());
        algoCombo.addActionListener(e -> handleAlgorithmChange());
        modeCombo.addActionListener(e -> applySelectedCipherOptions());
        paddingCombo.addActionListener(e -> applySelectedCipherOptions());

        // add action listeners to asymmetric
        hybridSymModeCombo.addActionListener(e -> applyHybridCipherOptions());
        asymPaddingCombo.addActionListener(e -> applyHybridCipherOptions());
        asymSymAlgoCombo.addActionListener(e -> {
            updateHybridUI();
            applyHybridCipherOptions();
        });
        hybridSymKeySizeCombo.addActionListener(e -> applyHybridCipherOptions());
    }

    private void applyHybridCipherOptions() {
        String algo = (String) asymSymAlgoCombo.getSelectedItem();
        if (algo == null) return;

        AFileSymCipher cipher = fileController.getSymCipher(algo);

        String mode = (String) hybridSymModeCombo.getSelectedItem();
        String padding = (String) asymPaddingCombo.getSelectedItem();

        for (Integer size : cipher.getKeySizes()) {
            hybridSymKeySizeCombo.addItem(size);
        }
        if (mode != null) {
            cipher.setMode(mode);
        }
        if (padding != null) {
            cipher.setPadding(padding);

        }
    }

    private void updateHybridUI() {
        String algo = (String) asymSymAlgoCombo.getSelectedItem();
        if (algo == null) return;

        AFileSymCipher cipher = fileController.getSymCipher(algo);

        // Clear old data
        hybridSymKeySizeCombo.removeAllItems();
        hybridSymModeCombo.removeAllItems();
        asymPaddingCombo.removeAllItems();

        // Reload new data
        for (Integer size : cipher.getKeySizes()) {
            hybridSymKeySizeCombo.addItem(size);
        }

        for (String mode : cipher.getSupportedModes()) {
            hybridSymModeCombo.addItem(mode);
        }

        for (String padding : cipher.getSupportedPaddings()) {
            asymPaddingCombo.addItem(padding);
        }

        // set default
        if (hybridSymKeySizeCombo.getItemCount() > 0)
            hybridSymKeySizeCombo.setSelectedIndex(0);

        if (hybridSymModeCombo.getItemCount() > 0)
            hybridSymModeCombo.setSelectedIndex(0);

        if (asymPaddingCombo.getItemCount() > 0)
            asymPaddingCombo.setSelectedIndex(0);
    }

    private JPanel createSymmetricKeyPanel() {
        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setOpaque(false);

        JPanel configPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        configPanel.setOpaque(false);

        symKeySizeWrapper = createLabeledFieldPanel("KÍCH THƯỚC KHÓA", keySizeCombo);
        symPaddingWrapper = createLabeledFieldPanel("PADDING", paddingCombo);

        configPanel.add(symKeySizeWrapper);
        configPanel.add(createLabeledFieldPanel("MODE OF OPERATION", modeCombo));
        configPanel.add(symPaddingWrapper);

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
        card.add(createKeyActionButtonPanel(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel createAsymmetricKeyPanel() {
        JPanel card = new JPanel(new BorderLayout(8, 12));
        card.setOpaque(false);

        // ================= RSA CONFIG =================
        JPanel rsaConfigPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        rsaConfigPanel.setOpaque(false);

        rsaKeySizeCombo = new JComboBox<>(new Integer[]{1024, 2048, 3072, 4096});
        rsaKeySizeCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rsaKeySizeCombo.setBackground(MainFrame.BG_INPUT);
        rsaKeySizeCombo.setForeground(MainFrame.TXT_MAIN);
        rsaKeySizeCombo.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        rsaKeySizeCombo.setPreferredSize(new Dimension(180, 32));
        rsaKeySizeCombo.setSelectedItem(2048);

        rsaModeCombo = new JComboBox<>(new String[]{"ECB"});
        rsaModeCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rsaModeCombo.setBackground(MainFrame.BG_INPUT);
        rsaModeCombo.setForeground(MainFrame.TXT_MAIN);
        rsaModeCombo.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        rsaModeCombo.setPreferredSize(new Dimension(180, 32));

        rsaPaddingCombo = new JComboBox<>(new String[]{
                "PKCS1Padding",
                "OAEPWithSHA-1AndMGF1Padding",
                "OAEPWithSHA-256AndMGF1Padding"
        });
        rsaPaddingCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        rsaPaddingCombo.setBackground(MainFrame.BG_INPUT);
        rsaPaddingCombo.setForeground(MainFrame.TXT_MAIN);
        rsaPaddingCombo.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        rsaPaddingCombo.setPreferredSize(new Dimension(180, 32));

        rsaConfigPanel.add(createLabeledFieldPanel("KÍCH THƯỚC KHÓA (RSA)", rsaKeySizeCombo));
        rsaConfigPanel.add(createLabeledFieldPanel("MODE OF OPERATION", rsaModeCombo));
        rsaConfigPanel.add(createLabeledFieldPanel("ASYM PADDING", rsaPaddingCombo));

        // ================= SYMMETRIC CONFIG FOR HYBRID =================
        JPanel symConfigPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        symConfigPanel.setOpaque(false);

        asymSymAlgoCombo = new JComboBox<>(new String[]{"AES", "DES"});
        asymSymAlgoCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        asymSymAlgoCombo.setBackground(MainFrame.BG_INPUT);
        asymSymAlgoCombo.setForeground(MainFrame.TXT_MAIN);
        asymSymAlgoCombo.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        hybridSymKeySizeCombo = new JComboBox<>(new Integer[]{128, 192, 256});
        hybridSymKeySizeCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hybridSymKeySizeCombo.setBackground(MainFrame.BG_INPUT);
        hybridSymKeySizeCombo.setForeground(MainFrame.TXT_MAIN);
        hybridSymKeySizeCombo.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        hybridSymKeySizeCombo.setSelectedItem(256);

        hybridSymModeCombo = new JComboBox<>(new String[]{"ECB", "CBC", "CTR", "CFB", "OFB", "GCM"});
        hybridSymModeCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hybridSymModeCombo.setBackground(MainFrame.BG_INPUT);
        hybridSymModeCombo.setForeground(MainFrame.TXT_MAIN);
        hybridSymModeCombo.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        asymPaddingCombo = new JComboBox<>(new String[]{"PKCS5Padding", "NoPadding"});
        asymPaddingCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        asymPaddingCombo.setBackground(MainFrame.BG_INPUT);
        asymPaddingCombo.setForeground(MainFrame.TXT_MAIN);
        asymPaddingCombo.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        symConfigPanel.add(createLabeledFieldPanel("THUẬT TOÁN ĐỐI XỨNG", asymSymAlgoCombo));
        symConfigPanel.add(createLabeledFieldPanel("KÍCH THƯỚC KHÓA (SYM)", hybridSymKeySizeCombo));
        symConfigPanel.add(createLabeledFieldPanel("MODE", hybridSymModeCombo));
        symConfigPanel.add(createLabeledFieldPanel("PADDING (SYM)", asymPaddingCombo));

        // ================= KEY AREAS =================
        publicKeyArea = createKeyTextArea();
        privateKeyArea = createKeyTextArea();
        symKeyArea = createKeyTextArea();

        JScrollPane publicScroll = new JScrollPane(publicKeyArea);
        publicScroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        JScrollPane privateScroll = new JScrollPane(privateKeyArea);
        privateScroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        JScrollPane symKeyScroll = new JScrollPane(symKeyArea);
        symKeyScroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        JPanel keyPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        keyPanel.setOpaque(false);
        keyPanel.add(createLabeledFieldPanel("PUBLIC KEY", publicScroll));
        keyPanel.add(createLabeledFieldPanel("PRIVATE KEY", privateScroll));
        keyPanel.add(createLabeledFieldPanel("SYMMETRIC KEY ", symKeyScroll));

        // ================= WRAPPER =================
        JPanel topWrapper = new JPanel(new BorderLayout(0, 8));
        topWrapper.setOpaque(false);
        topWrapper.add(rsaConfigPanel, BorderLayout.NORTH);
        topWrapper.add(symConfigPanel, BorderLayout.CENTER);

        JPanel keyContent = new JPanel(new BorderLayout(0, 12));
        keyContent.setOpaque(false);
        keyContent.add(topWrapper, BorderLayout.NORTH);
        keyContent.add(keyPanel, BorderLayout.CENTER);

        card.add(keyContent, BorderLayout.CENTER);
        card.add(createAsymmetricKeyActionButtonPanel(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel createAsymmetricKeyActionButtonPanel() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(18, 0, 0, 0));

        JButton genKeyPairButton = createButton("Tạo cặp khóa", MainFrame.ACCENT);
        genKeyPairButton.addActionListener(e -> {
            try {
                fileController.genPairKey(
                        (String) algoCombo.getSelectedItem(),
                        (Integer) rsaKeySizeCombo.getSelectedItem(),
                        publicKeyArea,
                        privateKeyArea);
            } catch (NoSuchAlgorithmException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        row.add(genKeyPairButton);

        JButton importPublicButton = createButton("Nhập Public", new Color(75, 85, 99));
        row.add(importPublicButton);

        JButton importPrivateButton = createButton("Nhập Private", new Color(75, 85, 99));
        row.add(importPrivateButton);

        JButton exportButton = createButton("Xuất cặp khóa", new Color(16, 185, 129));
        row.add(exportButton);

        return row;
    }

    private JPanel createKeyActionButtonPanel() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(18, 0, 0, 0));

        genButton = createButton("Tạo khóa", MainFrame.ACCENT);
        genButton.addActionListener(e -> {
            String algo = (String) algoCombo.getSelectedItem();
            AFileSymCipher cipher = fileController.getSymCipher(algo);
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
            AFileSymCipher cipher = fileController.getSymCipher(algo);
            fileController.importKey(cipher, keyArea);
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

    private void handleEncryptionTypeChange() {
        String type = (String) typeCombo.getSelectedItem();
        algoCombo.removeAllItems();

        if ("Bất đối xứng".equals(type)) {
            for (String a : ASYMMETRIC_ALGOS)
                algoCombo.addItem(a);

            keyCardLayout.show(keyCardPanel, "Asymmetric");
        } else {
            for (String a : SYMMETRIC_ALGOS)
                algoCombo.addItem(a);

            keyCardLayout.show(keyCardPanel, "Symmetric");
        }
        handleAlgorithmChange();
    }

    private void handleAlgorithmChange() {
        String algo = (String) algoCombo.getSelectedItem();
        if (algo == null) return;

        String type = (String) typeCombo.getSelectedItem();
        paddingCombo.setSelectedItem(null);

        // Clear items
        keySizeCombo.removeAllItems();
        modeCombo.removeAllItems();
        paddingCombo.removeAllItems();

        if ("Bất đối xứng".equals(type)) {
            AFileAsymCipher asymCipher = fileController.getAsymCipher(algo);
            for (Integer size : asymCipher.getKeySizes()) {
                keySizeCombo.addItem(size);
            }
            for (String p : asymCipher.getSupportedPaddings()) {
                paddingCombo.addItem(p);
            }
            // Default selected if have only one value
            if (keySizeCombo.getItemCount() > 0) {
                keySizeCombo.setSelectedIndex(keySizeCombo.getItemCount() - 1);
            }
            if (paddingCombo.getItemCount() > 0) {
                paddingCombo.setSelectedIndex(0);
            }

        } else {
            AFileSymCipher symCipher = fileController.getSymCipher(algo);
            for (Integer size : symCipher.getKeySizes()) {
                keySizeCombo.addItem(size);
            }
            for (String m : symCipher.getSupportedModes()) {
                modeCombo.addItem(m);
            }
            for (String p : symCipher.getSupportedPaddings()) {
                paddingCombo.addItem(p);
            }
            if (keySizeCombo.getItemCount() > 0) {
                keySizeCombo.setSelectedIndex(keySizeCombo.getItemCount() - 1);
            }
            if (modeCombo.getItemCount() > 0) {
                modeCombo.setSelectedIndex(0);
            }
            if (paddingCombo.getItemCount() > 0) {
                paddingCombo.setSelectedIndex(0);
            }
        }
        updateKeyLabelByAlgorithm(algo);
        applySelectedCipherOptions();
    }

    private void applySelectedCipherOptions() {
        String algo = (String) algoCombo.getSelectedItem();
        if (algo == null) return;
        String type = (String) typeCombo.getSelectedItem();
        String selectedPadding = (String) paddingCombo.getSelectedItem();

        if ("Bất đối xứng".equals(type)) {
            AFileAsymCipher cipher = fileController.getAsymCipher(algo);
            if (selectedPadding != null) {
                cipher.setASymPadding(selectedPadding);
            }
        } else {
            AFileSymCipher cipher = fileController.getSymCipher(algo);
            String selectedMode = (String) modeCombo.getSelectedItem();
            if (selectedMode != null) {
                cipher.setMode(selectedMode);
            }
            if (selectedPadding != null) {
                cipher.setPadding(selectedPadding);
            }
        }
    }

    private void updateKeyLabelByAlgorithm(String algo) {
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

    private JTextArea createKeyTextArea() {
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

    private JPanel createLabeledFieldPanel(String labelText, JComponent comp) {
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