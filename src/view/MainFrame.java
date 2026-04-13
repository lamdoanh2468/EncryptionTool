package view;

import controller.FileController;
import controller.TextController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    // Colors
    public static final Color BG_DEEP = Color.WHITE;
    public static final Color BG_PANEL = new Color(245, 245, 245);
    public static final Color BG_INPUT = Color.WHITE;
    public static final Color BORDER_CLR = new Color(210, 210, 210);
    public static final Color ACCENT = new Color(60, 120, 255);
    public static final Color BLUE_BTN = new Color(70, 130, 255);
    public static final Color TXT_MAIN = Color.BLACK;
    public static final Color TXT_MUTED = new Color(120, 120, 120);
    public static final Color TXT_LABEL = new Color(140, 140, 140);

    public TextPanel textPanel;
    public FilePanel filePanel;

    public TextController textController = new TextController(this);
    public FileController fileController = new FileController(this);
    public MainFrame() {
        setTitle("Cipher Tool");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 620);
        setMinimumSize(new Dimension(700, 520));
        setLocationRelativeTo(null);
        setBackground(BG_DEEP);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_DEEP);

        JPanel content = new JPanel(new BorderLayout(0, 12));
        content.setBackground(BG_DEEP);
        content.setBorder(new EmptyBorder(16, 20, 20, 20));



        JTabbedPane tabbedPane = buildTabs();
        content.add(tabbedPane, BorderLayout.CENTER);

        root.add(content, BorderLayout.CENTER);
        setContentPane(root);
    }

    public static JLabel makeLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setForeground(TXT_LABEL);
        return label;
    }

    public static JTextArea makeTextArea(String placeholder) {
        JTextArea ta = new JTextArea();
        ta.setFont(new Font("Monospaced", Font.PLAIN, 13));
        ta.setForeground(TXT_MAIN);
        ta.setBackground(BG_INPUT);
        ta.setCaretColor(TXT_MAIN);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Placeholder effect
        ta.setText(placeholder);
        ta.setForeground(TXT_MUTED);
        ta.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (ta.getText().equals(placeholder)) {
                    ta.setText("");
                    ta.setForeground(TXT_MAIN);
                }
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (ta.getText().isEmpty()) {
                    ta.setText(placeholder);
                    ta.setForeground(TXT_MUTED);
                }
            }
        });
        return ta;
    }

    public static JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = getModel().isPressed() ? bg.darker()
                        : getModel().isRollover() ? bg.brighter() : bg;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static JScrollPane scrollWrap(JTextArea ta) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_CLR),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        sp.getViewport().setBackground(BG_INPUT);
        return sp;
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tp = new JTabbedPane();
        tp.setBackground(BG_PANEL);
        tp.setForeground(TXT_MAIN);
        tp.setFont(new Font("SansSerif", Font.PLAIN, 13));
        styleTabPane(tp);

        textPanel = new TextPanel(textController);
        filePanel = new FilePanel(fileController);

        tp.addTab("  Text  ", textPanel);
        tp.addTab("  File  ", filePanel);

        return tp;
    }

    private void styleTabPane(JTabbedPane tp) {
        tp.setOpaque(false);
        UIManager.put("TabbedPane.selected", BG_PANEL);
        UIManager.put("TabbedPane.contentAreaColor", BG_PANEL);
        tp.setBorder(null);
    }
}