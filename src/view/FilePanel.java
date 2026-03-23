package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.File;
import java.util.List;

public class FilePanel extends JPanel {

    public  JButton encryptFileBtn;
    public  JButton decryptFileBtn;
    public final JLabel  fileNameLabel;
    public  JLabel  statusLabel;

    private File selectedFile;
    private final JPanel dropZone;

    public FilePanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(MainFrame.BG_PANEL);
        setBorder(new EmptyBorder(16, 0, 0, 0));

        // ── Drop zone ────────────────────────────
        dropZone = buildDropZone();
        add(dropZone, BorderLayout.CENTER);

        // ── File info bar ────────────────────────
        fileNameLabel = new JLabel("Chưa chọn file");
        fileNameLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fileNameLabel.setForeground(MainFrame.TXT_MUTED);
        fileNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(fileNameLabel, BorderLayout.NORTH);

        // ── Action bar ───────────────────────────
        add(buildActionBar(), BorderLayout.SOUTH);

        // ── Drag-and-drop ────────────────────────
        new DropTarget(dropZone, new DropTargetListener() {
            public void dragEnter(DropTargetDragEvent e)  { dropZone.setBorder(glowBorder()); }
            public void dragExit(DropTargetEvent e)        { dropZone.setBorder(dashedBorder()); }
            public void dragOver(DropTargetDragEvent e)   {}
            public void dropActionChanged(DropTargetDragEvent e) {}
            public void drop(DropTargetDropEvent e) {
                try {
                    e.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> files = (List<File>) e.getTransferable()
                        .getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) setFile(files.get(0));
                } catch (Exception ex) { ex.printStackTrace(); }
                dropZone.setBorder(dashedBorder());
            }
        });
    }

    private JPanel buildDropZone() {
        JPanel zone = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(92, 92, 255, 8));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
            }
        };
        zone.setBackground(MainFrame.BG_CARD);
        zone.setBorder(dashedBorder());
        zone.setPreferredSize(new Dimension(0, 180));
        zone.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel icon = new JLabel("📂");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 36));
        zone.add(icon, gbc);

        JLabel title = new JLabel("Kéo & thả file vào đây");
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        title.setForeground(MainFrame.TXT_MAIN);
        zone.add(title, gbc);

        JLabel sub = new JLabel("hoặc click để chọn file  (.txt, .csv, .bin, .dat)");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MainFrame.TXT_MUTED);
        zone.add(sub, gbc);

        zone.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { openFileChooser(); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { zone.setBorder(glowBorder()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e)  { zone.setBorder(dashedBorder()); }
        });

        return zone;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new GridLayout(1, 3, 12, 0));
        bar.setOpaque(false);

        JButton chooseBtn = new JButton("📂  Chọn file") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? MainFrame.BG_INPUT.brighter() : MainFrame.BG_INPUT);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(MainFrame.BORDER_CLR);
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(MainFrame.TXT_MAIN);
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2,
                    (getHeight()+fm.getAscent()-fm.getDescent())/2);
                g2.dispose();
            }
        };
        chooseBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        chooseBtn.setContentAreaFilled(false);
        chooseBtn.setBorderPainted(false);
        chooseBtn.setFocusPainted(false);
        chooseBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        chooseBtn.setPreferredSize(new Dimension(0, 44));
        chooseBtn.addActionListener(e -> openFileChooser());

        encryptFileBtn = MainFrame.makeButton("Mã Hóa  ▶", MainFrame.RED_BTN, Color.WHITE);
        encryptFileBtn.setEnabled(false);
        decryptFileBtn = MainFrame.makeButton("◀  Giải Mã", MainFrame.BLUE_BTN, Color.WHITE);
        decryptFileBtn.setEnabled(false);

        bar.add(chooseBtn);
        bar.add(encryptFileBtn);
        bar.add(decryptFileBtn);

        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);

        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(40, 200, 64));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        wrapper.add(bar, BorderLayout.CENTER);
        wrapper.add(statusLabel, BorderLayout.SOUTH);
        return wrapper;
    }

    private void openFileChooser() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Text & Binary files", "txt","csv","bin","dat"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            setFile(fc.getSelectedFile());
    }

    public void setFile(File f) {
        selectedFile = f;
        fileNameLabel.setText("📄  " + f.getName() +
            "  (" + (f.length() / 1024 + 1) + " KB)");
        fileNameLabel.setForeground(MainFrame.ACCENT);
        encryptFileBtn.setEnabled(true);
        decryptFileBtn.setEnabled(true);
        dropZone.repaint();
    }

    public File getSelectedFile()  { return selectedFile; }
    public void setStatus(String msg, boolean success) {
        statusLabel.setText(msg);
        statusLabel.setForeground(success ? new Color(40, 200, 64) : new Color(255, 80, 70));
    }

    private Border dashedBorder() {
        return BorderFactory.createDashedBorder(MainFrame.BORDER_CLR, 6, 4, 2, false);
    }

    private Border glowBorder() {
        return new LineBorder(MainFrame.ACCENT, 1);
    }
}
