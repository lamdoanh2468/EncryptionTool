//package view;
//
//import javax.swing.*;
//import javax.swing.border.*;
//import java.awt.*;
//
//public class MainFrame extends JFrame {
//
//    // --- Colors ---
//    public static final Color BG_DEEP    = Color.WHITE;
//    public static final Color BG_PANEL   = new Color(245,245,245);
//    public static final Color BG_CARD    = Color.WHITE;
//    public static final Color BG_INPUT   = Color.WHITE;
//
//    public static final Color BORDER_CLR = new Color(210,210,210);
//
//    public static final Color ACCENT     = new Color(60,120,255);
//
//    public static final Color RED_BTN    = new Color(220,70,70);
//    public static final Color BLUE_BTN   = new Color(70,130,255);
//
//    public static final Color TXT_MAIN   = Color.BLACK;
//    public static final Color TXT_MUTED  = new Color(120,120,120);
//    public static final Color TXT_LABEL  = new Color(140,140,140);
//
//    // --- Child panels ---
//    public final SelectorPanel selectorPanel;
//    public final TextPanel     textPanel;
//    public final FilePanel     filePanel;
//    public final JTabbedPane   tabbedPane;
//
//    public MainFrame() {
//        setTitle("Cipher Tool");
//        setDefaultCloseOperation(EXIT_ON_CLOSE);
//        setSize(820, 620);
//        setMinimumSize(new Dimension(700, 520));
//        setLocationRelativeTo(null);
//        setBackground(BG_DEEP);
//
//        try {
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//        } catch (Exception ignored) {}
//
//        // Root
//        JPanel root = new JPanel(new BorderLayout(0, 0));
//        root.setBackground(BG_DEEP);
//        root.setBorder(new EmptyBorder(0, 0, 0, 0));
//
//        JPanel content = new JPanel(new BorderLayout(0, 12));
//        content.setBackground(BG_DEEP);
//        content.setBorder(new EmptyBorder(16, 20, 20, 20));
//
//        selectorPanel = new SelectorPanel();
//        content.add(selectorPanel, BorderLayout.NORTH);
//
//        tabbedPane = buildTabs();
//        content.add(tabbedPane, BorderLayout.CENTER);
//
//        textPanel = (TextPanel) tabbedPane.getComponentAt(0);
//        filePanel = (FilePanel) tabbedPane.getComponentAt(1);
//
//        root.add(content, BorderLayout.CENTER);
//        setContentPane(root);
//    }
//
//
//    private JTabbedPane buildTabs() {
//        JTabbedPane tp = new JTabbedPane();
//        tp.setBackground(BG_PANEL);
//        tp.setForeground(TXT_MAIN);
//        tp.setFont(new Font("SansSerif", Font.PLAIN, 13));
//        tp.setTabPlacement(JTabbedPane.TOP);
//
//        styleTabPane(tp);
//
//        tp.addTab("  Text  ", new TextPanel());
//        tp.addTab("  File  ", new FilePanel());
//
//        return tp;
//    }
//
//    private void styleTabPane(JTabbedPane tp) {
//        tp.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
//            @Override protected void installDefaults() {
//                super.installDefaults();
//                highlight = BG_PANEL;
//                lightHighlight = BORDER_CLR;
//                shadow = BG_PANEL;
//                darkShadow = BG_PANEL;
//                focus = ACCENT;
//            }
//            @Override protected void paintTabBackground(Graphics g, int tp2, int idx,
//                    int x, int y, int w, int h, boolean sel) {
//                g.setColor(sel ? BG_PANEL : Color.WHITE);
//                g.fillRect(x, y, w, h);
//            }
//            @Override protected void paintTabBorder(Graphics g, int tp2, int idx,
//                    int x, int y, int w, int h, boolean sel) {
//                if (sel) {
//                    g.setColor(ACCENT);
//                    g.fillRect(x+2, y+h-3, w-4, 3);
//                }
//            }
//            @Override protected void paintFocusIndicator(Graphics g, int tp2,
//                    Rectangle[] rects, int idx, Rectangle iconRect, Rectangle textRect, boolean isFocused) {}
//            @Override protected int getTabRunOverlay(int runOverlay) { return 1; }
//        });
//    }
//
//    // ─── Utility ──────────────────────────────────
//    public static JLabel makeLabel(String text) {
//        JLabel l = new JLabel(text);
//        l.setFont(new Font("SansSerif", Font.BOLD, 10));
//        l.setForeground(TXT_MUTED);
//        return l;
//    }
//
//    public static JTextArea makeTextArea(String placeholder) {
//        JTextArea ta = new JTextArea() {
//            @Override protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                if (getText().isEmpty() && !isFocusOwner()) {
//                    Graphics2D g2 = (Graphics2D) g.create();
//                    g2.setColor(TXT_LABEL);
//                    g2.setFont(new Font("Monospaced", Font.ITALIC, 12));
//                    g2.drawString(placeholder, 12, 22);
//                    g2.dispose();
//                }
//            }
//        };
//        ta.setFont(new Font("Monospaced", Font.PLAIN, 18));
//        ta.setBackground(BG_INPUT);
//        ta.setForeground(TXT_MAIN);
//        ta.setCaretColor(ACCENT);
//        ta.setSelectionColor(new Color(60,120,255,80));
//        ta.setLineWrap(true);
//        ta.setWrapStyleWord(true);
//        ta.setBorder(new EmptyBorder(12, 12, 12, 12));
//        return ta;
//    }
//
//    public static JButton makeButton(String text, Color bg, Color fg) {
//        JButton btn = new JButton(text) {
//            @Override protected void paintComponent(Graphics g) {
//                Graphics2D g2 = (Graphics2D) g.create();
//                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//                g2.setColor(getModel().isPressed() ? bg.darker() :
//                             getModel().isRollover() ? bg.brighter() : bg);
//                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
//                g2.setColor(fg);
//                g2.setFont(getFont());
//                FontMetrics fm = g2.getFontMetrics();
//                int tx = (getWidth() - fm.stringWidth(getText())) / 2;
//                int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
//                g2.drawString(getText(), tx, ty);
//                g2.dispose();
//            }
//        };
//        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
//        btn.setPreferredSize(new Dimension(140, 40));
//        btn.setContentAreaFilled(false);
//        btn.setBorderPainted(false);
//        btn.setFocusPainted(false);
//        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//        return btn;
//    }
//
//    public static JScrollPane scrollWrap(JTextArea ta) {
//        JScrollPane sp = new JScrollPane(ta);
//        sp.setBorder(new LineBorder(BORDER_CLR, 1));
//        sp.getViewport().setBackground(BG_INPUT);
//        sp.setBackground(BG_INPUT);
//        return sp;
//    }
//}
