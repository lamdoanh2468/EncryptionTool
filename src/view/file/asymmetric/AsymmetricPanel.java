package view.file.asymmetric;

import controller.FileController;
import model.file.AFileAsymCipher;
import model.file.AFileSymCipher;
import view.MainFrame;
import view.file.FilePanelUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class AsymmetricPanel extends JPanel {

    public final JComboBox<Integer> rsaKeySizeCombo;
    public final JComboBox<String> rsaModeCombo;
    public final JComboBox<String> rsaPaddingCombo;
    public final JComboBox<String> asymSymAlgoCombo;
    public final JComboBox<Integer> hybridSymKeySizeCombo;
    public final JComboBox<String> hybridSymModeCombo;
    public final JComboBox<String> asymPaddingCombo;

    public final JTextArea publicKeyArea;
    public final JTextArea privateKeyArea;
    public final JTextArea symKeyArea;

    public JButton genKeyPairButton;
    public JButton genSymKeyButton;
    public JButton importPublicKeyButton;
    public JButton importPrivateKeyButton;
    public JButton exportKeyPairButton;
    public JButton removeAllButton;

    private final FileController fileController;

    public AsymmetricPanel(FileController fileController) {
        this.fileController = fileController;

        setLayout(new BorderLayout(8, 12));
        setOpaque(false);

        rsaKeySizeCombo = FilePanelUI.createIntegerDropdown(new Integer[]{1024, 2048, 3072, 4096});
        rsaKeySizeCombo.setSelectedItem(2048);
        rsaModeCombo = FilePanelUI.createDropdown(new String[]{"ECB"});
        rsaPaddingCombo = FilePanelUI.createDropdown(new String[]{
                "PKCS1Padding",
                "OAEPWithSHA-1AndMGF1Padding",
                "OAEPWithSHA-256AndMGF1Padding"
        });

        // Asym
        asymSymAlgoCombo = FilePanelUI.createDropdown(new String[]{"AES", "DES"});
        asymPaddingCombo = FilePanelUI.createDropdown(new String[]{"PKCS5Padding", "NoPadding"});

        // Hybrid
        hybridSymKeySizeCombo = FilePanelUI.createIntegerDropdown(new Integer[]{128, 192, 256});
        hybridSymKeySizeCombo.setSelectedItem(256);
        hybridSymModeCombo = FilePanelUI.createDropdown(new String[]{"ECB", "CBC", "CTR", "CFB", "OFB", "GCM"});

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
        rsaKeySizeCombo.removeAllItems();
        rsaPaddingCombo.removeAllItems();

        for (Integer size : asymCipher.getKeySizes()) {
            rsaKeySizeCombo.addItem(size);
        }
        for (String padding : asymCipher.getSupportedPaddings()) {
            rsaPaddingCombo.addItem(padding);
        }

        if (rsaKeySizeCombo.getItemCount() > 0) rsaKeySizeCombo.setSelectedIndex(rsaKeySizeCombo.getItemCount() - 1);
        if (rsaPaddingCombo.getItemCount() > 0) rsaPaddingCombo.setSelectedIndex(0);
    }

    public void bindActions(JComboBox<String> algoCombo) {

        genKeyPairButton.addActionListener(e -> {
            try {
                fileController.genPairKey(
                        (String) algoCombo.getSelectedItem(),
                        (Integer) rsaKeySizeCombo.getSelectedItem(),
                        publicKeyArea,
                        privateKeyArea
                );
            } catch (NoSuchAlgorithmException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        genSymKeyButton.addActionListener(e -> {
            try {
                fileController.genKey(fileController.getSymCipher((String) asymSymAlgoCombo.getSelectedItem()),
                        (Integer) hybridSymKeySizeCombo.getSelectedItem(), symKeyArea);
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
        });
        exportKeyPairButton.addActionListener(e -> {
            try {
                fileController.exportAllKeys(
                        fileController.getAsymCipher((String) algoCombo.getSelectedItem()),
                        fileController.getSymCipher((String) asymSymAlgoCombo.getSelectedItem())
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        removeAllButton.addActionListener(e->{
            publicKeyArea.setText("");
            privateKeyArea.setText("");
            symKeyArea.setText("");
        });
    }

    public String getSelectedHybridMode() {
        return (String) hybridSymModeCombo.getSelectedItem();
    }

    public String getSelectedHybridPadding() {
        return (String) asymPaddingCombo.getSelectedItem();
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
        rsaConfigPanel.add(FilePanelUI.createLabeledFieldPanel("KÍCH THƯỚC KHOÁ)", rsaKeySizeCombo));
        rsaConfigPanel.add(FilePanelUI.createLabeledFieldPanel("MODE OF OPERATION", rsaModeCombo));
        rsaConfigPanel.add(FilePanelUI.createLabeledFieldPanel("ASYM PADDING", rsaPaddingCombo));
        return rsaConfigPanel;
    }

    private JPanel createHybridSymConfigPanel() {
        JPanel symConfigPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        symConfigPanel.setOpaque(false);
        symConfigPanel.add(FilePanelUI.createLabeledFieldPanel("THUẬT TOÁN ĐỐI XỨNG", asymSymAlgoCombo));
        symConfigPanel.add(FilePanelUI.createLabeledFieldPanel("KÍCH THƯỚC KHOÁ", hybridSymKeySizeCombo));
        symConfigPanel.add(FilePanelUI.createLabeledFieldPanel("MODE OF OPERATION", hybridSymModeCombo));
        symConfigPanel.add(FilePanelUI.createLabeledFieldPanel("SYM PADDING", asymPaddingCombo));
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
        hybridSymModeCombo.addActionListener(e -> applyHybridCipherOptions());
        hybridSymKeySizeCombo.addActionListener(e -> applyHybridCipherOptions());

        asymPaddingCombo.addActionListener(e -> applyHybridCipherOptions());
        asymSymAlgoCombo.addActionListener(e -> {
            updateHybridUI();
            applyHybridCipherOptions();
        });
    }

    private void applyHybridCipherOptions() {
        String algo = (String) asymSymAlgoCombo.getSelectedItem();
        if (algo == null) return;

        AFileSymCipher cipher = fileController.getSymCipher(algo);
        String mode = getSelectedHybridMode();
        String padding = getSelectedHybridPadding();

        if (mode != null) cipher.setMode(mode);
        if (padding != null) cipher.setPadding(padding);
    }

    private void updateHybridUI() {
        String algo = (String) asymSymAlgoCombo.getSelectedItem();
        if (algo == null) return;

        AFileSymCipher cipher = fileController.getSymCipher(algo);
        hybridSymKeySizeCombo.removeAllItems();
        hybridSymModeCombo.removeAllItems();
        asymPaddingCombo.removeAllItems();

        for (Integer size : cipher.getKeySizes()) {
            hybridSymKeySizeCombo.addItem(size);
        }
        for (String mode : cipher.getSupportedModes()) {
            hybridSymModeCombo.addItem(mode);
        }
        for (String padding : cipher.getSupportedPaddings()) {
            asymPaddingCombo.addItem(padding);
        }

        if (hybridSymKeySizeCombo.getItemCount() > 0) hybridSymKeySizeCombo.setSelectedIndex(0);
        if (hybridSymModeCombo.getItemCount() > 0) hybridSymModeCombo.setSelectedIndex(0);
        if (asymPaddingCombo.getItemCount() > 0) asymPaddingCombo.setSelectedIndex(0);
    }
}

