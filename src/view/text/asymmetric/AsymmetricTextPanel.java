package view.text.asymmetric;

import controller.text.TextController;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AsymmetricTextPanel extends JPanel {

    public final JTextArea publicArea;
    public final JTextArea privateArea;
    private final TextController textController;
    private final JComboBox<String> algoCombo;
    private JButton genButton;
    private JButton copyPubButton;
    private JButton copyPriButton;
    private JButton importPubButton ;
    private JButton importPriButton;
    private JButton removeButton;

    public AsymmetricTextPanel(TextController textController, JComboBox<String> algoCombo) {
        this.textController = textController;
        this.algoCombo = algoCombo;

        setLayout(new BorderLayout(10, 0));
        setOpaque(false);

        publicArea = makeKeyArea();
        privateArea = makeKeyArea();

        JPanel publicPanel = createKeyPanel("PUBLIC KEY", publicArea);
        JPanel privatePanel = createKeyPanel("PRIVATE KEY", privateArea);

        JPanel main = new JPanel(new GridLayout(1, 2, 10, 0));
        main.setOpaque(false);
        main.add(publicPanel);
        main.add(privatePanel);

        JPanel btnCol = buildButtonColumn();

        add(main, BorderLayout.CENTER);
        add(btnCol, BorderLayout.EAST);
    }

    private JTextArea makeKeyArea() {
        JTextArea ta = new JTextArea(4, 10);
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

    private JPanel createKeyPanel(String title, JTextArea area) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        lbl.setForeground(MainFrame.TXT_LABEL);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(MainFrame.BORDER_CLR));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildButtonColumn() {
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        col.setBorder(new EmptyBorder(6, 0, 0, 0));

        genButton = createButton("Tạo cặp khóa", MainFrame.ACCENT);
        genButton.addActionListener(e -> textController.genKeyPair(publicArea, privateArea));

        copyPubButton = createButton("Copy Public", new Color(70, 70, 70));
        copyPubButton.addActionListener(e -> textController.copyKey(publicArea));

        copyPriButton = createButton("Copy Private", new Color(70, 70, 70));
        copyPriButton.addActionListener(e -> textController.copyKey(privateArea));

        importPubButton = createButton("Import Public", new Color(70, 70, 70));
        importPubButton.addActionListener(e -> textController.importKey(publicArea));

        importPriButton = createButton("Import Private", new Color(70, 70, 70));
        importPriButton.addActionListener(e -> textController.importKey(privateArea));

        removeButton = createButton("Xóa khóa", Color.RED);
        removeButton.addActionListener(e -> textController.removeKeyArea(publicArea,privateArea));

        col.add(genButton);
        col.add(Box.createVerticalStrut(8));
        col.add(copyPubButton);
        col.add(Box.createVerticalStrut(8));
        col.add(copyPriButton);
        col.add(Box.createVerticalStrut(8));
        col.add(importPubButton);
        col.add(Box.createVerticalStrut(8));
        col.add(importPriButton);
        col.add(Box.createVerticalStrut(8));
        col.add(removeButton);

        return col;
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
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 11));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 26));
        btn.setMaximumSize(new Dimension(110, 26));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        return btn;
    }

    public void configureForAlgorithm() {
        publicArea.setText("");
        privateArea.setText("");
    }
}