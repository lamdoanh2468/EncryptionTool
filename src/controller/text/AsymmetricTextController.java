package controller.text;

import model.cipher.text.RSAText;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;

public class AsymmetricTextController {
    private final MainFrame view;
    private final RSAText rsa = new RSAText();

    public AsymmetricTextController(MainFrame view) {
        this.view = view;
    }

    // ==================== KEY MANAGEMENT ====================
    public void importKey(JTextArea keyArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Nhập khóa từ đường dẫn");
        int option = fileChooser.showOpenDialog(view);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                JOptionPane.showMessageDialog(null, "File không đúng định dạng .txt");
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
            JOptionPane.showMessageDialog(view, "Người dùng chưa tạo khóa");
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

    public void copyKey(JTextArea keyArea) {
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(keyArea.getText()), null);
        JOptionPane.showMessageDialog(null, "Sao chép thành công");
    }

    // ==================== RSA SPECIFIC ====================
    public void genKeyPair(JTextArea publicArea, JTextArea privateArea) {
        rsa.genKey();
        publicArea.setText(rsa.getPublicKeyText());
        privateArea.setText(rsa.getPrivateKeyText());
    }

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
            rsa.loadPublicKeyFromText(publicKeyArea.getText().trim());
            String encrypted = rsa.encrypt(plainText, rsa.getKeyPair());
            outputArea.setText(encrypted);
            JOptionPane.showMessageDialog(null, "Mã hóa RSA thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Văn bản quá dài, không thể mã hoá");
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
            rsa.loadPrivateKeyFromText(privateKey.trim());
            String decrypted = rsa.decrypt(cipherText, rsa.getKeyPair());
            outputArea.setText(decrypted);
            JOptionPane.showMessageDialog(null, "Giải mã RSA thành công!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, "Lỗi giải mã RSA: " + e.getMessage());
        }
    }

    public void removeKeyArea(JTextArea... keyArea) {
        for (JTextArea area : keyArea) {
            area.setText("");
        }
    }

}