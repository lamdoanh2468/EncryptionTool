package controller.text;

import model.cipher.text.*;
import model.hash.*;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;

public class TextController {
    private final MainFrame view;

    // Text symmetric cipher models
    private final Caesar caesar = new Caesar();
    private final Affine affine = new Affine();
    private final Vigenere vigenere = new Vigenere();
    private final Hill hill = new Hill();
    private final Substitution substitution = new Substitution();
    private final Permutation permutation = new Permutation();

    // Text asymmetric cipher models
    private final RSAText rsa = new RSAText();

    public TextController(MainFrame view) {
        this.view = view;
    }

    public void importKey(JTextArea keyArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Nhập khóa từ đường dẫn");
        int option = fileChooser.showOpenDialog(view);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                JOptionPane.showMessageDialog(null, "File không đúng định dạng .");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                keyArea.setText(builder.toString());
                JOptionPane.showMessageDialog(null, "Tải khóa thành công");
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Lỗi khi nhập khóa");
            }
        }

    }

    public void exportKey(JTextArea keyArea, String ext) {
        if (keyArea.getText() == null || keyArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(view, " Người dùng chưa tạo khóa");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save as");

        int userSelection = fileChooser.showSaveDialog(view);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(ext)) {
                file = new File(file.getPath() + "." + ext);
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(keyArea.getText());
                JOptionPane.showMessageDialog(null, "Lưu khóa vào file thành công");
            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Lỗi khi xuất khóa");
            }
        }
    }

    public <K> void generateKey(ATextCipher<K> cipher, JTextArea keyArea) {
        K key = cipher.genKey();
        cipher.loadKey(key);
        keyArea.setText(cipher.getKey());
    }

    public <K> void encryptText(ATextCipher<K> cipher, String text, String keyText, JTextArea outputArea) {
        if (keyText == null || keyText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Chưa có khóa, vui lòng tạo khóa");
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập văn bản!");
            return;
        }
        try {
            K key = cipher.parseKey(keyText.trim());
            if (key == null) {
                JOptionPane.showMessageDialog(view, "Key không hợp lệ! Vui lòng tạo lại key", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String cipherText = cipher.encrypt(text, key);
            JOptionPane.showMessageDialog(null, "Mã hóa thành công");
            outputArea.setText(cipherText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi mã hóa, vui lòng tạo lại khóa và thử lại");
        }
    }

    public <K> void decryptText(ATextCipher<K> cipher, String text, String keyText, JTextArea inputArea) {
        if (keyText == null || keyText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Chưa có khóa, vui lòng tạo hoặc import khóa", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập văn bản!");
            return;
        }
        try {
            K key = cipher.parseKey(keyText.trim());
            if (key == null) {
                JOptionPane.showMessageDialog(view, "Key không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String plainText = cipher.decrypt(text, key);
            JOptionPane.showMessageDialog(null, "Giải mã thành công");

            inputArea.setText(plainText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi giải mã: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void copyKey(JTextArea keyArea) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(keyArea.getText()), null);
        JOptionPane.showMessageDialog(null, "Sao chép thành công");
    }

    public ATextCipher<?> getCipher(String algoName) {
        if (algoName == null) return null;
        return switch (algoName) {
            case "Caesar" -> caesar;
            case "Affine" -> affine;
            case "Vigenere" -> vigenere;
            case "Thay thế" -> substitution;
            case "Hoán vị" -> permutation;
            case "Hill" -> hill;
            case "RSA" -> rsa;
            default -> null;
        };
    }

    // HÀM BĂM

    public void hashText(String algo, String text, JTextArea outputArea) {
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập văn bản cần băm!");
            return;
        }

        try {
            AFileHash hasher = getHashInstance(algo);
            byte[] hashBytes = hasher.hash(text);
            String hexResult = bytesToHex(hashBytes);

            outputArea.setText(hexResult);
            JOptionPane.showMessageDialog(null, "Băm thành công (" + algo + ")");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi băm: " + e.getMessage());
        }
    }



    public void verifyHash(String algo, String text, String expectedHash) {
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập văn bản cần kiểm tra!");
            return;
        }
        if (expectedHash == null || expectedHash.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập hàm băm cần kiểm tra!");
            return;
        }

        try {
            AFileHash hasher = getHashInstance(algo);
            byte[] actualHash = hasher.hash(text);
            String actualHex = bytesToHex(actualHash);

            boolean isMatch = actualHex.equalsIgnoreCase(expectedHash.trim());

            if (isMatch) {
                JOptionPane.showMessageDialog(null, "Giá trị băm khớp với văn bản gốc");
            } else {
                JOptionPane.showMessageDialog(null,
                        "Giá trị băm KHÔNG KHỚP!\n" +
                                "Hash bạn nhập : " + expectedHash + "\n" +
                                "Hash tính được: " + actualHex);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi khi kiểm tra hash: " + e.getMessage());
        }
    }


    private AFileHash getHashInstance(String algo) {
        if (algo == null) return new SHA256();

        return switch (algo) {
            case "MD5" -> new MD5();
            case "SHA-1" -> new SHA1();
            case "SHA-224" -> new SHA224();
            case "SHA-256" -> new SHA256();
            case "SHA-384" -> new SHA384();
            case "SHA-512" -> new SHA512();
            default -> throw new IllegalStateException("Unexpected hash function: " + algo);
        };
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    public void toggleOutputArea(boolean enable) {
        if (view.textPanel != null && view.textPanel.outputArea != null) {
            view.textPanel.outputArea.setEditable(enable);
            view.textPanel.outputArea.setVisible(enable);
        }
    }

    public void genKeyPair(JTextArea publicArea, JTextArea privateArea) {
        rsa.genKey();
        publicArea.setText(rsa.getPublicKeyText());
        privateArea.setText(rsa.getPrivateKeyText());
    }

    // Handling RSA Text Encryption and Decryption
    public void encryptRSA(String plainText, JTextArea publicKeyArea, JTextArea outputArea) {
        if (plainText == null || plainText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập văn bản cần mã hóa!");
            return;
        }
        if (publicKeyArea.getText() == null || publicKeyArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập hoặc tạo Public Key!");
            return;
        }

        try {
            // Load public key from text area
            rsa.loadPublicKeyFromText(publicKeyArea.getText().trim());

            String encrypted = rsa.encrypt(plainText, rsa.getKeyPair());
            outputArea.setText(encrypted);
            JOptionPane.showMessageDialog(null, "Mã hóa RSA thành công!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi mã hóa RSA: " + e.getMessage());
        }
    }

    public void decryptRSA(String cipherText, JTextArea privateKeyArea, JTextArea outputArea) {
        if (cipherText == null || cipherText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập văn bản cần giải mã!");
            return;
        }
        String privateKey = privateKeyArea.getText();
        if (privateKey == null || privateKey.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập Private Key!");
            return;
        }

        try {
            // Load private key from text area
            rsa.loadPrivateKeyFromText(privateKey.trim());

            String decrypted = rsa.decrypt(cipherText, rsa.getKeyPair());
            outputArea.setText(decrypted);
            JOptionPane.showMessageDialog(null, "Giải mã RSA thành công!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi giải mã RSA: " + e.getMessage());
        }
    }
}
