package controller.file;

import model.hash.AFileHash;
import model.hash.HashFactory;
import view.file.hash.HashFilePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedWriter;
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
        String algorithm = (String) fileController.view.filePanel.fileSelectorPanel.algoCombo.getSelectedItem();
        File selectedFile = fileController.view.filePanel.selectedFile;

        try {

            AFileHash hasher = HashFactory.getInstance(algorithm);
            byte[] rawBytes = Files.readAllBytes(selectedFile.toPath());
            String fileContent = new String(rawBytes, StandardCharsets.UTF_8);
            byte[] hashBytes = hasher.hashText(fileContent);
            String finalHash = bytesToHex(hashBytes);

            hashFilePanel.setResult(finalHash);
            fileController.updateStatus("Băm file thành công (" + algorithm + ")");

        } catch (Exception ex) {
            ex.printStackTrace();
            fileController.handleCipherException(ex, "băm file");
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public void copyHashResult() {
        if (hashFilePanel == null || hashFilePanel.resultArea.getText().isEmpty()) {
            fileController.showWarning("Chưa có kết quả để sao chép");
            return;
        }

        String hashText = hashFilePanel.resultArea.getText();
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(hashText), null);
        fileController.showInfo("Đã copy hash");
    }

    public void saveHashToFile() {
        if (hashFilePanel == null || hashFilePanel.resultArea.getText().isEmpty()) {
            fileController.showWarning("Chưa có kết quả để lưu");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu hash ra file");
        chooser.setSelectedFile(new File("hash.txt"));

        int chooseResult = chooser.showSaveDialog(fileController.view);
        if (chooseResult != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File saveFile = chooser.getSelectedFile();

        if (!saveFile.getName().toLowerCase().endsWith(".txt")) {
            saveFile = new File(saveFile.getAbsolutePath() + ".txt");
        }

        try {

            BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(saveFile));

            writer.write(hashFilePanel.resultArea.getText());
            writer.flush();
            writer.close();

            fileController.showInfo("Lưu hash thành công!");

        } catch (IOException e) {

            fileController.handleCipherException(e, "lưu hash");
        }
    }
}