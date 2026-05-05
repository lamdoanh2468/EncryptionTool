package model.cipher.file;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class AES extends AFileSymCipher {

    public AES() {
        super("AES", "CBC", "PKCS5Padding");
        keySizes = Arrays.asList(128, 192, 256);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        AES aes = new AES();
        aes.genKey(aes.keySizes.get(2));
        aes.encryptFile("C:\\Users\\lamdo\\OneDrive\\Desktop\\Báo cáo Nền tảng Thời trang & Xưởng may.pptx", "C:\\Users\\lamdo\\OneDrive\\Desktop\\Báo cáo Nền tảng Thời trang & Xưởng may_enc.pptx");
        aes.decryptFile("C:\\Users\\lamdo\\OneDrive\\Desktop\\Báo cáo Nền tảng Thời trang & Xưởng may_enc.pptx", "C:\\Users\\lamdo\\OneDrive\\Desktop\\Báo cáo Nền tảng Thời trang & Xưởng may_dec.pptx");

    }

    @Override
    public String getAlgorithm() {
        return "AES";
    }

    @Override
    public List<String> getSupportedModes() {
        return Arrays.asList("ECB", "CBC","PCBC","CFB","OFB","CTR");
    }

    @Override
    public List<String> getSupportedPaddings() {
        return Arrays.asList("PKCS5Padding", "NoPadding");
    }


    @Override
    public SecretKey genKey(int keySize) throws NoSuchAlgorithmException {
        // TODO Auto-generated method stub
        validateKeySize(keySize);
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(keySize);
        key = keyGen.generateKey();
        return key;
    }

    // ==================== encrypt / decrypt text ====================
    @Override
    public byte[] encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        Cipher cipher = Cipher.getInstance(getTransformation());
        if (getTransformation().contains("/ECB/")) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            IvParameterSpec initVector = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, key, initVector);
        }
        byte[] cipherData = data.getBytes(StandardCharsets.UTF_8);
        return cipher.doFinal(cipherData);
    }

    @Override
    public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        Cipher cipher = Cipher.getInstance(getTransformation());
        if (getTransformation().contains("/ECB/")) {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            IvParameterSpec initVector = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, key, initVector);
        }
        byte[] decryptedData = cipher.doFinal(data);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    // encryptFileSymmetric / decryptFileSymmetric
    @Override
    public boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(getTransformation());
        if (getTransformation().contains("/ECB/")) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            IvParameterSpec initVector = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.ENCRYPT_MODE, key, initVector);
        }

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
        CipherInputStream cis = new CipherInputStream(bis, cipher);

        int count = 0;
        byte[] readBuffer = new byte[1024];
        while ((count = cis.read(readBuffer)) != -1) {
            bos.write(readBuffer, 0, count);
        }
//        readBuffer = cipher.doFinal();
//        if (readBuffer != null) {
//            bos.write(readBuffer);
//        }
        bis.close();
        bos.flush();
        bos.close();

        return true;
    }

    @Override
    public boolean decryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        Cipher cipher = Cipher.getInstance(getTransformation());
        if (getTransformation().contains("/ECB/")) {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            IvParameterSpec initVector = new IvParameterSpec(new byte[16]);
            cipher.init(Cipher.DECRYPT_MODE, key, initVector);
        }

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
        CipherOutputStream cos = new CipherOutputStream(bos, cipher);

        int count;
        byte[] readBuffer = new byte[1024];
        while ((count = bis.read(readBuffer)) != -1) {
            cos.write(readBuffer, 0, count);
        }
        cos.flush();
        cos.close();
        bis.close();
        return true;
    }

}
