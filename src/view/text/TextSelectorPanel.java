package view.text;

import controller.text.TextController;
import org.bouncycastle.jcajce.provider.symmetric.TEA;
import view.MainFrame;
import view.file.FilePanelUI;
import view.text.asymmetric.AsymmetricTextPanel;
import view.text.hash.HashTextPanel;
import view.text.symmetric.SymmetricTextPanel;

import javax.swing.*;
import java.awt.*;

public class TextSelectorPanel extends JPanel {

    private static final String[] SYMMETRIC_ALGOS = {"Caesar", "Affine", "Vigenere", "Hill", "Thay thế", "Hoán vị"};

    private static final String[] HASH_FUNCTIONS = {"MD5", "SHA-1", "SHA-256", "SHA-512", "SHA3-256"};
    public final JComboBox<String> typeCombo;
    public final JComboBox<String> algoCombo;

    public final SymmetricTextPanel symmetricPanel;
    public final AsymmetricTextPanel asymmetricPanel;
    public final HashTextPanel hashPanel;

    private final CardLayout keyCardLayout = new CardLayout();
    private final JPanel keyCardPanel = new JPanel(keyCardLayout);

    public TextController textController;

    public TextSelectorPanel(TextController textController) {
        this.textController = textController;

        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        // Top row
        JPanel topRow = new JPanel(new GridLayout(1, 2, 14, 0));
        topRow.setOpaque(false);
        typeCombo = createDropdown(new String[]{"Đối xứng", "Bất đối xứng", "Hàm Băm"});
        algoCombo = createDropdown(SYMMETRIC_ALGOS);
        topRow.add(FilePanelUI.createLabeledFieldPanel("LOẠI MÃ HÓA", typeCombo));
        topRow.add(FilePanelUI.createLabeledFieldPanel("THUẬT TOÁN", algoCombo));
        add(topRow, BorderLayout.NORTH);

        // Child panels
        symmetricPanel = new SymmetricTextPanel(textController, algoCombo);
        asymmetricPanel = new AsymmetricTextPanel(textController, algoCombo);
        hashPanel = new HashTextPanel();

        keyCardPanel.setOpaque(false);
        keyCardPanel.add(symmetricPanel, "Symmetric");
        keyCardPanel.add(asymmetricPanel, "Asymmetric");
        keyCardPanel.add(hashPanel, "Hash");
        add(keyCardPanel, BorderLayout.CENTER);

        // Listeners
        typeCombo.addActionListener(e -> handleTypeChange());
        algoCombo.addActionListener(e -> handleAlgorithmChange());

        // Initial state
        handleTypeChange();
    }

    private void handleTypeChange() {
        algoCombo.removeAllItems();
        textController.removeKeyArea(symmetricPanel.keyArea, asymmetricPanel.publicArea, asymmetricPanel.privateArea);
        textController.clearAll();

        if (isAsymmetricSelected()) {
            algoCombo.addItem("RSA");
            keyCardLayout.show(keyCardPanel, "Asymmetric");
        } else if (isHashSelected()) {
            for (String h : HASH_FUNCTIONS) algoCombo.addItem(h);
            keyCardLayout.show(keyCardPanel, "Hash");
            textController.toggleOutputArea(true);
        } else {
            for (String a : SYMMETRIC_ALGOS) algoCombo.addItem(a);
            keyCardLayout.show(keyCardPanel, "Symmetric");
        }
        handleAlgorithmChange();
    }

    private void handleAlgorithmChange() {
        String algo = (String) algoCombo.getSelectedItem();
        if (algo == null) return;

        if (isAsymmetricSelected()) {
            asymmetricPanel.configureForAlgorithm();
        } else if (!isHashSelected()) {
            symmetricPanel.configureForAlgorithm(algo);
        }
    }

    public boolean isAsymmetricSelected() {
        return "Bất đối xứng".equals(typeCombo.getSelectedItem());
    }

    public boolean isHashSelected() {
        return "Hàm Băm".equals(typeCombo.getSelectedItem());
    }

    // Helper methods
    private JComboBox<String> createDropdown(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        cb.setBackground(MainFrame.BG_INPUT);
        cb.setForeground(MainFrame.TXT_MAIN);
        cb.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        cb.setPreferredSize(new Dimension(200, 32));
        return cb;
    }
}