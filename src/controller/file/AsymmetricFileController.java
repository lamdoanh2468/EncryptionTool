package controller.file;

import model.cipher.file.AFileAsymCipher;
import model.cipher.file.config.AsymmetricFileConfig;
import view.file.asymmetric.AsymmetricPanel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class AsymmetricFileController {

    private final FileController fileController;
    private AsymmetricPanel asymmetricPanel;

    public AsymmetricFileController(FileController fileController) {
        this.fileController = fileController;
    }

    public void setAsymmetricPanel(AsymmetricPanel panel) {
        this.asymmetricPanel = panel;
    }

    public void genPairKey(String algorithm, int keySize, JTextArea pubKeyArea, JTextArea privKeyArea)
            throws NoSuchAlgorithmException, IOException {

        AFileAsymCipher asymCipher = fileController.getAsymCipher(algorithm);
        asymCipher.genKeyPair(algorithm, keySize, "keypair");

        fileController.currentPublicKey = asymCipher.getPublicKey();
        fileController.currentPrivateKey = asymCipher.getPrivateKey();

        String publicKey = asymCipher.getPublicKeyString();
        String privateKey = asymCipher.getPrivateKeyString();

        pubKeyArea.setText(publicKey);
        privKeyArea.setText(privateKey);
        JOptionPane.showMessageDialog(null, "Tạo cặp khoá thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        fileController.updateEncryptDecryptButtons();
        fileController.updateStatus("Tạo cặp khoá thành công, vui lòng tiếp tục tạo khoá đối xứng");
    }

    public void importPublicKey(AFileAsymCipher cipher, JTextArea keyArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file chứa khóa công khai");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection != JFileChooser.APPROVE_OPTION) return;

        File selectedFile = fileChooser.getSelectedFile();
        try {
            String encodeKey = java.nio.file.Files.readString(selectedFile.toPath()).trim();
            byte[] decodedKey = Base64.getDecoder().decode(encodeKey);

            KeyFactory keyFactory = KeyFactory.getInstance(cipher.getAsymAlgorithm());
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);

            fileController.currentPublicKey = keyFactory.generatePublic(keySpec);
            keyArea.setText(encodeKey);
            JOptionPane.showMessageDialog(null, "Nhập khóa công khai thành công");
            fileController.updateStatus("Đã nhập khóa, sẵn sàng mã hoá file");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi nhập public key", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean importPrivateKey(AFileAsymCipher cipher, JTextArea keyArea) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file chứa khóa bí mật");
        int userSelection = fileChooser.showOpenDialog(null);

        if (userSelection != JFileChooser.APPROVE_OPTION) return false;

        File selectedFile = fileChooser.getSelectedFile();
        try {
            String encodedKey = java.nio.file.Files.readString(selectedFile.toPath()).trim();
            byte[] decodedKey = Base64.getDecoder().decode(encodedKey);

            KeyFactory keyFactory = KeyFactory.getInstance(cipher.getAsymAlgorithm());
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);

            fileController.currentPrivateKey = keyFactory.generatePrivate(keySpec);
            keyArea.setText(encodedKey);
            JOptionPane.showMessageDialog(null, "Nhập private key thành công");
            fileController.updateEncryptDecryptButtons();
            fileController.updateStatus("Đã nhập private key, vui lòng chọn giải mã");
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, "File không đúng định dạng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi nhập private key", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public void exportKeyPair(AFileAsymCipher asymCipher, String mode, String padding) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn đường dẫn để lưu cặp khoá công khai và riêng tư");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedDir = fileChooser.getSelectedFile();
            String dirPath = selectedDir.getAbsolutePath();

            if (asymCipher.getPrivateKey() == null || asymCipher.getPublicKeyString() == null) {
                JOptionPane.showMessageDialog(null, "Chưa tạo cặp khoá", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            asymCipher.exportPublicKey(asymCipher.getPublicKey(), dirPath + File.separator + "public.key");
            asymCipher.exportPrivateKey(asymCipher.getPrivateKey(), dirPath + File.separator + "private.key");

            asymCipher.setAsymMode(mode);
            asymCipher.setAsymPadding(padding);
            asymCipher.exportTransformation(asymCipher.getTransformation(), dirPath + File.separator + "asym_transformation.key");
        }

        JOptionPane.showMessageDialog(null,
                "Xuất khoá thành công!\n• public.key\n• private.key\nTransformation: " + asymCipher.getTransformation(),
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
        fileController.updateStatus("Đã lưu các khoá và thông tin thuật toán thành công, tiếp tục tạo khoá đối xứng ");
    }

    public void encryptFileAsymmetric(AsymmetricFileConfig config)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException,
            IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException {

        if (fileController.currentPublicKey == null) {
            JOptionPane.showMessageDialog(null, "Người dùng chưa tạo khóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (config.getSelectedFile() == null) {
            JOptionPane.showMessageDialog(null, "Người dùng chưa nhập file", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn đường dẫn để lưu file mã hóa");
        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File savedFile = fileChooser.getSelectedFile();
            // Set asymmetric cipher
            config.getAsymCipher().setAsymMode(config.getAsymMode());
            config.getAsymCipher().setAsymPadding(config.getAsymPadding());
            config.getAsymCipher().setSymCipher(config.getSymCipher());

            // Set symmetric cipher
            config.getAsymCipher().setSymKey(fileController.currentKey);
            config.getSymCipher().setMode(config.getSymMode());
            config.getSymCipher().setPadding(config.getSymPadding());

            config.getAsymCipher().encryptFile(config.getSelectedFile().getAbsolutePath(), savedFile.getAbsolutePath());
            JOptionPane.showMessageDialog(null, "Mã hóa file thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            // Set key area and file path to default
            fileController.removeKeyArea(asymmetricPanel.publicKeyArea, asymmetricPanel.privateKeyArea, asymmetricPanel.symKeyArea);
            fileController.setFilePath("Chưa chọn file nào...");
            fileController.updateStatus("Mã hóa xong, bạn có thể tiếp tục với file khác");
        }
    }

    public void decryptFileAsymmetric(AsymmetricFileConfig config)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException,
            IOException, BadPaddingException, InvalidKeyException, IllegalBlockSizeException, NoSuchProviderException {

        if (fileController.currentPrivateKey == null) {
            JOptionPane.showMessageDialog(null, "Người dùng chưa tạo khóa", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        File selectedFile = config.getSelectedFile();
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(null, "Người dùng chưa nhập file", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn đường dẫn để lưu file giải mã");
        int option = fileChooser.showSaveDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File savedFile = fileChooser.getSelectedFile();

            config.getAsymCipher().setAsymMode(config.getAsymMode());
            config.getAsymCipher().setAsymPadding(config.getAsymPadding());
            config.getAsymCipher().decryptFile(selectedFile.getAbsolutePath(), savedFile.getAbsolutePath());

            JOptionPane.showMessageDialog(null, "Giải mã file thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);

            // Set key area and file path to default
            fileController.removeKeyArea(asymmetricPanel.publicKeyArea, asymmetricPanel.privateKeyArea, asymmetricPanel.symKeyArea);
            fileController.setFilePath("Chưa chọn file nào...");
            fileController.updateStatus("Giải mã xong, bạn có thể tiếp tục với file khác");
        }
    }

    public void setAsymmetricCipherInfo(AsymmetricPanel asymmetricPanel) throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file có tên asym_transformation.key ở thư mục lưu khoá");
        int option = fileChooser.showOpenDialog(null);
        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String transformation = reader.readLine();
                String[] asymParts = transformation.split("/");
                asymmetricPanel.asymModeCombo.setSelectedItem(asymParts[1]);
                asymmetricPanel.asymPaddingCombo.setSelectedItem(asymParts[2]);
                JOptionPane.showMessageDialog(null, "Đã lấy xong thông tin thuật toán bất đối xứng", "Thông Báo", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Không thể đọc thông tin thuật toán bất đối xứng", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


}

