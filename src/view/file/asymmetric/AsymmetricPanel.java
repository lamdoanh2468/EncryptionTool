package view.file.asymmetric;

import controller.FileController;
import model.file.AFileAsymCipher;
import model.file.AFileSymCipher;
import model.file.config.AsymmetricFiletConfig;
import view.MainFrame;
import view.file.FilePanelUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class AsymmetricPanel extends JPanel {

    // Asymmetric
    public final JComboBox<Integer> asymKeySizeCombo;
    public final JComboBox<String> asymModeCombo;
    public final JComboBox<String> asymPaddingCombo;

    // Symmetric
    public final JComboBox<String> symAlgoCombo;
    public final JComboBox<String> symPaddingCombo;
    public final JComboBox<Integer> symKeySizeCombo;
    public final JComboBox<String> symModeCombo;

    public final JTextArea publicKeyArea;
    public final JTextArea privateKeyArea;
    public final JTextArea symKeyArea;
    private final FileController fileController;

    public JButton genKeyPairButton;
    public JButton genSymKeyButton;
    public JButton importPublicKeyButton;
    public JButton importPrivateKeyButton;
    public JButton importSymKeyButton;
    public JButton exportKeyPairButton;
    public JButton removeAllButton;

    public AsymmetricPanel(FileController fileController) {
        this.fileController = fileController;

        setLayout(new BorderLayout(8, 12));
        setOpaque(false);

        asymKeySizeCombo = FilePanelUI.createIntegerDropdown(new Integer[]{1024, 2048, 3072, 4096});
        asymKeySizeCombo.setSelectedItem(2048);
        asymModeCombo = FilePanelUI.createDropdown(new String[]{"ECB"});
        asymPaddingCombo = FilePanelUI.createDropdown(new String[]{
                "PKCS1Padding",
                "OAEPWithSHA-1AndMGF1Padding",
                "OAEPWithSHA-256AndMGF1Padding"
        });

        // Asym
        symAlgoCombo = FilePanelUI.createDropdown(new String[]{"AES", "DES"});
        symPaddingCombo = FilePanelUI.createDropdown(new String[]{"PKCS5Padding", "NoPadding"});

        // Hybrid
        symKeySizeCombo = FilePanelUI.createIntegerDropdown(new Integer[]{128, 192, 256});
        symKeySizeCombo.setSelectedItem(256);
        symModeCombo = FilePanelUI.createDropdown(new String[]{"ECB", "CBC", "CTR", "CFB", "OFB"});

        publicKeyArea = FilePanelUI.createKeyTextArea();
        privateKeyArea = FilePanelUI.createKeyTextArea();
        symKeyArea = FilePanelUI.createKeyTextArea();

        add(createContentPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        bindHybridActions();
        updateHybridUI();
    }

    public void configureForAlgorithm(String algo) {
        if (algo == null) return;

        AFileAsymCipher asymCipher = fileController.getAsymCipher(algo);
        asymKeySizeCombo.removeAllItems();
        asymPaddingCombo.removeAllItems();

        for (Integer size : asymCipher.getKeySizes()) {
            asymKeySizeCombo.addItem(size);
        }
        for (String padding : asymCipher.getSupportedPaddings()) {
            asymPaddingCombo.addItem(padding);
        }

        if (asymKeySizeCombo.getItemCount() > 0) asymKeySizeCombo.setSelectedIndex(asymKeySizeCombo.getItemCount() - 1);
        if (asymPaddingCombo.getItemCount() > 0) asymPaddingCombo.setSelectedIndex(0);
    }

    public void bindActions(JComboBox<String> algoCombo) {

        genKeyPairButton.addActionListener(e -> {
            try {
                fileController.genPairKey(
                        (String) algoCombo.getSelectedItem(),
                        (Integer) asymKeySizeCombo.getSelectedItem(),
                        publicKeyArea,
                        privateKeyArea
                );
            } catch (NoSuchAlgorithmException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        genSymKeyButton.addActionListener(e -> {
            try {
                fileController.genKey(fileController.getSymCipher((String) symAlgoCombo.getSelectedItem()),
                        (Integer) symKeySizeCombo.getSelectedItem(), symKeyArea);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        });

        importPublicKeyButton.addActionListener(e -> {
            fileController.importPublicKey(
                    fileController.getAsymCipher((String) algoCombo.getSelectedItem()),
                    publicKeyArea
            );
        });
        importPrivateKeyButton.addActionListener(e -> {
            fileController.importPrivateKey(
                    fileController.getAsymCipher((String) algoCombo.getSelectedItem()),
                    privateKeyArea
            );
            try {
                fileController.setAsymmetricCipherInfo(this);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        importSymKeyButton.addActionListener(e -> {
            fileController.importKey(fileController.getSymCipher((String)symAlgoCombo.getSelectedItem()),symKeyArea);
        });
        exportKeyPairButton.addActionListener(e -> {
            try {
                fileController.exportAllKeys(
                        fileController.getAsymCipher((String) algoCombo.getSelectedItem()),
                        (String) asymModeCombo.getSelectedItem(),
                        (String) asymPaddingCombo.getSelectedItem(),
                        fileController.getSymCipher((String) symAlgoCombo.getSelectedItem())
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        removeAllButton.addActionListener(e -> {
            publicKeyArea.setText("");
            privateKeyArea.setText("");
            symKeyArea.setText("");
        });
    }

    public String getSelectedHybridMode() {
        return (String) symModeCombo.getSelectedItem();
    }

    public String getSelectedHybridPadding() {
        return (String) symPaddingCombo.getSelectedItem();
    }

    private JPanel createContentPanel() {
        JPanel topWrapper = new JPanel(new BorderLayout(0, 8));
        topWrapper.setOpaque(false);
        topWrapper.add(createRsaConfigPanel(), BorderLayout.NORTH);
        topWrapper.add(createHybridSymConfigPanel(), BorderLayout.CENTER);

        JPanel keyPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        keyPanel.setOpaque(false);
        keyPanel.add(FilePanelUI.createLabeledFieldPanel("PUBLIC KEY", FilePanelUI.createBorderedScrollPane(publicKeyArea)));
        keyPanel.add(FilePanelUI.createLabeledFieldPanel("PRIVATE KEY", FilePanelUI.createBorderedScrollPane(privateKeyArea)));
        keyPanel.add(FilePanelUI.createLabeledFieldPanel("SYMMETRIC KEY", FilePanelUI.createBorderedScrollPane(symKeyArea)));

        JPanel keyContent = new JPanel(new BorderLayout(0, 12));
        keyContent.setOpaque(false);
        keyContent.add(topWrapper, BorderLayout.NORTH);
        keyContent.add(keyPanel, BorderLayout.CENTER);
        return keyContent;
    }

    private JPanel createRsaConfigPanel() {
        JPanel rsaConfigPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        rsaConfigPanel.setOpaque(false);
        rsaConfigPanel.add(FilePanelUI.createLabeledFieldPanel("KÍCH THƯỚC KHOÁ)", asymKeySizeCombo));
        rsaConfigPanel.add(FilePanelUI.createLabeledFieldPanel("MODE OF OPERATION", asymModeCombo));
        rsaConfigPanel.add(FilePanelUI.createLabeledFieldPanel("ASYM PADDING", asymPaddingCombo));
        return rsaConfigPanel;
    }

    private JPanel createHybridSymConfigPanel() {
        JPanel symConfigPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        symConfigPanel.setOpaque(false);
        symConfigPanel.add(FilePanelUI.createLabeledFieldPanel("THUẬT TOÁN ĐỐI XỨNG", symAlgoCombo));
        symConfigPanel.add(FilePanelUI.createLabeledFieldPanel("KÍCH THƯỚC KHOÁ", symKeySizeCombo));
        symConfigPanel.add(FilePanelUI.createLabeledFieldPanel("MODE OF OPERATION", symModeCombo));
        symConfigPanel.add(FilePanelUI.createLabeledFieldPanel("SYM PADDING", symPaddingCombo));
        return symConfigPanel;
    }

    private JPanel createButtonPanel() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(18, 0, 0, 0));

        genKeyPairButton = FilePanelUI.createOutlineButton("Tạo cặp khoá", MainFrame.ACCENT);
        genSymKeyButton = FilePanelUI.createOutlineButton("Tạo khoá đối xứng", MainFrame.ACCENT);
        exportKeyPairButton = FilePanelUI.createOutlineButton("Xuất các khoá ", new Color(16, 185, 129));
        importPublicKeyButton = FilePanelUI.createOutlineButton("Nhập public key", new Color(75, 85, 99));
        importPrivateKeyButton = FilePanelUI.createOutlineButton("Nhập private key", new Color(75, 85, 99));
        importSymKeyButton =FilePanelUI.createOutlineButton("Nhập sym key", new Color(75, 85, 99));
        removeAllButton = FilePanelUI.createOutlineButton("Xóa tất cả", new Color(255, 65, 54));

        row.add(genKeyPairButton);
        row.add(genSymKeyButton);
        row.add(exportKeyPairButton);
        row.add(importPublicKeyButton);
        row.add(importPrivateKeyButton);
        row.add(importSymKeyButton);
        row.add(removeAllButton);

        return row;
    }

    private void bindHybridActions() {
        symModeCombo.addActionListener(e -> applyHybridCipherOptions());
        symKeySizeCombo.addActionListener(e -> applyHybridCipherOptions());

        symPaddingCombo.addActionListener(e -> applyHybridCipherOptions());
        symAlgoCombo.addActionListener(e -> {
            updateHybridUI();
            applyHybridCipherOptions();
        });
    }

    private void applyHybridCipherOptions() {
        String algo = (String) symAlgoCombo.getSelectedItem();
        if (algo == null) return;

        AFileSymCipher cipher = fileController.getSymCipher(algo);
        String mode = getSelectedHybridMode();
        String padding = getSelectedHybridPadding();

        if (mode != null) cipher.setMode(mode);
        if (padding != null) cipher.setPadding(padding);
    }

    private void updateHybridUI() {
        String algo = (String) symAlgoCombo.getSelectedItem();
        if (algo == null) return;

        AFileSymCipher cipher = fileController.getSymCipher(algo);
        symKeySizeCombo.removeAllItems();
        symModeCombo.removeAllItems();
        symPaddingCombo.removeAllItems();

        for (Integer size : cipher.getKeySizes()) {
            symKeySizeCombo.addItem(size);
        }
        for (String mode : cipher.getSupportedModes()) {
            symModeCombo.addItem(mode);
        }
        for (String padding : cipher.getSupportedPaddings()) {
            symPaddingCombo.addItem(padding);
        }

        if (symKeySizeCombo.getItemCount() > 0) symKeySizeCombo.setSelectedIndex(0);
        if (symModeCombo.getItemCount() > 0) symModeCombo.setSelectedIndex(0);
        if (symPaddingCombo.getItemCount() > 0) symPaddingCombo.setSelectedIndex(0);
    }

    public AsymmetricFiletConfig buildEncryptConfig(String asymAlgo,
                                                    File selectedFile
    ) {
        return new AsymmetricFiletConfig(
                fileController.getAsymCipher(asymAlgo),
                fileController.getSymCipher((String) symAlgoCombo.getSelectedItem()),
                (String) asymModeCombo.getSelectedItem(),
                (String) asymPaddingCombo.getSelectedItem(),
                (String) symModeCombo.getSelectedItem(),
                (String) symPaddingCombo.getSelectedItem(),
                selectedFile
        );
    }
}

