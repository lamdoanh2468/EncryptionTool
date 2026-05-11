package controller.text;

import model.hash.*;
import view.MainFrame;

import javax.swing.*;

public class HashTextController {
    private final MainFrame view;

    public HashTextController(MainFrame view) {
        this.view = view;
    }

    public void hashText(String algo, String text, JTextArea outputArea) {
        if (text == null || text.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Vui lòng nhập văn bản cần băm!");
            return;
        }

        try {
            AFileHash hasher = HashFactory.getInstance(algo);
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
            AFileHash hasher = HashFactory.getInstance(algo);
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

}