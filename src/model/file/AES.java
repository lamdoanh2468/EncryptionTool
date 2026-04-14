package model.file;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AES extends AFileCipher {

    public static void main(String[] args) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidKeyException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        AES aes = new AES();
        aes.genKey(aes.keySizes.get(2));
        aes.encryptFile("C:\\Users\\lamdo\\Downloads\\artplayer_06_12.png","C:\\Users\\lamdo\\Downloads\\artplayer_06_12_enc.png");
        aes.decryptFile("C:\\Users\\lamdo\\Downloads\\artplayer_06_12_enc.png","C:\\Users\\lamdo\\Downloads\\artplayer_06_12_dec.png");

    }

    public AES() {
        super("AES/CBC/PKCS5Padding"); // default mode and padding
        keySizes = Arrays.asList(128,192,256);
    }

    @Override
    public String getAlgorithm() {
        return "AES";
    }

    @Override
    public List<Integer> getSupportedKeySizes() {
        return keySizes;
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
        IvParameterSpec initVector =  new IvParameterSpec(new byte[16]);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key,initVector);
        byte[] cipherData = data.getBytes(StandardCharsets.UTF_8);
        return cipher.doFinal(cipherData);
    }

    @Override
    public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        IvParameterSpec initVector =  new IvParameterSpec(new byte[16]);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key,initVector);
        byte[] decryptedData = cipher.doFinal(data);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }
    // ==================== encryptFile / decryptFile ====================
    @Override
    public boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        IvParameterSpec initVector =  new IvParameterSpec(new byte[16]);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.ENCRYPT_MODE, key,initVector);

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
    public boolean decryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        IvParameterSpec initVector =  new IvParameterSpec(new byte[16]);
        Cipher cipher = Cipher.getInstance(transformation);
        cipher.init(Cipher.DECRYPT_MODE, key,initVector);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
        CipherOutputStream cos = new CipherOutputStream(bos, cipher);

        int count;
        byte[] readBuffer = new byte[1024];
        while ((count = bis.read(readBuffer)) != -1) {
            cos.write(readBuffer, 0, count);
        }
        bos.flush();
        bos.close();
        bis.close();
        return true;
    }

}
