package controller.text;

import model.cipher.text.RSAText;
import view.MainFrame;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class AsymTextController {
    private final MainFrame view;
    private final RSAText rsa = new RSAText();

    public PublicKey currentPublicKey;
    public PrivateKey currentPrivateKey;

    public AsymTextController(MainFrame view) {
        this.view = view;
    }

    public void importPublicKey(JTextArea keyArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file chứa khóa công khai");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File selectedFile = fileChooser.getSelectedFile();
        try {
            String encodeKey = java.nio.file.Files.readString(selectedFile.toPath()).trim();
            byte[] decodedKey = Base64.getDecoder().decode(encodeKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);

            currentPublicKey = keyFactory.generatePublic(keySpec);
            rsa.setPubKey(currentPublicKey);
            keyArea.setText(encodeKey);
            JOptionPane.showMessageDialog(null, "Nhập khóa công khai thành công");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi nhập public key", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void importPrivateKey(JTextArea keyArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file chứa khóa bí mật");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File selectedFile = fileChooser.getSelectedFile();
        try {
            String encodedKey = java.nio.file.Files.readString(selectedFile.toPath()).trim();
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

            currentPrivateKey = keyFactory.generatePrivate(keySpec);
            rsa.setPriKey(currentPrivateKey);
            JOptionPane.showMessageDialog(null, "Nhập private key thành công");
            keyArea.setText(encodedKey);

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "File không đúng định dạng", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi nhập private key", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void exportKeyPair(JTextArea publicArea, JTextArea privateArea) {

        if (publicArea == null || publicArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Chưa có Public Key!");
            return;
        }
        if (privateArea == null || privateArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Chưa có Private Key!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn thư mục lưu cặp khóa");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showSaveDialog(view);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFolder = fileChooser.getSelectedFile();

        File publicFile = new File(selectedFolder, "public.key");
        File privateFile = new File(selectedFolder, "private.key");

        try {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(publicFile))) {
                writer.write(publicArea.getText().trim());
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(privateFile))) {
                writer.write(privateArea.getText().trim());
            }

            JOptionPane.showMessageDialog(null, "Đã lưu thành công 2 file:\n" + publicFile.getName() + "\n" + privateFile.getName());

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất cặp khóa!");
        }
    }


    public void genKeyPair(JTextArea publicArea, JTextArea privateArea) {
        rsa.genKey();
        currentPublicKey = rsa.getPublicKey();
        currentPrivateKey = rsa.getPrivateKey();
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