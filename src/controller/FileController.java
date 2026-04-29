package controller;

import model.file.*;
import view.MainFrame;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class FileController {
    public static SecretKey currentKey;
    private final MainFrame view;
    // File cipher models
    private final AES aes = new AES();
    private final DES des = new DES();
    private final RSA rsa = new RSA();
    public PublicKey currentPublicKey;
    public PrivateKey currentPrivateKey;

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

    public void genKey(AFileSymCipher cipher, Integer keySize, JTextArea keyArea) throws NoSuchAlgorithmException {
        currentKey = cipher.genKey(keySize);
        cipher.loadKey(currentKey);
        JOptionPane.showMessageDialog(null, "Tạo khóa thành công");

        // Convert key from byte to text
        byte[] encodedKey = currentKey.getEncoded();
        String keyText = Base64.getEncoder().encodeToString(encodedKey);
        keyArea.setText(keyText);
        view.filePanel.statusLabel.setText("Đã có khóa, chọn mã hóa hoặc giải mã");

    }

    public void importKey(AFileSymCipher cipher, JTextArea keyArea) {
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
                keyArea.setText(encodeKey);
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
            case "RSA":
                return new int[]{1024, 2048, 4096};
            default:
                throw new IllegalArgumentException("Không hỗ trợ thuật toán này: " + algorithm);
        }
    }

    public void encryptFile(AFileSymCipher cipher, String mode, String padding, File selectedFile) throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
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
            cipher.setMode(mode);
            cipher.setPadding(padding);
            cipher.encryptFile(selectedFile.getAbsolutePath(), savedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(view, "Mã hóa file thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            view.filePanel.statusLabel.setText("Mã hóa xong, bạn có thể tiếp tục với file khác");
        }
    }

    public void decryptFile(AFileSymCipher cipher, String mode, String padding, File selectedFile) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, IOException, BadPaddingException, InvalidKeyException {
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
            cipher.setMode(mode);
            cipher.setPadding(padding);
            cipher.decryptFile(selectedFile.getAbsolutePath(), savedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(view, "Giải mã file thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            view.filePanel.statusLabel.setText("Giải mã xong, vui lòng kiểm tra file giải mã");
        }
    }

    public void handleCipherException(Exception ex, String action, String operation) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        String title = "Lỗi " + action + " file";

        String message = "<html>" + buildErrorMessage(root, ex, action) + "</html>";

        JOptionPane.showMessageDialog(view, message, title, JOptionPane.ERROR_MESSAGE);

    }

    private String buildErrorMessage(Throwable root, Exception ex, String action) {

        if (root instanceof InvalidKeyException) {
            return "<b>Khóa không hợp lệ hoặc không phù hợp với thuật toán</b><br>" + "• Khóa chưa được tạo<br>" + "• Hoặc khóa import sai kích thước (AES: 16/24/32 bytes, DES: 8 bytes)<br>" + "<br>Vui lòng tạo khóa mới hoặc import lại khóa đúng.";
        } else if (root instanceof BadPaddingException) {
            return "<b>Giải mã thất bại (Bad Padding)</b><br>" + "Nguyên nhân thường gặp:<br>" + "• Khóa sai<br>" + "• File không phải là file đã được mã hóa bởi chương trình này<br>" + "• File bị hỏng hoặc đã bị chỉnh sửa sau khi mã hóa";
        } else if (root instanceof IllegalBlockSizeException) {
            return "<b>Kích thước khối dữ liệu không hợp lệ</b><br>" + "File có thể bị hỏng, không đầy đủ, hoặc không phải định dạng mã hóa hợp lệ.";
        } else if (root instanceof InvalidAlgorithmParameterException) {
            return "<b>Tham số thuật toán không hợp lệ</b><br>" + "Thường xảy ra khi IV (Initialization Vector) bị sai hoặc mode mã hóa không đúng.";
        } else if (root instanceof NoSuchAlgorithmException) {
            return "<b>Thuật toán không được hỗ trợ</b><br>" + "Máy tính của bạn không hỗ trợ thuật toán " + (action.contains("Mã hóa") ? "AES/DES" : "này") + ".";
        } else if (root instanceof NoSuchPaddingException) {
            return "<b>Chế độ Padding không được hỗ trợ</b><br>" + "Vấn đề liên quan đến cấu hình mã hóa (thường hiếm gặp).";
        } else if (ex instanceof IOException) {
            return "<b>Lỗi đọc/ghi file</b><br>" + "• File nguồn không tồn tại<br>" + "• Không có quyền đọc/ghi<br>" + "• Đường dẫn lưu file bị trùng hoặc bị khóa";
        } else {
            return " <b>Đã xảy ra lỗi không xác định khi " + action.toLowerCase() + " file</b><br>" + "<small>Chi tiết kỹ thuật: " + ex.getClass().getSimpleName() + " – " + ex.getMessage() + "</small>";
        }
    }

    public void copyKey(JTextArea keyArea) {
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(keyArea.getText()), null);
        JOptionPane.showMessageDialog(null, "Sao chép khóa thành công");
    }

    public AFileSymCipher getSymCipher(String algoName) {
        if (algoName == null) return null;
        return switch (algoName) {
            case "AES" -> aes;
            case "DES" -> des;
            default -> null;
        };

    }

    public AFileAsymCipher getAsymCipher(String algoName) {
        if (algoName == null) return null;
        return switch (algoName) {
            case "RSA" -> rsa;
            default -> null;
        };
    }

    public AFileSymCipher getConfiguredSymCipher(String algoName, String mode, String padding) {
        AFileSymCipher cipher = getSymCipher(algoName);
        if (cipher != null) {
            cipher.setMode(mode);
            cipher.setPadding(padding);
        }
        return cipher;
    }

    public void genPairKey(String algorithm, int keySize, JTextArea pubKeyArea, JTextArea privKeyArea) throws NoSuchAlgorithmException, IOException {
        AFileAsymCipher asymCipher = getAsymCipher(algorithm);

        // Generate key pair
        asymCipher.genKeyPair(algorithm, keySize, "keypair");
        String publicKey = asymCipher.getPublicKeyString();
        String privateKey = asymCipher.getPrivateKeyString();
        // Set key text to text area
        pubKeyArea.setText(publicKey);
        privKeyArea.setText(privateKey);
        JOptionPane.showMessageDialog(view, "Tạo cặp khoá thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        view.filePanel.statusLabel.setText(" Đâ tạo xong, vui lòng tiếp tục tạo khoá đối xứng");
    }

    public void importPublicKey(AFileAsymCipher cipher, JTextArea keyArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file chứa khóa công khai");

        int userSelection = fileChooser.showOpenDialog(view);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();

        try {
            String encodeKey = Files.readString(selectedFile.toPath()).trim();
            byte[] decodedKey = Base64.getDecoder().decode(encodeKey);

            KeyFactory keyFactory = KeyFactory.getInstance(cipher.getAsymAlgorithm());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);

            currentPublicKey = keyFactory.generatePublic(keySpec);

            keyArea.setText(encodeKey);
            view.filePanel.statusLabel.setText("Đã nhập khóa, chọn mã hóa hoặc giải mã");

            JOptionPane.showMessageDialog(view, "Nhập khóa công khai thành công");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Lỗi khi nhập public key",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void importPrivateKey(AFileAsymCipher cipher, JTextArea keyArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file chứa khóa bí mật");

        int userSelection = fileChooser.showOpenDialog(view);

        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File selectedFile = fileChooser.getSelectedFile();

        try {
            String encodedKey = Files.readString(selectedFile.toPath()).trim();
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

            KeyFactory keyFactory = KeyFactory.getInstance(cipher.getAsymAlgorithm());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

            currentPrivateKey = keyFactory.generatePrivate(keySpec);

            keyArea.setText(encodedKey);
            view.filePanel.statusLabel.setText("Đã nhập private key, chọn mã hóa hoặc giải mã");

            JOptionPane.showMessageDialog(view, "Nhập private key thành công");

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    view,
                    "File không đúng định dạng Base64",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    view,
                    "Lỗi khi nhập private key",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void exportAllKeys(AFileAsymCipher asymCipher, AFileSymCipher symCipher) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn đường dẫn để lưu cặp khoá công khai và riêng tư");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            String dirPath = selectedDir.getAbsolutePath();

            if(asymCipher.getPrivateKey() == null || asymCipher.getPublicKeyString() == null){
                JOptionPane.showMessageDialog(view, "Chưa tạo cặp khoá", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Generate key pair
            asymCipher.exportPublicKey(asymCipher.getPublicKey(),
                    dirPath + File.separator + "public.key");
            asymCipher.exportPrivateKey(asymCipher.getPrivateKey(),
                    dirPath + File.separator + "private.key");
            symCipher.exportKey(symCipher.getKey(), dirPath + File.separator + "symmetric.key");

            JOptionPane.showMessageDialog(view, "Lưu khoá thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            view.filePanel.statusLabel.setText(" Đã lưu các khoá vào file thành công");
        }
    }

}



