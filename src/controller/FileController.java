package controller;

import model.file.AES;
import model.file.AFileCipher;
import model.file.DES;
import model.text.ITextCipher;
import view.MainFrame;

public class FileController {
    private final MainFrame view;


    // File cipher models
    private final AES aes = new AES();
    private final DES des = new DES();

    public FileController(MainFrame view) {
        this.view = view;

    }

    public void importKeyText(ITextCipher cipher) {
    }

    public void exportKeyText(ITextCipher cipher) {

    }

    public void copyKeyText(ITextCipher cipher) {

    }

    public void encryptFile(AFileCipher cipher, String src, String des) {


    }

    public void decryptFile(AFileCipher cipher, String src, String des) {


    }
}
