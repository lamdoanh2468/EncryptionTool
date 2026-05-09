package controller.file;

import model.hash.*;
import view.file.hash.HashFilePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class HashFileController {

    private final FileController fileController;
    private HashFilePanel hashFilePanel;

    public HashFileController(FileController fileController) {
        this.fileController = fileController;
    }

    public void setHashFilePanel(HashFilePanel panel) {
        this.hashFilePanel = panel;
    }

    public void hashSelectedFile() {
        if (fileController.view.filePanel == null || fileController.view.filePanel.selectedFile == null) {
            fileController.showWarning("Vui lòng chọn file trước!");
            return;
        }

        String algo = (String) fileController.view.filePanel.fileSelectorPanel.algoCombo.getSelectedItem();
        File file = fileController.view.filePanel.selectedFile;

        try {
            AFileHash hasher = getHashInstance(algo);
            byte[] fileBytes = Files.readAllBytes(file.toPath());
            String fileContent = new String(fileBytes, StandardCharsets.UTF_8);
            byte[] hashBytes = hasher.hashText(fileContent);
            String hexResult = bytesToHex(hashBytes);

            hashFilePanel.setResult(hexResult);
            fileController.updateStatus("Băm file thành công (" + algo + ")");

        } catch (Exception e) {
            fileController.handleCipherException(e, "băm file");
        }
    }

    private AFileHash getHashInstance(String algo) {
        if (algo == null || algo.isBlank()) {
            return new SHA256();
        }

        AFileHash hashInstance = null;
        switch (algo) {
            // Legacy
            case "MD2" -> hashInstance = new MD2();
            case "MD5" -> hashInstance = new MD5();
            case "SHA-1", "SHA1" -> hashInstance = new SHA1();

            // SHA-2
            case "SHA-224" -> hashInstance = new SHA224();
            case "SHA-256" -> hashInstance = new SHA256();
            case "SHA-384" -> hashInstance = new SHA384();
            case "SHA-512" -> hashInstance = new SHA512();
            case "SHA-512/224", "SHA512/224", "SHA512224" -> hashInstance = new SHA_512_224();
            case "SHA-512/256", "SHA512/256", "SHA512256" -> hashInstance = new SHA_512_256();

            // SHA-3 F
            case "SHA3-224" -> hashInstance = new SHA3_224();
            case "SHA3-256" -> hashInstance = new SHA3_256();
            case "SHA3-384" -> hashInstance = new SHA3_384();
            case "SHA3-512" -> hashInstance = new SHA3_512();

            default -> throw new IllegalArgumentException("Hàm băm không được hỗ trợ: " + algo);
        };
        return hashInstance;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public void copyHashResult() {
        if (hashFilePanel == null || hashFilePanel.resultArea.getText().isEmpty()) {
            fileController.showWarning("Chưa có kết quả để sao chép");
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(hashFilePanel.resultArea.getText()), null);
        fileController.showInfo("Đã sao chép hash");
    }

    public void saveHashToFile() {
        if (hashFilePanel == null || hashFilePanel.resultArea.getText().isEmpty()) {
            fileController.showWarning("Chưa có kết quả để lưu");
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Lưu hash thành file");
        fc.setSelectedFile(new java.io.File("hash.txt"));

        int option = fc.showSaveDialog(fileController.view);
        if (option == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fc.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".txt")) {
                file = new java.io.File(file.getAbsolutePath() + ".txt");
            }
            try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(file))) {
                writer.write(hashFilePanel.resultArea.getText());
                fileController.showInfo("Lưu hash thành công!");
            } catch (IOException e) {
                fileController.handleCipherException(e, "lưu hash");
            }
        }
    }
}