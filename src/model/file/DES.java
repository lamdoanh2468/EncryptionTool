package model.file;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class DES extends AFileCipher {

    public DES() {
        super("DES/CBC/PKCS5Padding");
        keySizes = List.of(56);   //
    }

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, IOException, InvalidAlgorithmParameterException {
        String data = "Hạ nghị sĩ Đảng Dân chủ Seth Moulton (bang Massachusetts), thành viên Ủy ban Quân dịch Hạ viện, đã công khai chỉ trích cách tiếp cận của Tổng thống Donald Trump đối với cuộc xung đột tại Iran sau sự cố máy bay chiến đấu Mỹ bị bắn hạ.";

        DES des = new DES();
        des.genKey(des.keySizes.getFirst());
        des.encryptFile("/home/lamdoanh2468/Desktop/1.pdf", "/home/lamdoanh2468/Desktop/2.pdf");
        des.decryptFile("/home/lamdoanh2468/Desktop/2.pdf", "/home/lamdoanh2468/Desktop/3.pdf");

    }

    @Override
    public SecretKey genKey(int keySize) throws NoSuchAlgorithmException {
        // TODO Auto-generated method stub
        validateKeySize(keySize);
        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(keySize);
        key = keyGenerator.generateKey();
        return key;
    }

    @Override
    public byte[] encrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.key, initVector);

        byte[] data = text.getBytes();
        return cipher.doFinal(data);

    }

    @Override
    public String decrypt(byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, this.key, initVector);
        byte[] data = cipher.doFinal(text);

        return new String(data, StandardCharsets.UTF_8);

    }
    // ==================================

    /*
     * BIS ----CIS-encrypt-read->>>>>>>> BOS
     *
     *
     *
     */

    @Override
    public String getAlgorithm() {
        return "DES";
    }

    @Override
    public void loadKey(SecretKey key) {
        // TODO Auto-generated method stub
        this.key = key;
    }

    @Override
    public List<Integer> getSupportedKeySizes() {
        return keySizes;
    }

    @Override
    public boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        // TODO Auto-generated method stub
        IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, this.key, initVector);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
        CipherInputStream cis = new CipherInputStream(bis, cipher);

        int count;
        byte[] readByte = new byte[1024];
        while ((count = cis.read(readByte)) != -1) {
            bos.write(readByte, 0, count);
        }

        cis.close();
        bos.flush();
        bos.close();

        return true;
    }

    @Override
    public boolean decryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, initVector);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
        CipherOutputStream cos = new CipherOutputStream(bos, cipher);

        int count = 0;
        byte[] readByte = new byte[1024];
        while ((count = bis.read(readByte)) != -1) {
            cos.write(readByte, 0, count);
        }

        bis.close();
        bos.flush();
        bos.close();

        return true;
    }
}
