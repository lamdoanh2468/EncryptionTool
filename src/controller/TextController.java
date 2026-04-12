package controller;

import model.text.*;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;

public class TextController {
    private final MainFrame view;

    // Text cipher models
    private final Caesar caesar = new Caesar();
    private final Affine affine = new Affine();
    private final Vigenere vigenere = new Vigenere();
    private final Hill hill = new Hill();
    private final Substitution substitution = new Substitution();
    private final Permutation permutation = new Permutation();

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
            case "Vigenère" -> vigenere;
            case "Substitution" -> substitution;
            case "Permutation" -> permutation;
            case "Hill" -> hill;
            default -> null;
        };
    }

}
