package view.file.asymmetric;

import controller.file.AsymmetricFileController;
import controller.file.FileController;
import controller.file.SymmetricFileController;
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
    private final AsymmetricFileController asymmetricFileController;
    private final SymmetricFileController symmetricFileController;
    private final FileController fileController;
    public JButton genKeyPairButton;
    public JButton genSymKeyButton;
    public JButton importPublicKeyButton;
    public JButton importPrivateKeyButton;
    public JButton exportKeyPairButton;
    public JButton removeAllButton;

    public AsymmetricPanel(FileController fileController, AsymmetricFileController asymmetricFileController
            , SymmetricFileController symmetricFileController) {
        this.fileController = fileController;
        this.asymmetricFileController = asymmetricFileController;
        this.symmetricFileController = symmetricFileController;
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

    public void bindButtonActions(JComboBox<String> algoCombo) {

        genKeyPairButton.addActionListener(e -> {
            try {
                asymmetricFileController.genPairKey(
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
                symmetricFileController.genKey(fileController.getSymCipher((String) symAlgoCombo.getSelectedItem()),
                        (Integer) symKeySizeCombo.getSelectedItem(), symKeyArea);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        });

        importPublicKeyButton.addActionListener(e -> {
            asymmetricFileController.importPublicKey(
                    fileController.getAsymCipher((String) algoCombo.getSelectedItem()),
                    publicKeyArea
            );
        });
        importPrivateKeyButton.addActionListener(e -> {
            String algo = (String) algoCombo.getSelectedItem();
            AFileAsymCipher asymCipher = fileController.getAsymCipher(algo);

            boolean success = asymmetricFileController.importPrivateKey(
                    asymCipher,
                    privateKeyArea
            );
            if (success) {
                try {
                    asymmetricFileController.setAsymmetricCipherInfo(this);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        });
        exportKeyPairButton.addActionListener(e -> {
            try {
                asymmetricFileController.exportKeyPair(
                        fileController.getAsymCipher((String) algoCombo.getSelectedItem()),
                        (String) asymModeCombo.getSelectedItem(),
                        (String) asymPaddingCombo.getSelectedItem()
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        removeAllButton.addActionListener(e -> {
            fileController.removeKeyArea(publicKeyArea, privateKeyArea, symKeyArea);
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
        JPanel row = new JPanel(new GridLayout(1, 7, 8, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(15, 10, 15, 10));

        genKeyPairButton = FilePanelUI.createOutlineButton("Tạo cặp khoá", MainFrame.ACCENT);
        genSymKeyButton = FilePanelUI.createOutlineButton("Tạo khoá đối xứng", MainFrame.ACCENT);
        exportKeyPairButton = FilePanelUI.createOutlineButton("Xuất cặp khoá ", new Color(16, 185, 129));
        importPublicKeyButton = FilePanelUI.createOutlineButton("Nhập public key", new Color(75, 85, 99));
        importPrivateKeyButton = FilePanelUI.createOutlineButton("Nhập private key", new Color(75, 85, 99));
        removeAllButton = FilePanelUI.createOutlineButton("Xóa tất cả", new Color(255, 65, 54));

        row.add(genKeyPairButton);
        row.add(genSymKeyButton);
        row.add(exportKeyPairButton);
        row.add(importPublicKeyButton);
        row.add(importPrivateKeyButton);
        row.add(removeAllButton);

        return row;
    }

    private void bindHybridActions() {

        // Asymmetric key size
        asymKeySizeCombo.addActionListener(e -> clearAsymKeys());

        // Symmetric key size, mode, padding and algorithm
        symModeCombo.addActionListener(e -> {
            applyHybridCipherOptions();
            clearSymKey();
        });
        symKeySizeCombo.addActionListener(e -> {
            applyHybridCipherOptions();
            clearSymKey();
        });
        symPaddingCombo.addActionListener(e ->
        {
            applyHybridCipherOptions();
            clearSymKey();
        });
        symAlgoCombo.addActionListener(e -> {
            updateHybridUI();
            clearSymKey();
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

    public void clearAsymKeys() {
        boolean hasKeyPairs = !publicKeyArea.getText().isEmpty() || !privateKeyArea.getText().isEmpty();
        if (hasKeyPairs) {
            JOptionPane.showMessageDialog(this, " Đã xóa tất cả các khoá và thông tin thuật toán bất đối xứng", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            publicKeyArea.setText("");
            privateKeyArea.setText("");
            fileController.currentPublicKey = null;
            fileController.currentPrivateKey = null;
            fileController.updateStatus("Bạn vừa mới thay đổi tham số thuật toán , vui lòng tạo lại khoá bất đối xứng");
        }
    }

    public void clearSymKey() {
        boolean hasSymKey = !symKeyArea.getText().isEmpty();
        if (hasSymKey) {
            JOptionPane.showMessageDialog(this, " Đã xóa tất cả các khoá và thông tin thuật toán đối xứng", "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            symKeyArea.setText("");
            fileController.currentKey = null;
            fileController.updateStatus("Bạn vừa mới thay đổi tham số thuật toán , vui lòng tạo lại khoá đối xứng");
        }
    }

    public void setCombosEnabled(boolean enabled) {
        //  Asymmetric combos
        this.asymKeySizeCombo.setEnabled(enabled);
        this.asymModeCombo.setEnabled(enabled);
        this.asymPaddingCombo.setEnabled(enabled);
        //  Symmetric combos
        this.symAlgoCombo.setEnabled(enabled);
        this.symKeySizeCombo.setEnabled(enabled);
        this.symModeCombo.setEnabled(enabled);
        this.symPaddingCombo.setEnabled(enabled);
        //  Buttons
        this.genKeyPairButton.setEnabled(enabled);
        this.genSymKeyButton.setEnabled(enabled);
        this.importPublicKeyButton.setEnabled(enabled);
        this.importPrivateKeyButton.setEnabled(enabled);
        this.exportKeyPairButton.setEnabled(enabled);
        this.removeAllButton.setEnabled(enabled);
    }
}

