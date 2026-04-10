package view;

import controller.TextController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class SelectorPanel extends JPanel {
    private static final String[] SYMMETRIC_ALGOS =
            {"Caesar", "Affine", "Vigenère", "Hill", "Substitution", "Permutation"};
    public final JComboBox<String> typeCombo;
    public final JComboBox<String> algoCombo;
    public final JTextArea keyArea;
    private final CardLayout keyCardLayout = new CardLayout();
    private final JPanel keyCardPanel = new JPanel(keyCardLayout);
    public JButton genButton;
    public JButton importButton;
    public JButton exportButton;
    public JComboBox<String> fileCipherAlgoCombo;
    public JComboBox<Integer> keySizeCombo;
    TextController textController;

    public SelectorPanel() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);

        // Row 1: type + algo combos
        JPanel topRow = new JPanel(new GridLayout(1, 2, 14, 0));
        topRow.setOpaque(false);
        typeCombo = createDropdown(new String[]{"Đối xứng", "Bất đối xứng"});
        algoCombo = createDropdown(SYMMETRIC_ALGOS);
        topRow.add(wrapLabeled("LOẠI MÃ HÓA", typeCombo));
        topRow.add(wrapLabeled("THUẬT TOÁN", algoCombo));
        add(topRow, BorderLayout.NORTH);

        // Key manager cards
        keyCardPanel.setOpaque(false);
        keyArea = makeKeyArea();

        keyCardPanel.add(buildSymmetricKeyCard(), "Symmetric");
        add(keyCardPanel, BorderLayout.CENTER);

        // Listeners
        typeCombo.addActionListener(e -> onTypeChanged());
        algoCombo.addActionListener(e -> onAlgoChanged());

        // Initial state
        onTypeChanged();
        onAlgoChanged();
    }


    private JPanel buildSymmetricKeyCard() {
        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setOpaque(false);

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setOpaque(false);

        JLabel hint = new JLabel("KEY");
        hint.setName("classicalHint");
        hint.setFont(new Font("SansSerif", Font.BOLD, 10));
        hint.setForeground(MainFrame.TXT_LABEL);

        JScrollPane scroll = new JScrollPane(keyArea);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        left.add(hint, BorderLayout.NORTH);
        left.add(scroll, BorderLayout.CENTER);

        card.add(left, BorderLayout.CENTER);
        card.add(buildKeyButtonCol(keyArea, "txt", true), BorderLayout.EAST);
        return card;
    }


    private JPanel buildKeyHalf(String label, JTextArea area, String ext) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);

        JPanel left = new JPanel(new BorderLayout(0, 4));
        left.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 10));
        lbl.setForeground(MainFrame.TXT_LABEL);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));
        left.add(lbl, BorderLayout.NORTH);
        left.add(scroll, BorderLayout.CENTER);

        p.add(left, BorderLayout.CENTER);
        p.add(buildKeyButtonCol(area, ext, false), BorderLayout.EAST);
        return p;
    }

    //  Key buttons

    private JPanel buildKeyButtonCol(JTextArea area, String ext, boolean clickGen) {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setBorder(new EmptyBorder(18, 0, 0, 0)); // align with textarea (below label)

        if (clickGen) {
            genButton = createButton("Generate", MainFrame.ACCENT);
            col.add(genButton);
            col.add(Box.createVerticalStrut(15));
        }

        importButton = createButton("Import", new Color(70, 70, 70));
        String algo = (String) algoCombo.getSelectedItem();
        importButton.addActionListener(e -> {
            textController.generateKey(textController.getCipher(algo), keyArea);
        });
        col.add(importButton);
        col.add(Box.createVerticalStrut(15));

        exportButton = createButton("Export", new Color(70, 70, 70));
        col.add(exportButton);
        col.add(Box.createVerticalStrut(15));

        JButton cpy = createButton("Copy", new Color(70, 70, 70));
        cpy.addActionListener(e ->
                Toolkit.getDefaultToolkit().getSystemClipboard()
                        .setContents(new StringSelection(area.getText()), null));
        col.add(cpy);
        col.add(Box.createVerticalGlue());
        return col;
    }


    private void onTypeChanged() {
        String type = (String) typeCombo.getSelectedItem();
        if ("Bất đối xứng".equals(type)) {
            algoCombo.removeAllItems();
            algoCombo.addItem("RSA");
            keyCardLayout.show(keyCardPanel, "Asymmetric");
        } else {
            if (algoCombo.getItemCount() != SYMMETRIC_ALGOS.length) {
                algoCombo.removeAllItems();
                for (String a : SYMMETRIC_ALGOS) algoCombo.addItem(a);
            }
            onAlgoChanged();
        }
    }

    private void onAlgoChanged() {
        String algo = (String) algoCombo.getSelectedItem();
        if (algo == null) return;
        keyCardLayout.show(keyCardPanel, "Classical");
        updateTextAlgo(algo);
    }

    private void updateTextAlgo(String algo) {
        String hint = switch (algo) {
            case "Caesar" -> "KEY  —  số nguyên 0–25";
            case "Affine" -> "KEY  —  Hai tham số a và b ";
            case "Vigenère" -> "KEY  —  chuỗi chữ cái a-z ";
            case "Hill" -> "KEY  —  ma trận 2×2 JSON  (ví dụ: [[3,3],[2,5]])";
            case "Substitution" -> "KEY  —  chuỗi 26 ký tự hoán vị của A-Z";
            case "Permutation" -> "KEY  —  thứ tự cột, cách dấu phẩy";
            default -> "KEY";
        };
        findAndSetAlgo(keyCardPanel, "classicalHint", hint);
    }

    private void findAndSetAlgo(Container c, String name, String text) {
        for (Component comp : c.getComponents()) {
            if (name.equals(comp.getName()) && comp instanceof JLabel lbl) {
                lbl.setText(text);
                return;
            }
            if (comp instanceof Container sub) findAndSetAlgo(sub, name, text);
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