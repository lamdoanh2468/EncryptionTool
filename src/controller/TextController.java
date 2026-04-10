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
    private final Substitution substEnglish = Substitution.createEnglish();
    private final Substitution substVietnamese = Substitution.createVietnamese();
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

    public <K> void encryptText(ATextCipher<K> cipher, String text, JTextArea outputArea) {
        K key = cipher.getCurrentKey();
        if (key == null) {
            view.selectorPanel.keyArea.setText("Chưa có khóa");
            return;
        }
        String cipherText = cipher.encrypt(text, key);
        outputArea.setText(cipherText);
    }

    public <K> void decryptText(ATextCipher<K> cipher, String text, JTextArea inputArea) {
        K key = cipher.getCurrentKey();
        if (key == null) {
            view.selectorPanel.keyArea.setText("Chưa có khóa, vui lòng tạo khóa");
            return;
        }
        String cipherText = cipher.decrypt(text, key);
        inputArea.setText(cipherText);
    }

    public ATextCipher<?> getCipher(String algoName) {
        if (algoName == null) return null;
        return switch (algoName) {
            case "Caesar" -> caesar;
            case "Affine" -> affine;
            case "Vigenère" -> vigenere;
            case "Substitution" -> substEnglish;
            case "Permutation" -> permutation;
            case "Hill" -> hill;
            default -> null;
        };
    }

}
