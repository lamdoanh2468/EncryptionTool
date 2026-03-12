import javax.swing.*;
import java.awt.*;

public class EncryptionToolUI extends JFrame {

    private JTextArea inputArea;
    private JTextArea outputArea;
    private JTextField keyField;
    private JComboBox<String> algorithms;

    public EncryptionToolUI() {

        setLayout(new BorderLayout(10, 10));

        // ===== TOP PANEL =====
        JPanel topPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        JLabel algorithmLabel = new JLabel("Algorithm:");
        algorithms = new JComboBox<>(new String[]{
                "Caesar Cipher",
                "AES",
                "Base64"
        });
        JLabel keyLabel = new JLabel("Key:");
        keyField = new JTextField();

        topPanel.add(algorithmLabel);
        topPanel.add(algorithms);
        topPanel.add(keyLabel);
        topPanel.add(keyField);

        add(topPanel, BorderLayout.NORTH);

        // ===== INPUT AND OUTPUT  =====
        inputArea = new JTextArea();
        outputArea = new JTextArea();

        // set dark theme
        inputArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));
        outputArea.setFont(new Font("JetBrains Mono", Font.PLAIN, 14));

        JScrollPane inputScroll = new JScrollPane(inputArea);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Input Text"));
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output Result"));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                inputScroll,
                outputScroll
        );
        splitPane.setDividerLocation(200);
        add(splitPane, BorderLayout.CENTER);

        // ===== BOTTOM PANEL =====
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton encryptBtn = new JButton("Encrypt");
        JButton decryptBtn = new JButton("Decrypt");
        JButton clearBtn = new JButton("Clear");

        buttonPanel.add(encryptBtn);
        buttonPanel.add(decryptBtn);
        buttonPanel.add(clearBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // === SETTINGS ===
        setTitle("Encryption Tool");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {

        new EncryptionToolUI();

    }
}