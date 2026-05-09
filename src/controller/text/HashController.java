package controller.text;

import model.hash.*;
import view.MainFrame;

import javax.swing.*;

public class HashController {
    private final MainFrame view;

    public HashController(MainFrame view) {
        this.view = view;
    }

    public void hashText(String algo, String text, JTextArea outputArea) {
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập văn bản cần băm!");
            return;
        }

        try {
            AFileHash hasher = getHashInstance(algo);
            byte[] hashBytes = hasher.hashText(text);
            String hexResult = hasher.bytesToHex(hashBytes);

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
            byte[] actualHash = hasher.hashText(text);
            String actualHex = hasher.bytesToHex(actualHash);

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
        if (algo == null || algo.isBlank()) {
            return new SHA256();
        }

        return switch (algo) {
            // Legacy
            case "MD2" -> new MD2();
            case "MD5" -> new MD5();
            case "SHA-1", "SHA1" -> new SHA1();

            // SHA-2
            case "SHA-224" -> new SHA224();
            case "SHA-256" -> new SHA256();
            case "SHA-384" -> new SHA384();
            case "SHA-512" -> new SHA512();
            case "SHA-512/224", "SHA512/224", "SHA512224" -> new SHA_512_224();
            case "SHA-512/256", "SHA512/256", "SHA512256" -> new SHA_512_256();

            // SHA-3
            case "SHA3-224" -> new SHA3_224();
            case "SHA3-256" -> new SHA3_256();
            case "SHA3-384" -> new SHA3_384();
            case "SHA3-512" -> new SHA3_512();

            // BLAKE
            case "BLAKE2b" -> new BLAKE2b();
            case "BLAKE3" -> new BLAKE3();

            default -> throw new IllegalArgumentException("Hàm băm không được hỗ trợ: " + algo);
        };
    }
}