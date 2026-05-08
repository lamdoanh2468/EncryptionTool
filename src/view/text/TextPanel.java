package view.text;

import controller.text.TextController;
import view.MainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TextPanel extends JPanel {

    public final JTextArea inputArea;
    public final JTextArea outputArea;
    public JButton encryptBtn;
    public JButton decryptBtn;
    public JButton clearBtn;
    public JButton copyBtn;
    public JLabel charCountLabel;

    public TextController textController;
    public TextSelectorPanel selectorPanel;

    private boolean isHashMode = false;

    public TextPanel(TextController textController) {

        this.textController = textController;

        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.BG_PANEL);
        setBorder(new EmptyBorder(16, 0, 0, 0));


        // Selector panel
        selectorPanel = new TextSelectorPanel(textController);
        selectorPanel.typeCombo.addActionListener(e -> {
            String type = (String) selectorPanel.typeCombo.getSelectedItem();
            if ("Hàm Băm".equals(type)) {
                switchToHashMode();
            } else {
                switchToNormalMode();
            }
        });
        JPanel northPanel = new JPanel(new BorderLayout(0, 0));
        northPanel.setOpaque(false);
        northPanel.add(selectorPanel, BorderLayout.CENTER);

        // Create line separator
        JSeparator divider = new JSeparator(SwingConstants.HORIZONTAL);
        divider.setForeground(MainFrame.BORDER_CLR);
        divider.setPreferredSize(new Dimension(0, 1));
        northPanel.add(divider, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);

        JPanel ioRow = new JPanel(new GridBagLayout());
        ioRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Input
        inputArea = MainFrame.makeTextArea("");
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JPanel leftPanel = createLabeledArea("Văn bản gốc", inputArea);
        leftPanel.setMinimumSize(new Dimension(300, 280));

        // Mid buttons
        JPanel midPanel = buildMidPanel();
        midPanel.setPreferredSize(new Dimension(140, 160));   // ← SỬA
        midPanel.setMaximumSize(new Dimension(140, 160));     // ← THÊM

        // Output
        outputArea = MainFrame.makeTextArea("");
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(248, 250, 252));
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JPanel rightPanel = createLabeledArea("Kết quả", outputArea);
        rightPanel.setMinimumSize(new Dimension(300, 280));

        // Grid
        gbc.gridx = 0; gbc.weightx = 0.45; gbc.insets = new Insets(0,0,0,0);
        ioRow.add(leftPanel, gbc);

        gbc.gridx = 1; gbc.weightx = 0; gbc.insets = new Insets(0,16,0,16);
        ioRow.add(midPanel, gbc);                     // ← Đảm bảo có dòng này

        gbc.gridx = 2; gbc.weightx = 0.45; gbc.insets = new Insets(0,0,0,0);
        ioRow.add(rightPanel, gbc);

        add(ioRow, BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension(300, 200));
        // Bottom bar
        add(buildBottomBar(), BorderLayout.SOUTH);

        // Char counter
        inputArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateCount();
            }
        });
    }

    private JPanel createLabeledArea(String labelText, JTextArea textArea) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(MainFrame.TXT_LABEL);

        JScrollPane scroll = MainFrame.scrollWrap(textArea);

        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));

        panel.add(label, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildMidPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(140, 160));
        p.setPreferredSize(new Dimension(110, 0));
        encryptBtn = createEDButton("MÃ HÓA", MainFrame.BLUE_BTN);
        encryptBtn.addActionListener(e -> {
            String type = (String) selectorPanel.typeCombo.getSelectedItem();
            if (type.equals("Hàm Băm")) {
                String text = inputArea.getText();
                String algo = (String) selectorPanel.algoCombo.getSelectedItem();
                textController.hashText(algo, text, outputArea);
            }
            else if (type.equals("Bất đối xứng")){
                String text = inputArea.getText();
                textController.encryptRSA(text, selectorPanel.asymmetricPanel.publicArea, outputArea);
            }
            else {
                String key = selectorPanel.symmetricPanel.keyArea.getText();
                String text = inputArea.getText();
                String algo = (String) selectorPanel.algoCombo.getSelectedItem();
                textController.encryptText(textController.getCipher(algo), text, key, outputArea);
            }
        });

        decryptBtn = createEDButton("GIẢI MÃ", new Color(60, 180, 120));
        decryptBtn.addActionListener(e -> {
            String type = (String) selectorPanel.typeCombo.getSelectedItem();
            if (type.equals("Hàm Băm")) {
                String text = inputArea.getText();
                String algo = (String) selectorPanel.algoCombo.getSelectedItem();
                textController.verifyHash(algo, text, outputArea.getText());
            } else if (type.equals("Bất đối xứng")) {
                String text = inputArea.getText();
                textController.decryptRSA(text, selectorPanel.asymmetricPanel.privateArea, outputArea);
            }
            else {
                String key = selectorPanel.symmetricPanel.keyArea.getText();
                String text = inputArea.getText();
                String algo = (String) selectorPanel.algoCombo.getSelectedItem();
                textController.decryptText(textController.getCipher(algo), text, key, outputArea);
            }
        });
        p.add(Box.createVerticalGlue());
        p.add(encryptBtn);
        p.add(Box.createVerticalStrut(10));
        p.add(decryptBtn);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JButton createEDButton(String label, Color color) {
        JButton btn = new JButton("<html><center>" + label + "</small></center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed() ? color.darker()
                        : getModel().isRollover() ? color.brighter() : color;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 40));
                g2.fillRoundRect(-3, -3, getWidth() + 6, getHeight() + 6, 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(110, 60));
        btn.setPreferredSize(new Dimension(110, 60));
        return btn;
    }

    private JPanel buildBottomBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(8, 2, 0, 2));

        charCountLabel = new JLabel("0 ký tự");
        charCountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        charCountLabel.setForeground(MainFrame.TXT_LABEL);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnRow.setOpaque(false);

        copyBtn = createButton("Sao chép kết quả", MainFrame.ACCENT);
        copyBtn.addActionListener(e -> {
            textController.copyKey(outputArea);
        });

        clearBtn = createButton("Xóa tất cả", Color.RED);
        clearBtn.addActionListener(e -> {
            textController.clearAll();
        });

        btnRow.add(copyBtn);
        btnRow.add(clearBtn);

        bar.add(charCountLabel, BorderLayout.WEST);
        bar.add(btnRow, BorderLayout.EAST);
        return bar;
    }

    private JButton createButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color border = getModel().isRollover() ? color.brighter() : color;
                g2.setColor(getModel().isRollover() ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 30)
                        : MainFrame.BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.setFont(getFont());
                g2.setColor(border);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2,
                        (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        btn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 30));
        return btn;
    }

    public void updateCount() {
        int len = inputArea.getText().length();
        charCountLabel.setText(len + " ký tự");
    }


    private void switchToHashMode() {
        isHashMode = true;
        encryptBtn.setText("BĂM VĂN BẢN");
        decryptBtn.setText("KIỂM TRA");
    }

    private void switchToNormalMode() {
        isHashMode = false;
        encryptBtn.setText("MÃ HOÁ");
        decryptBtn.setText("GIẢI MÃ");
    }
}
