package controller;

import model.file.AES;
import model.file.AFileCipher;
import model.file.DES;
import view.MainFrame;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class FileController {
    public static SecretKey currentKey;
    private final MainFrame view;

    // File cipher models
    private final AES aes = new AES();
    private final DES des = new DES();

    public FileController(MainFrame view) {
        this.view = view;
    }

    public File chooseFile(JLabel filePathLabel) {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Chọn file để tiếp tục");
        int result = fc.showOpenDialog(view);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fc.getSelectedFile();
            filePathLabel.setText(selectedFile.getAbsolutePath());
            filePathLabel.setForeground(MainFrame.TXT_MAIN);
            JOptionPane.showMessageDialog(null, "Chọn file thành công");
            view.filePanel.statusLabel.setText("Đã chọn file, hãy tạo khóa");
            return selectedFile;
        }
        return null;
    }

    public void genKey(AFileCipher cipher, Integer keySize,JTextArea keyArea) throws NoSuchAlgorithmException {
        currentKey = cipher.genKey(keySize);
        cipher.loadKey(currentKey);
        JOptionPane.showMessageDialog(null, "Tạo khóa thành công");

        // Convert key from byte to text
        byte[] encodedKey = currentKey.getEncoded();
        String keyText = Base64.getEncoder().encodeToString(encodedKey);
        keyArea.setText(keyText);
        view.filePanel.statusLabel.setText("Đã có khóa, chọn mã hóa hoặc giải mã");

    }

    public void importKey(AFileCipher cipher) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file chứa khóa");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String encodeKey = Files.readString(selectedFile.toPath());
                byte[] decodeKey = Base64.getDecoder().decode(encodeKey);
                if (!hasValidKeyLength(decodeKey.length, cipher.getAlgorithm())) {
                    JOptionPane.showMessageDialog(view, "Key không phù hợp với thuật toán", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                currentKey = new SecretKeySpec(decodeKey, cipher.getAlgorithm());
                System.out.println(currentKey);
                JOptionPane.showMessageDialog(view, "Nhập khóa từ file thành công");
                view.filePanel.statusLabel.setText("Đã nhập khóa, chọn mã hóa hoặc giải mã");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(view, "Lỗi khi nhập key", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        }
    }

    public void exportKey() throws NoSuchAlgorithmException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu khóa dưới dạng file");
        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                byte[] keyToBytes = currentKey.getEncoded();
                String encodedKey = Base64.getEncoder().encodeToString(keyToBytes);
                System.out.println(encodedKey);
                writer.write(encodedKey);
                JOptionPane.showMessageDialog(null, "Lưu khóa vào file thành công");

            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Lỗi khi xuất khóa");
            }
        }
    }


    private boolean hasValidKeyLength(int keyLength, String algorithm) {
        int[] expectedKeyLength = getExpectedLength(algorithm);
        for (int expectKey : expectedKeyLength) {
            if (keyLength == expectKey) return true;
        }
        return false;


    }

    private int[] getExpectedLength(String algorithm) {
        switch (algorithm.toUpperCase()) {
            case "AES":
                return new int[]{16, 24, 32};
            case "DES":
                return new int[]{8};
            default:
                throw new IllegalArgumentException("Không hỗ trợ thuật toán này: " + algorithm);
        }
    }

    public void encryptFile(AFileCipher cipher, File selectedFile) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {
        if (currentKey == null) {
            JOptionPane.showMessageDialog(view, "Người dùng chưa tạo khóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(view, "Người dùng chưa nhập file", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn đường dẫn để lưu file mã hóa");
        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File savedFile = fileChooser.getSelectedFile();
            cipher.loadKey(currentKey);
            cipher.encryptFile(selectedFile.getAbsolutePath(), savedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(view, "Mã hóa file thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            view.filePanel.statusLabel.setText("Mã hóa xong, bạn có thể tiếp tục với file khác");
        }
    }

    public void decryptFile(AFileCipher cipher, File selectedFile) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {
        if (currentKey == null) {
            JOptionPane.showMessageDialog(view, "Người dùng chưa tạo khóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(view, "Người dùng chưa nhập file", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn đường dẫn để lưu file giải mã");
        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File savedFile = fileChooser.getSelectedFile();
            cipher.loadKey(currentKey);
            cipher.decryptFile(selectedFile.getAbsolutePath(), savedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(view, "Giải mã file thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            view.filePanel.statusLabel.setText("Giải mã xong, vui lòng kiểm tra file giải mã");
        }
    }

    public AFileCipher getCipher(String algoName) {
        if (algoName == null) return null;
        return switch (algoName) {
            case "AES" -> aes;
            case "DES" -> des;
//            case "RSA" -> rsa;
            default -> null;
        };
    }
}
