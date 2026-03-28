//package controller;
//
//import model.ITextCipher;
//import model.ITextCipher.Params;
//import view.MainFrame;
//
//import java.awt.*;
//import java.awt.datatransfer.Clipboard;
//import java.awt.datatransfer.StringSelection;
//import java.io.File;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//
//public class CipherController {
//    private final ITextCipher model;
//    private final MainFrame view;
//
//    public CipherController(ITextCipher model, MainFrame view) {
//        this.model = model;
//        this.view = view;
//        wireTextTab();
//        wireFileTab();
//    }
//
//    private void wireTextTab() {
//        view.textPanel.encryptBtn.addActionListener(e -> handleText(true));
//        view.textPanel.decryptBtn.addActionListener(e -> handleText(false));
//        view.textPanel.clearBtn.addActionListener(e -> view.textPanel.clearAll());
//        view.textPanel.copyBtn.addActionListener(e -> copyOutputToClipboard());
//    }
//
//    private void wireFileTab() {
//        view.filePanel.encryptFileBtn.addActionListener(e -> handleFile(true));
//        view.filePanel.decryptFileBtn.addActionListener(e -> handleFile(false));
//    }
//
//    private void handleText(boolean encrypt) {
//        try {
//            String input = view.textPanel.getInputText();
//            String algo = view.selectorPanel.getSelectedAlgo();
//            Params params = readParamsFromSelector();
//            String out = encrypt
//                    ? model.encrypt(input, algo, params)
//                    : model.decrypt(input, algo, params);
//            view.textPanel.setOutputText(out);
//        } catch (Exception ex) {
//            view.textPanel.setOutputText("Error: " + ex.getMessage());
//        }
//    }
//
//    private void copyOutputToClipboard() {
//        String out = view.textPanel.outputArea.getText();
//        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//        clipboard.setContents(new StringSelection(out), null);
//    }
//
//    private void handleFile(boolean encrypt) {
//        File selected = view.filePanel.getSelectedFile();
//        if (selected == null) {
//            view.filePanel.setStatus("Chưa chọn file", false);
//            return;
//        }
//
//        String algo = view.selectorPanel.getSelectedAlgo();
//        Params params = readParamsFromSelector();
//
//        try {
//            view.filePanel.setStatus("Đang xử lý...", true);
//            String input = readFileText(selected);
//
//            String out = encrypt
//                    ? model.encrypt(input, algo, params)
//                    : model.decrypt(input, algo, params);
//
//            String suffix = encrypt ? ".enc" : ".dec";
//            File output = new File(selected.getParentFile(), selected.getName() + suffix);
//            writeFileText(output, out);
//            view.filePanel.setStatus("Đã lưu: " + output.getName(), true);
//        } catch (Exception ex) {
//            view.filePanel.setStatus("Lỗi: " + ex.getMessage(), false);
//        }
//    }
//
//    private Params readParamsFromSelector() {
//        Params p = new Params();
//        p.affineA = view.selectorPanel.getAffineA();
//        p.affineB = view.selectorPanel.getAffineB();
//        p.caesarShift = view.selectorPanel.getCaesarShift();
//        p.vigenereKey = view.selectorPanel.getVigenereKey();
//        p.railCount = view.selectorPanel.getRailCount();
//        return p;
//    }
//
//    private String readFileText(File file) throws Exception {
//        byte[] bytes = Files.readAllBytes(file.toPath());
//        // Ưu tiên UTF-8, nếu không hợp lệ thì fallback về charset mặc định.
//        try {
//            return new String(bytes, StandardCharsets.UTF_8);
//        } catch (Exception ignored) {
//            Charset cs = Charset.defaultCharset();
//            return new String(bytes, cs);
//        }
//    }
//
//    private void writeFileText(File file, String content) throws Exception {
//        if (content == null) content = "";
//        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
//    }
//}
//
