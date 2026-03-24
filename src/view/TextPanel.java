package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class TextPanel extends JPanel {

    public final JTextArea inputArea;
    public final JTextArea outputArea;
    public  JButton   encryptBtn  ;
    public  JButton   decryptBtn  ;
    public  JButton   clearBtn   ;
    public  JButton   copyBtn ;
    public  JLabel    charCountLabel ;

    public TextPanel() {
        setLayout(new BorderLayout(0, 10));
        setBackground(MainFrame.BG_PANEL);
        setBorder(new EmptyBorder(16, 0, 0, 0));

        // ── IO Row ───────────────────────────────
        JPanel ioRow = new JPanel(new GridBagLayout());
        ioRow.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Left textarea
        inputArea = MainFrame.makeTextArea("Input text");
        JScrollPane leftScroll = MainFrame.scrollWrap(inputArea);

        // Middle buttons
        JPanel midPanel = buildMidPanel();

        // Right textarea
        outputArea = MainFrame.makeTextArea("Result");
        outputArea.setEditable(false);
        outputArea.setBackground(Color.WHITE);
        JScrollPane rightScroll = MainFrame.scrollWrap(outputArea);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1.0;
        ioRow.add(leftScroll, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 8, 0, 8);
        ioRow.add(midPanel, gbc);

        gbc.gridx = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        ioRow.add(rightScroll, gbc);

        add(ioRow, BorderLayout.CENTER);

        // ── Bottom bar ───────────────────────────
        add(buildBottomBar(), BorderLayout.SOUTH);

        // ── Char counter ─────────────────────────
        inputArea.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { updateCount(); }
        });
    }

    private JPanel buildMidPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(110, 0));

        encryptBtn = makeArrowBtn("MÃ HÓA", MainFrame.ACCENT, "▶");
        decryptBtn = makeArrowBtn("GIẢI MÃ", new Color(60, 180, 120), "◀");

        p.add(Box.createVerticalGlue());
        p.add(encryptBtn);
        p.add(Box.createVerticalStrut(10));
        p.add(decryptBtn);
        p.add(Box.createVerticalGlue());
        return p;
    }

    private JButton makeArrowBtn(String label, Color color, String icon) {
        JButton btn = new JButton("<html><center>" + icon + "<br><small>" + label + "</small></center></html>") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed() ? color.darker() :
                              getModel().isRollover() ? color.brighter() : color;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                // glow
                g2.setColor(new Color(base.getRed(), base.getGreen(), base.getBlue(), 40));
                g2.fillRoundRect(-3, -3, getWidth()+6, getHeight()+6, 14, 14);
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

        copyBtn  = makeSmallBtn("Sao chép kết quả", MainFrame.ACCENT);
        clearBtn = makeSmallBtn("Xóa tất cả", new Color(100, 40, 40));

        btnRow.add(copyBtn);
        btnRow.add(clearBtn);

        bar.add(charCountLabel, BorderLayout.WEST);
        bar.add(btnRow, BorderLayout.EAST);
        return bar;
    }

    private JButton makeSmallBtn(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color border = getModel().isRollover() ? color.brighter() : color;
                g2.setColor(getModel().isRollover() ? new Color(color.getRed(), color.getGreen(), color.getBlue(), 30) : MainFrame.BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(border);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                g2.setFont(getFont());
                g2.setColor(border);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
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

    public String getInputText()  { return inputArea.getText(); }
    public void   setOutputText(String t) { outputArea.setText(t); }
    public void   clearAll() {
        inputArea.setText("");
        outputArea.setText("");
        updateCount();
    }
}
