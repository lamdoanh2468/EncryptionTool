package view.file.symmetric;

import controller.file.FileController;
import controller.file.SymmetricFileController;
import model.file.AFileSymCipher;
import view.MainFrame;
import view.file.FilePanelUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.security.NoSuchAlgorithmException;

public class SymmetricPanel extends JPanel {

    public final JComboBox<Integer> keySizeCombo;
    public final JComboBox<String> modeCombo;
    public final JComboBox<String> paddingCombo;
    public final JTextArea keyArea;

    public final JButton genButton;
    public final JButton copyButton;
    public final JButton importButton;
    public final JButton exportButton;
    public final JButton removeKeyButton;

    private final SymmetricFileController symmetricFileController;
    private final FileController fileController;
    private final JLabel keyLabel;

    public SymmetricPanel(FileController fileController, SymmetricFileController symmetricFileController) {
        this.fileController = fileController;
        this.symmetricFileController = symmetricFileController;

        setLayout(new BorderLayout(8, 12));
        setOpaque(false);

        keySizeCombo = FilePanelUI.createIntegerDropdown(new Integer[]{});
        modeCombo = FilePanelUI.createDropdown(new String[]{});
        paddingCombo = FilePanelUI.createDropdown(new String[]{});
        keyArea = FilePanelUI.createKeyTextArea();

        keyLabel = new JLabel("KHÓA");
        keyLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        keyLabel.setForeground(MainFrame.TXT_LABEL);

        genButton = FilePanelUI.createOutlineButton("Tạo khóa", MainFrame.ACCENT);
        copyButton = FilePanelUI.createOutlineButton("Sao chép khóa", new Color(99, 102, 241));
        importButton = FilePanelUI.createOutlineButton("Nhập khóa từ file", new Color(75, 85, 99));
        exportButton = FilePanelUI.createOutlineButton("Xuất khóa ra file", new Color(16, 185, 129));
        removeKeyButton = FilePanelUI.createOutlineButton("Xoá khoá", new Color(255, 65, 54));

        add(createContentPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    public void configureForAlgorithm(String algo) {
        if (algo == null) return;

        AFileSymCipher cipher = fileController.getSymCipher(algo);
        keySizeCombo.removeAllItems();
        modeCombo.removeAllItems();
        paddingCombo.removeAllItems();

        for (Integer size : cipher.getKeySizes()) {
            keySizeCombo.addItem(size);
        }
        for (String mode : cipher.getSupportedModes()) {
            modeCombo.addItem(mode);
        }
        for (String padding : cipher.getSupportedPaddings()) {
            paddingCombo.addItem(padding);
        }

        if (keySizeCombo.getItemCount() > 0) keySizeCombo.setSelectedIndex(keySizeCombo.getItemCount() - 1);
        if (modeCombo.getItemCount() > 0) modeCombo.setSelectedIndex(0);
        if (paddingCombo.getItemCount() > 0) paddingCombo.setSelectedIndex(0);

        updateKeyLabel(algo);
        applySelectedCipherOptions(algo);
    }

    public void applySelectedCipherOptions(String algo) {
        if (algo == null) return;

        AFileSymCipher cipher = fileController.getSymCipher(algo);
        String selectedMode = getSelectedMode();
        String selectedPadding = getSelectedPadding();

        if (selectedMode != null) cipher.setMode(selectedMode);
        if (selectedPadding != null) cipher.setPadding(selectedPadding);
    }

    public void bindActions(JComboBox<String> algoCombo) {
        modeCombo.addActionListener(e -> applySelectedCipherOptions((String) algoCombo.getSelectedItem()));
        paddingCombo.addActionListener(e -> applySelectedCipherOptions((String) algoCombo.getSelectedItem()));

        genButton.addActionListener(e -> {
            String algo = (String) algoCombo.getSelectedItem();
            AFileSymCipher cipher = fileController.getSymCipher(algo);
            Integer keySize = (Integer) keySizeCombo.getSelectedItem();
            try {
                symmetricFileController.genKey(cipher, keySize, keyArea);
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        });

        copyButton.addActionListener(e -> fileController.copyKey(keyArea));

        importButton.addActionListener(e -> {
            String algo = (String) algoCombo.getSelectedItem();
            AFileSymCipher cipher = fileController.getSymCipher(algo);
            symmetricFileController.importKey(cipher, keyArea);
        });

        exportButton.addActionListener(e -> {
            try {
                symmetricFileController.exportKey();
            } catch (NoSuchAlgorithmException ex) {
                throw new RuntimeException(ex);
            }
        });
        removeKeyButton.addActionListener(e -> fileController.removeKeyArea(keyArea));
    }

    public String getSelectedMode() {
        return (String) modeCombo.getSelectedItem();
    }

    public String getSelectedPadding() {
        return (String) paddingCombo.getSelectedItem();
    }

    private JPanel createContentPanel() {
        JPanel configPanel = new JPanel(new GridLayout(1, 3, 14, 0));
        configPanel.setOpaque(false);
        configPanel.add(FilePanelUI.createLabeledFieldPanel("KÍCH THƯỚC KHÓA", keySizeCombo));
        configPanel.add(FilePanelUI.createLabeledFieldPanel("MODE OF OPERATION", modeCombo));
        configPanel.add(FilePanelUI.createLabeledFieldPanel("PADDING", paddingCombo));

        JPanel keyContent = new JPanel(new BorderLayout(0, 8));
        keyContent.setOpaque(false);
        keyContent.add(configPanel, BorderLayout.NORTH);
        keyContent.add(keyLabel, BorderLayout.CENTER);
        keyContent.add(FilePanelUI.createBorderedScrollPane(keyArea), BorderLayout.SOUTH);
        return keyContent;
    }

    private JPanel createButtonPanel() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(18, 0, 0, 0));
        row.add(genButton);
        row.add(copyButton);
        row.add(importButton);
        row.add(exportButton);
        row.add(removeKeyButton);
        return row;
    }

    private void updateKeyLabel(String algo) {
        String text = switch (algo) {
            case "AES" -> "KEY — 128/192/256 bit";
            case "DES" -> "KEY — 64 bit";
            default -> "KEY";
        };
        keyLabel.setText(text);
        keyArea.setText("");
    }
}

