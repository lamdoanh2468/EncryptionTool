package controller.file;

import model.file.AFileSymCipher;
import view.file.symmetric.SymmetricPanel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class SymmetricFileController {

    private final FileController fileController;
    private SymmetricPanel symmetricPanel;

    public SymmetricFileController(FileController fileController) {
        this.fileController = fileController;
    }

    public void setSymmetricPanel(SymmetricPanel panel) {
        this.symmetricPanel = panel;
    }

    public void genKey(AFileSymCipher cipher, Integer keySize, JTextArea keyArea) throws NoSuchAlgorithmException {
        fileController.currentKey = cipher.genKey(keySize);
        cipher.loadKey(fileController.currentKey);
        JOptionPane.showMessageDialog(null, "Tạo khóa thành công");

        // Convert key from byte to text
        byte[] encodedKey = fileController.currentKey.getEncoded();
        String keyText = Base64.getEncoder().encodeToString(encodedKey);
        keyArea.setText(keyText);
        fileController.updateEncryptDecryptButtons();
        fileController.updateStatus("Đã có khóa, vui lòng xuất hoặc sao chép khóa để sử dụng lại");
    }

    public boolean importKey(AFileSymCipher cipher, JTextArea keyArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file chứa khóa");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                String encodeKey = Files.readString(selectedFile.toPath());
                byte[] decodeKey = Base64.getDecoder().decode(encodeKey);
                if (!hasValidKeyLength(decodeKey.length, cipher.getAlgorithm())) {
                    JOptionPane.showMessageDialog(null, "Khoá không phù hợp với thuật toán", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                fileController.currentKey = new SecretKeySpec(decodeKey, cipher.getAlgorithm());
                keyArea.setText(encodeKey);
                JOptionPane.showMessageDialog(null, "Nhập khóa từ file thành công");
                fileController.updateEncryptDecryptButtons();
                fileController.updateStatus("Đã nhập khóa, chọn mã hóa hoặc giải mã");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Lỗi khi nhập khoá", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    public void exportKey(AFileSymCipher symCipher, String mode, String padding) throws NoSuchAlgorithmException, IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn đường dẫn để lưu file chứa khoá");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userSelection = fileChooser.showSaveDialog(null);

        if (fileController.currentKey == null) {
            JOptionPane.showMessageDialog(null, "Người dùng chưa tạo khóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            String dirPath = selectedDir.getAbsolutePath();
            if (symCipher.getKey() == null) {
                JOptionPane.showMessageDialog(null, "Chưa tạo cặp khoá", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String keyFilePath = dirPath + File.separator + "symmetric.key";
            String transFilePath = dirPath + File.separator + "sym_transformation.key";

            symCipher.setMode(mode);
            symCipher.setPadding(padding);
            symCipher.exportKey(fileController.currentKey, keyFilePath);
            symCipher.exportTransformation(symCipher.getTransformation(), transFilePath);
            JOptionPane.showMessageDialog(null, "Lưu khóa vào file thành công");

            fileController.updateStatus("Đã lưu khoá và thông tin thuật toán thành công, tiếp tục mã hóa hoặc giải mã");

        }
    }

    public void encryptFileSymmetric(AFileSymCipher cipher, String mode, String padding, File selectedFile)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException,
            IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {

        if (fileController.currentKey == null) {
            JOptionPane.showMessageDialog(null, "Người dùng chưa tạo khóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(null, "Người dùng chưa nhập file", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn đường dẫn để lưu file mã hóa");
        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File savedFile = fileChooser.getSelectedFile();
            cipher.loadKey(fileController.currentKey);
            cipher.setMode(mode);
            cipher.setPadding(padding);
            cipher.encryptFile(selectedFile.getAbsolutePath(), savedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(null, "Mã hóa file thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            // Set key areas and file path to default
            fileController.removeKeyArea(symmetricPanel.keyArea);
            fileController.setFilePath("Chưa chọn file nào...");
            fileController.updateStatus("Mã hóa xong, bạn có thể tiếp tục với file khác");
        }
    }

    public void decryptFileSymmetric(AFileSymCipher cipher, String mode, String padding, File selectedFile)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {

        if (fileController.currentKey == null) {
            JOptionPane.showMessageDialog(null, "Người dùng chưa tạo khóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(null, "Người dùng chưa nhập file", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn đường dẫn để lưu file giải mã");
        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File savedFile = fileChooser.getSelectedFile();
            cipher.loadKey(fileController.currentKey);
            cipher.setMode(mode);
            cipher.setPadding(padding);
            cipher.decryptFile(selectedFile.getAbsolutePath(), savedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(null, "Giải mã file thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            // Set key area and file path to default
            fileController.removeKeyArea(symmetricPanel.keyArea);
            fileController.setFilePath("Chưa chọn file nào...");
            fileController.updateStatus("Giải mã xong, vui lòng kiểm tra file giải mã");
        }
    }

    public void setSymmetricCipherInfo(SymmetricPanel symmetricPanel) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file chứa thông tin thuật toán");
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String transformation = reader.readLine();
                String[] asymParts = transformation.split("/");
                symmetricPanel.modeCombo.setSelectedItem(asymParts[1]);
                symmetricPanel.paddingCombo.setSelectedItem(asymParts[2]);
                JOptionPane.showMessageDialog(null, "Đã lấy xong thông tin thuật toán", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Không thể đọc thông tin thuật toán bất đối xứng", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
}
