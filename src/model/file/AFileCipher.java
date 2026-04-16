package model.file;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

public abstract class AFileCipher implements IFileCipher {
    protected final String algorithm;
    protected SecretKey key;
    protected String mode;
    protected String padding;
    protected List<Integer> keySizes;

    public AFileCipher(String algorithm, String defaultMode, String defaultPadding) {
        this.algorithm = algorithm;
        this.mode = defaultMode;
        this.padding = defaultPadding;
    }

    public List<Integer> getKeySizes() {
        return keySizes;
    }

    protected void validateKeySize(int keySize) throws NoSuchAlgorithmException {
        if (!keySizes.contains(keySize)) {
            throw new NoSuchAlgorithmException("Key size " + keySize + " không có được hỗ trợ cho " + getClass().getSimpleName());
        }
    }

    public void loadKey(SecretKey key) {
        if (key == null) {
            throw new IllegalArgumentException("Key không được bỏ trống");
        }
        this.key = key;
    }

    public String getTransformation() {
        return algorithm + "/" + mode + "/" + padding;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        if (getSupportedModes().contains(mode)) {
            this.mode = mode;
        } else {
            throw new IllegalArgumentException("Mode " + mode + " không được hỗ trợ cho " + algorithm);
        }
    }

    public String getPadding() {
        return padding;
    }

    public void setPadding(String padding) {
        if (getSupportedPaddings().contains(padding)) {
            this.padding = padding;
        } else {
            throw new IllegalArgumentException("Padding " + padding + " không được hỗ trợ cho " + algorithm);
        }
    }

    public abstract List<String> getSupportedModes();

    public abstract List<String> getSupportedPaddings();


    public String encryptBase64(String text) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] encrypted = encrypt(text);
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public abstract SecretKey genKey(int keySize) throws NoSuchAlgorithmException;

    public abstract byte[] encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException;

    public abstract String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException;

    public abstract boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException;

    public abstract boolean decryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException;

}
