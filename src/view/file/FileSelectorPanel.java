package view.file;

import controller.FileController;
import view.file.asymmetric.AsymmetricPanel;
import view.file.symmetric.SymmetricPanel;

import javax.swing.*;
import java.awt.*;

public class FileSelectorPanel extends JPanel {
    private static final String[] SYMMETRIC_ALGOS = {"AES", "DES"};
    private static final String[] ASYMMETRIC_ALGOS = {"RSA"};

    public final JComboBox<String> typeCombo;
    public final JComboBox<String> algoCombo;
    public final JComboBox<String> modeCombo;
    public final JComboBox<String> paddingCombo;
    public final JComboBox<Integer> keySizeCombo;

    public final JTextArea keyArea;

    public final JButton genButton;
    public final JButton copyButton;
    public final JButton importButton;
    public final JButton exportButton;

    private final CardLayout keyCardLayout = new CardLayout();
    private final JPanel keyCardPanel = new JPanel(keyCardLayout);
    private final SymmetricPanel symmetricPanel;
    private final AsymmetricPanel asymmetricPanel;

    public final FileController fileController;

    public FileSelectorPanel(FileController fileController) {
        this.fileController = fileController;

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        typeCombo = FilePanelUI.createDropdown(new String[]{"Đối xứng", "Bất đối xứng"});
        algoCombo = FilePanelUI.createDropdown(SYMMETRIC_ALGOS);

        symmetricPanel = new SymmetricPanel(fileController);
        asymmetricPanel = new AsymmetricPanel(fileController);

        modeCombo = symmetricPanel.modeCombo;
        paddingCombo = symmetricPanel.paddingCombo;
        keySizeCombo = symmetricPanel.keySizeCombo;
        keyArea = symmetricPanel.keyArea;
        genButton = symmetricPanel.genButton;
        copyButton = symmetricPanel.copyButton;
        importButton = symmetricPanel.importButton;
        exportButton = symmetricPanel.exportButton;

        keyCardPanel.setOpaque(false);
        keyCardPanel.add(symmetricPanel, "Symmetric");
        keyCardPanel.add(asymmetricPanel, "Asymmetric");

        add(createTopRow(), BorderLayout.NORTH);
        add(keyCardPanel, BorderLayout.CENTER);

        bindActions();
        handleEncryptionTypeChange();
    }

    public String getSelectedAlgorithm() {
        return (String) algoCombo.getSelectedItem();
    }

    public String getSelectedMode() {
        if (isAsymmetricSelected()) {
            return asymmetricPanel.getSelectedHybridMode();
        }
        return symmetricPanel.getSelectedMode();
    }

    public String getSelectedPadding() {
        if (isAsymmetricSelected()) {
            return asymmetricPanel.getSelectedHybridPadding();
        }
        return symmetricPanel.getSelectedPadding();
    }

    private JPanel createTopRow() {
        JPanel topRow = new JPanel(new GridLayout(1, 2, 14, 0));
        topRow.setOpaque(false);
        topRow.add(FilePanelUI.createLabeledFieldPanel("LOẠI MÃ HÓA", typeCombo));
        topRow.add(FilePanelUI.createLabeledFieldPanel("THUẬT TOÁN", algoCombo));
        return topRow;
    }

    private void bindActions() {
        typeCombo.addActionListener(e -> handleEncryptionTypeChange());
        algoCombo.addActionListener(e -> handleAlgorithmChange());
        symmetricPanel.bindActions(algoCombo);
        asymmetricPanel.bindActions(algoCombo);
    }

    private void handleEncryptionTypeChange() {
        algoCombo.removeAllItems();

        if (isAsymmetricSelected()) {
            for (String algo : ASYMMETRIC_ALGOS) {
                algoCombo.addItem(algo);
            }
            keyCardLayout.show(keyCardPanel, "Asymmetric");
        } else {
            for (String algo : SYMMETRIC_ALGOS) {
                algoCombo.addItem(algo);
            }
            keyCardLayout.show(keyCardPanel, "Symmetric");
        }

        handleAlgorithmChange();
    }

    private void handleAlgorithmChange() {
        String algo = getSelectedAlgorithm();
        if (algo == null) return;

        if (isAsymmetricSelected()) {
            asymmetricPanel.configureForAlgorithm(algo);
        } else {
            symmetricPanel.configureForAlgorithm(algo);
        }
    }

    private boolean isAsymmetricSelected() {
        return "Bất đối xứng".equals(typeCombo.getSelectedItem());
    }
}
