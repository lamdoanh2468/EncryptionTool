package controller.file;

import model.cipher.file.AFileAsymCipher;
import model.cipher.file.AFileSymCipher;
import model.cipher.file.FileCipherFactory;
import view.MainFrame;
import view.file.asymmetric.AsymmetricPanel;
import view.file.hash.HashFilePanel;
import view.file.symmetric.SymmetricPanel;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.security.*;

public class FileController {
    public final MainFrame view;
    // Controllers
    private final SymmetricFileController symmetricController;
    private final AsymmetricFileController asymmetricController;
    private final HashFileController hashController;
    public SecretKey currentKey;
    public PublicKey currentPublicKey;
    public PrivateKey currentPrivateKey;
    // Ciphers
    private AFileSymCipher currentSymCipher;
    private AFileAsymCipher currentAsymCipher;

    public FileController(MainFrame view) {
        this.view = view;
        this.symmetricController = new SymmetricFileController(this);
        this.asymmetricController = new AsymmetricFileController(this);
        this.hashController = new HashFileController(this);
    }

    public AFileSymCipher getSymCipher(String algoName) {
        this.currentSymCipher = FileCipherFactory.getSymmetricCipher(algoName);
        return currentSymCipher;
    }

    public AFileAsymCipher getAsymCipher(String algoName) {
        this.currentAsymCipher = FileCipherFactory.getAsymmetricCipher(algoName);
        return currentAsymCipher;
    }

    public SymmetricFileController getSymmetricController() {
        return symmetricController;
    }

    public AsymmetricFileController getAsymmetricController() {
        return asymmetricController;
    }

    public HashFileController getHashController() {
        return hashController;
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
            updateStatus("Đã chọn file, hãy chọn loại mã hóa và thông tin thuật toán");
            updateEncryptDecryptButtons();
            setCombosEnabled(true);
            return selectedFile;
        }
        return null;
    }


    public void handleCipherException(Exception ex, String action) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }

        String title = "Lỗi " + action + " file";
        String message = buildErrorMessage(root, ex, action);

        JOptionPane.showMessageDialog(view, message, title, JOptionPane.ERROR_MESSAGE);
    }

    private String buildErrorMessage(Throwable root, Exception ex, String action) {

        if (root instanceof InvalidKeyException) {
            return "Khóa không hợp lệ hoặc không phù hợp với thuật toán\n"
                    + "• Khóa chưa được tạo\n"
                    + "• Hoặc nhập khoá bị sai kích thước\n"
                    + "Vui lòng tạo khóa mới hoặc nhập lại khóa đúng.";

        } else if (root instanceof BadPaddingException) {
            return "Giải mã thất bại (Bad Padding)\n"
                    + "Nguyên nhân thường gặp:\n"
                    + "• Khóa sai\n"
                    + "• File không phải là file đã được mã hóa bởi chương trình này\n"
                    + "• File bị hỏng hoặc đã bị chỉnh sửa sau khi mã hóa";

        } else if (root instanceof IllegalBlockSizeException) {
            return "Kích thước khối dữ liệu không hợp lệ\n"
                    + "File có thể bị hỏng, không đầy đủ, hoặc không phải định dạng mã hóa hợp lệ.";

        } else if (root instanceof InvalidAlgorithmParameterException) {
            return "Tham số thuật toán không hợp lệ\n"
                    + "Thường xảy ra khi IV (Initialization Vector) bị sai hoặc mode mã hóa không đúng.";

        } else if (root instanceof NoSuchAlgorithmException) {
            return "Thuật toán không được hỗ trợ\n"
                    + "Máy tính của bạn không hỗ trợ thuật toán "
                    + (action.contains("Mã hóa") ? "AES/DES" : "này") + ".";

        } else if (root instanceof NoSuchPaddingException) {
            return "Chế độ Padding không được hỗ trợ\n"
                    + "Vấn đề liên quan đến cấu hình mã hóa (thường hiếm gặp).";

        } else if (ex instanceof IOException) {
            return "Lỗi đọc/ghi file\n"
                    + "• File nguồn không tồn tại\n"
                    + "• Không có quyền đọc/ghi\n"
                    + "• Đường dẫn lưu file bị trùng hoặc bị khóa";

        } else {
            return "Đã xảy ra lỗi không xác định khi " + action.toLowerCase() + " file\n"
                    + "Chi tiết kỹ thuật: "
                    + ex.getClass().getSimpleName() + " – " + ex.getMessage();
        }
    }

    public void copyKey(JTextArea keyArea) {
        if (keyArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Không có khóa để sao chép", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(keyArea.getText()), null);
        JOptionPane.showMessageDialog(null, "Sao chép khóa thành công");
    }


    public void updateStatus(String message) {
        if (view.filePanel != null && view.filePanel.statusLabel != null) {
            view.filePanel.statusLabel.setText(message);
            view.filePanel.statusLabel.setForeground(new Color(0, 128, 0)); // màu xanh
        }
    }

    public void setFilePath(String label) {
        if (view.filePanel != null && view.filePanel.filePathLabel != null) {

            view.filePanel.selectedFile = null;

            // Clear previous file path
            view.filePanel.filePathLabel.setText(label);
            view.filePanel.filePathLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
            view.filePanel.filePathLabel.setForeground(MainFrame.TXT_MUTED);
            view.filePanel.filePathLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(MainFrame.BORDER_CLR),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            view.filePanel.filePathLabel.setBackground(MainFrame.BG_INPUT);
            view.filePanel.filePathLabel.setOpaque(true);

            // Hide encrypt/decrypt buttons
            view.filePanel.encryptFileBtn.setVisible(false);
            view.filePanel.decryptFileBtn.setVisible(false);

            setCombosEnabled(false);
        }
    }

    public void removeKeyArea(JTextArea... keyAreas) {
        for (JTextArea keyArea : keyAreas) {
            keyArea.setText("");
        }
    }


    public void setSymmetricPanel(SymmetricPanel panel) {
        this.symmetricController.setSymmetricPanel(panel);
    }

    public void setAsymmetricPanel(AsymmetricPanel panel) {
        this.asymmetricController.setAsymmetricPanel(panel);
    }
    public void setHashPanel(HashFilePanel panel){
        this.hashController.setHashFilePanel(panel);
    }

    public void updateEncryptDecryptButtons() {
        if (view.filePanel == null) return;

        boolean hasFile = view.filePanel.selectedFile != null;
        boolean hasKey = currentKey != null || currentPublicKey != null;

        // Set visibility of buttons
        view.filePanel.encryptFileBtn.setVisible(hasFile);
        view.filePanel.decryptFileBtn.setVisible(hasFile);

        // Enable/Disable buttons
        view.filePanel.encryptFileBtn.setEnabled(hasFile && hasKey);
        view.filePanel.decryptFileBtn.setEnabled(hasFile && hasKey);
    }

    public void setCombosEnabled(boolean enabled) {
        if (view.filePanel != null && view.filePanel.fileSelectorPanel != null) {
            view.filePanel.fileSelectorPanel.setCombosEnabled(enabled);
        }
    }

    public void showWarning(String message) {
        JOptionPane.showMessageDialog(view, message, "Cảnh báo", JOptionPane.WARNING_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(view, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

}






