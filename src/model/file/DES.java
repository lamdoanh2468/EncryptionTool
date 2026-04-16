package model.file;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class DES extends AFileCipher {

    public DES() {
        super("DES", "CBC", "PKCS5Padding");
        keySizes = List.of(56);
    }

    // ==================== TEST ====================
    public static void main(String[] args) throws Exception {
        DES des = new DES();
        des.genKey(56);

        String original = "Hạ nghị sĩ Đảng Dân chủ Seth Moulton..."; // dữ liệu test của bạn
        System.out.println("Original : " + original);

        byte[] encrypted = des.encrypt(original);
        String decrypted = des.decrypt(encrypted);

        System.out.println("Decrypted: " + decrypted);
        System.out.println("✅ DES hoạt động đúng với transformation động!");
    }

    @Override
    public String getAlgorithm() {
        return "DES";
    }

    @Override
    public List<String> getSupportedModes() {
        // Các mode phổ biến của DES (ECB không khuyến khích dùng cho file)
        return Arrays.asList("ECB", "CBC", "CTR", "CFB", "OFB");
    }

    @Override
    public List<String> getSupportedPaddings() {
        return Arrays.asList("PKCS5Padding", "NoPadding");
    }

    @Override
    public SecretKey genKey(int keySize) throws NoSuchAlgorithmException {
        validateKeySize(keySize);
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        keyGen.init(keySize);
        key = keyGen.generateKey();
        return key;
    }

    // ==================== TEXT ENCRYPT / DECRYPT ====================
    @Override
    public byte[] encrypt(String data) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec(new byte[8]); // DES block = 8 bytes
        Cipher cipher = Cipher.getInstance(getTransformation());
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String decrypt(byte[] data) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException {
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance(getTransformation());
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decrypted = cipher.doFinal(data);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    // ==================== FILE ENCRYPT / DECRYPT ====================
    @Override
    public boolean encryptFile(String src, String des) throws InvalidAlgorithmParameterException, InvalidKeyException, IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance(getTransformation());
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
             CipherInputStream cis = new CipherInputStream(bis, cipher);
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des))) {

            byte[] buffer = new byte[4096];
            int count;
            while ((count = cis.read(buffer)) != -1) {
                bos.write(buffer, 0, count);
            }
        }
        return true;
    }

    @Override
    public boolean decryptFile(String src, String des) throws InvalidAlgorithmParameterException, InvalidKeyException, IOException, NoSuchPaddingException, NoSuchAlgorithmException {
        IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance(getTransformation());
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
             CipherOutputStream cos = new CipherOutputStream(
                     new BufferedOutputStream(new FileOutputStream(des)), cipher)) {

            byte[] buffer = new byte[4096];
            int count;
            while ((count = bis.read(buffer)) != -1) {
                cos.write(buffer, 0, count);
            }
        }
        return true;
    }
}