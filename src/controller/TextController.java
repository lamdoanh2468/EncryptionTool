package controller;

import model.text.*;
import view.MainFrame;

import javax.swing.*;

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

//    private void importKey(JTextArea target, String ext) {
//        JFileChooser fc = new JFileChooser();
//        fc.setDialogTitle("Import Key");
//        fc.setFileFilter(new FileNameExtensionFilter("Key files (*." + ext + ", *.txt)", ext, "txt"));
//        if (fc.showOpenDialog(view.selectorPanel) != JFileChooser.APPROVE_OPTION) return;
//        try {
//            File f = fc.getSelectedFile();
//            if ("key".equals(ext)) {
//                byte[] raw = Files.readAllBytes(f.toPath());
//                target.setText(Base64.getEncoder().encodeToString(raw));
//            } else {
//                target.setText(Files.readString(f.toPath(), StandardCharsets.UTF_8).trim());
//            }
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog(view.selectorPanel, "Lỗi đọc file: " + ex.getMessage(),
//                    "Import Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }

//    private void exportKey(JTextArea target, String ext) {
//        String content = target.getText().trim();
//        if (content.isEmpty()) {
//            JOptionPane.showMessageDialog(this,
//                    "Key trống — hãy tạo key trước.", "Export",
//                    JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        JFileChooser fc = new JFileChooser();
//        fc.setDialogTitle("Export Key");
//        fc.setSelectedFile(new File("cipher_key." + ext));
//        if (fc.showSaveDialog(view.selectorPanel) != JFileChooser.APPROVE_OPTION) return;
//        try {
//            File f = fc.getSelectedFile();
//            if ("key".equals(ext)) {
//                Files.write(f.toPath(), Base64.getDecoder().decode(content));
//            } else {
//                Files.writeString(f.toPath(), content, StandardCharsets.UTF_8);
//            }
//            JOptionPane.showMessageDialog(view.selectorPanel, "Đã lưu: " + f.getName(),
//                    "Export OK", JOptionPane.INFORMATION_MESSAGE);
//        } catch (IOException | IllegalArgumentException ex) {
//            JOptionPane.showMessageDialog(view.selectorPanel, "Lỗi ghi file: " + ex.getMessage(),
//                    "Export Error", JOptionPane.ERROR_MESSAGE);
//        }
//    }

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
        try {
            K key = cipher.parseKey(keyText.trim());
            if (key == null) {
                JOptionPane.showMessageDialog(view, "Key không hợp lệ! Vui lòng tạo lại key", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String cipherText = cipher.encrypt(text, key);
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
        try {
            K key = cipher.parseKey(keyText.trim());
            if (key == null) {
                JOptionPane.showMessageDialog(view, "Key không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String plainText = cipher.decrypt(text, key);
            inputArea.setText(plainText);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi giải mã: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
