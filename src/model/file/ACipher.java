package model.file;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public abstract class ACipher implements IFileCipher {
    protected SecretKey key;
    protected String transformation; // mode and padding
    protected List<Integer> keySizes;

    public ACipher(String transformation) {
        this.transformation = transformation;
        this.keySizes = new ArrayList<>();
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

    // === Key Size ===
    public abstract List<Integer> getSupportedKeySizes();

    // === Transformation  ===
    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }


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
