package controller.text;

import model.cipher.text.*;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;

public class SymmetricTextController {
    private final MainFrame mainView;

    public SymmetricTextController(MainFrame view) {
        this.mainView = view;
    }

    public void importKey(JTextArea keyArea,JComboBox<String>algos) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Nhập khóa từ đường dẫn");
        int option = fileChooser.showOpenDialog(mainView);

        if (option == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (!selectedFile.getName().toLowerCase().endsWith(".txt")) {
                JOptionPane.showMessageDialog(null, "File không đúng định dạng .txt");
                return;
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                String keyContent = builder.toString();
                String detectedAlgo = detectSymmetricAlgorithm(keyContent);
                algos.setSelectedItem(detectedAlgo);
                JOptionPane.showMessageDialog(null, "Tải khóa thành công");
                keyArea.setText(keyContent);

            } catch (IOException ioe) {
                JOptionPane.showMessageDialog(null, "Lỗi khi nhập khóa");
            }
        }
    }

    public void exportKey(JTextArea keyArea, String extension) {

        String currentKey = keyArea.getText();

        if (currentKey == null || currentKey.trim().isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Chưa có khóa để lưu");
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu khóa");

        int saveResult = chooser.showSaveDialog(mainView);

        if (saveResult != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File saveFile = chooser.getSelectedFile();

        if (!saveFile.getName().toLowerCase().endsWith(extension)) {
            saveFile = new File(saveFile.getAbsolutePath() + "." + extension);
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));

            bw.write(currentKey);
            bw.flush();
            bw.close();

            JOptionPane.showMessageDialog(null, "Lưu khóa thành công");

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Xuất khóa thất bại");
        }
    }

    public <K> void generateKey(ATextCipher<K> cipherObj, JTextArea keyArea) {

        K randomKey = cipherObj.genKey();
        cipherObj.loadKey(randomKey);
        String generatedKey = cipherObj.getKey();
        keyArea.setText(generatedKey);
    }

    public <K> void encryptText(ATextCipher<K> cipherObj, String plainText, String keyString, JTextArea outputArea) {

        if (keyString == null || keyString.trim().isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Chưa có khóa, hãy tạo khóa trước");
            return;
        }

        if (plainText == null || plainText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Bạn chưa nhập văn bản");
            return;
        }

        try {

            K parsedKey = cipherObj.parseKey(keyString.trim());

            if (parsedKey == null) {
                JOptionPane.showMessageDialog(mainView, "Key không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String encryptedResult = cipherObj.encrypt(plainText, parsedKey);
            outputArea.setText(encryptedResult);
            JOptionPane.showMessageDialog(null, "Mã hóa xong");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(mainView, "Có lỗi khi mã hóa, thử tạo key mới");
        }
    }


    public <K> void decryptText(ATextCipher<K> cipherObj, String encryptedText, String keyText, JTextArea inputArea) {

        if (keyText == null || keyText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Thiếu khóa giải mã", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (encryptedText == null || encryptedText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(mainView, "Chưa có nội dung cần giải mã");
            return;
        }

        try {
            K parsedKey = cipherObj.parseKey(keyText.trim());
            if (parsedKey == null) {
                JOptionPane.showMessageDialog(mainView, "Key bị lỗi", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String originalText = cipherObj.decrypt(encryptedText, parsedKey);
            inputArea.setText(originalText);
            JOptionPane.showMessageDialog(null, "Giải mã hoàn tất");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainView, "Lỗi giải mã: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void copyKey(JTextArea keyArea) {

        String keyText = keyArea.getText();

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(keyText), null);

        JOptionPane.showMessageDialog(null, "Đã copy khóa");
    }


    public void toggleOutputArea(boolean isEnable) {

        if (mainView == null) {
            return;
        }

        if (mainView.textPanel != null && mainView.textPanel.outputArea != null) {

            mainView.textPanel.outputArea.setEditable(isEnable);
        }
    }

    public void removeKeyArea(JTextArea... keyAreas) {

        for (JTextArea txt : keyAreas) {

            if (txt != null) {
                txt.setText("");
            }
        }
    }

    public void clearAll() {

        if (mainView == null || mainView.textPanel == null) {
            return;
        }

        JTextArea input = mainView.textPanel.inputArea;
        JTextArea output = mainView.textPanel.outputArea;

        if (input == null || output == null) {
            return;
        }

        input.setText("");
        output.setText("");

        // update lại số ký tự
        mainView.textPanel.updateCount();
    }

    private String detectSymmetricAlgorithm(String key) {

        if (key == null || key.trim().isEmpty()) {
            return null;
        }

        key = key.trim();

        if (key.matches("\\d{1,2}")) {

            int shiftValue = Integer.parseInt(key);
            if (shiftValue >= 0 && shiftValue <= 25) {
                return "Caesar";
            }
        }

        if (key.matches(".*\\d+.*\\d+.*") && key.length() < 15) {
            return "Affine";
        }

        if (key.contains(";") || key.matches(".*\\d+\\s+\\d+.*\\d+.*")) {
            return "Hill";
        }

        if (key.length() >= 20) {
            return "Thay thế";
        }

        if (key.contains(",") || key.contains("-") || key.matches(".*[a-zA-Z].*[0-9].*")) {
            return "Hoán vị";
        }

        if (key.matches("[a-zA-Z]+")) {
            return "Vigenere";
        }

        return "Caesar";
    }

    public ATextCipher<?> getCipher(String algoName) {
       return TextCipherFactory.getCipher(algoName);
    }
}