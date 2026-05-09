package controller.text;

import model.cipher.text.*;
import view.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class TextController {
    private final MainFrame view;

    // Controllers
    private final SymmetricTextController symmetricController;
    private final AsymmetricTextController asymmetricController;
    private final HashController hashController;

    public TextController(MainFrame view) {
        this.view = view;
        this.symmetricController = new SymmetricTextController(view);
        this.asymmetricController = new AsymmetricTextController(view);
        this.hashController = new HashController(view);
    }

    // Symmetric
    public void importKey(JTextArea keyArea) {
        symmetricController.importKey(keyArea);
    }

    public void exportKey(JTextArea keyArea, String ext) {
        symmetricController.exportKey(keyArea, ext);
    }

    public void copyKey(JTextArea keyArea) {
        symmetricController.copyKey(keyArea);
    }

    public void removeKeyArea(JTextArea... keyArea) {
        symmetricController.removeKeyArea(keyArea);
    }

    public void clearAll() {
        symmetricController.clearAll();
    }

    public void toggleOutputArea(boolean enable) {
        symmetricController.toggleOutputArea(enable);
    }

    public <K> void generateKey(ATextCipher<K> cipher, JTextArea keyArea) {
        symmetricController.generateKey(cipher, keyArea);
    }

    public <K> void encryptText(ATextCipher<K> cipher, String text, String keyText, JTextArea outputArea) {
        symmetricController.encryptText(cipher, text, keyText, outputArea);
    }

    public <K> void decryptText(ATextCipher<K> cipher, String text, String keyText, JTextArea inputArea) {
        symmetricController.decryptText(cipher, text, keyText, inputArea);
    }

    public ATextCipher<?> getCipher(String algoName) {
        return symmetricController.getCipher(algoName);
    }

    // Asymmetric
    public void genKeyPair(JTextArea publicArea, JTextArea privateArea) {
        asymmetricController.genKeyPair(publicArea, privateArea);
    }

    public void encryptRSA(String plainText, JTextArea publicKeyArea, JTextArea outputArea) {
        asymmetricController.encryptRSA(plainText, publicKeyArea, outputArea);
    }

    public void decryptRSA(String cipherText, JTextArea privateKeyArea, JTextArea outputArea) {
        asymmetricController.decryptRSA(cipherText, privateKeyArea, outputArea);
    }

    // Hash function
    public void hashText(String algo, String text, JTextArea outputArea) {
        hashController.hashText(algo, text, outputArea);
    }

    public void verifyHash(String algo, String text, String expectedHash) {
        hashController.verifyHash(algo, text, expectedHash);
    }
}