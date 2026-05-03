package view.file;

import controller.file.AsymmetricFileController;
import controller.file.FileController;
import controller.file.SymmetricFileController;
import model.file.config.AsymmetricFiletConfig;
import view.file.asymmetric.AsymmetricPanel;
import view.file.symmetric.SymmetricPanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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

    public final FileController fileController;
    public final SymmetricPanel symmetricPanel;
    public final AsymmetricPanel asymmetricPanel;
    private final CardLayout keyCardLayout = new CardLayout();
    private final JPanel keyCardPanel = new JPanel(keyCardLayout);

    public FileSelectorPanel(FileController fileController, SymmetricFileController symmetricFileController, AsymmetricFileController asymmetricFileController) {

        this.fileController = fileController;

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        typeCombo = FilePanelUI.createDropdown(new String[]{"Đối xứng", "Bất đối xứng"});
        algoCombo = FilePanelUI.createDropdown(SYMMETRIC_ALGOS);

        symmetricPanel = new SymmetricPanel(fileController, symmetricFileController);
        asymmetricPanel = new AsymmetricPanel(fileController, asymmetricFileController, symmetricFileController);

        modeCombo = symmetricPanel.modeCombo;
        paddingCombo = symmetricPanel.paddingCombo;
        keySizeCombo = symmetricPanel.keySizeCombo;
        keyArea = symmetricPanel.keyArea;
        genButton = symmetricPanel.genButton;
        copyButton = symmetricPanel.copyButton;
        importButton = symmetricPanel.importButton;
        exportButton = symmetricPanel.exportButton;

        setCombosEnabled(false);
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
        asymmetricPanel.bindButtonActions(algoCombo);
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

    public boolean isAsymmetricSelected() {
        return "Bất đối xứng".equals(typeCombo.getSelectedItem());
    }

    public AsymmetricFiletConfig buildAsymmetricEncryptConfig(File selectedFile) {
        String asymAlgo = (String) algoCombo.getSelectedItem();
        return asymmetricPanel.buildEncryptConfig(asymAlgo, selectedFile);
    }

    public void setCombosEnabled(boolean enabled) {
        typeCombo.setEnabled(enabled);
        algoCombo.setEnabled(enabled);

        // Symmetric Panel
        symmetricPanel.setCombosEnabled(enabled);

        // Asymmetric Panel
        asymmetricPanel.setCombosEnabled(enabled);


    }
}
