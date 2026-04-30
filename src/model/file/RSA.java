package model.file;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

public class RSA extends AFileAsymCipher {

    public RSA() {
        super("RSA", "ECB", "PKCS1Padding");
        symCipher = new AES();
        keySizes = Arrays.asList(1024, 2048);
    }
    public static String extractAlgorithm(String transformation) {
        return transformation.split("/")[0];
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IOException, IllegalBlockSizeException, BadPaddingException {

        RSA fileRSA = new RSA();
        fileRSA.genKeyPair("RSA", 2048, "C:\\Users\\lamdo\\Desktop");
        fileRSA.genSymKey("AES", 128);

        fileRSA.encryptFile("C:\\Users\\lamdo\\Desktop\\1.pdf", "C:\\Users\\lamdo\\Desktop\\2.pdf");

        fileRSA.decryptFile("C:\\Users\\lamdo\\Desktop\\2.pdf", "C:\\Users\\lamdo\\Desktop\\3.pdf");
    }

    public void genSymKey(String algorithm, int keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
        keyGenerator.init(keySize);
        symKey = keyGenerator.generateKey();
    }

    /**
     * DOS - HEADER -BOS
     * COS - DATA	 -BOS
     **/


    @Override
    public boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        Cipher rsaCipher = Cipher.getInstance(getTransformation());
        rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = rsaCipher.doFinal(symKey.getEncoded());

        String symAlgorithm = this.symCipher.getTransformation();
        Cipher symCipher = Cipher.getInstance(symAlgorithm);
        byte[] ivBytes;
        if (symAlgorithm.contains("/ECB/")) {
            symCipher.init(Cipher.ENCRYPT_MODE, symKey);
            ivBytes = new byte[0];
        } else {
            SecureRandom secureRandom = new SecureRandom();
            int blockSize = symCipher.getBlockSize();
            if (blockSize == 0) blockSize = 16;
            ivBytes = new byte[blockSize];
            secureRandom.nextBytes(ivBytes);
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            symCipher.init(Cipher.ENCRYPT_MODE, symKey, iv);
        }

        FileInputStream fis = new FileInputStream(src);

        FileOutputStream fos = new FileOutputStream(des);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        DataOutputStream dos = new DataOutputStream(bos); // HEADER

        // Write header
        dos.writeUTF(symAlgorithm);
        dos.writeInt(symKey.getEncoded().length * 8);
        dos.writeInt(ivBytes.length);
        dos.write(ivBytes);
        if (symKey != null) {
            dos.writeInt(encryptedKey.length);
            dos.write(encryptedKey);
        }
        dos.flush();

        CipherOutputStream cos = new CipherOutputStream(bos, symCipher); // DATA ENCRYPTED
        // Write data
        int count;
        byte[] readBytes = new byte[4096];
        while ((count = fis.read(readBytes)) != -1) {
            cos.write(readBytes, 0, count);
        }

        fis.close();

        cos.flush();
        cos.close();

        return true;
    }


    @Override
    public boolean decryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

        FileOutputStream fos = new FileOutputStream(des);
        FileInputStream fis = new FileInputStream(src);
        BufferedInputStream bis = new BufferedInputStream(fis);
        DataInputStream dis = new DataInputStream(bis);

        // READ HEADER
        String symAlgorithm = dis.readUTF();
        int keySize = dis.readInt();

        int ivLength = dis.readInt();
        byte[] iv = new byte[ivLength];
        dis.readFully(iv);

        int keyLength = dis.readInt();
        byte[] key = new byte[keyLength];
        dis.readFully(key);

        Cipher rsaCipher = Cipher.getInstance(getTransformation());
        rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);

        byte[] keyBytes = rsaCipher.doFinal(key);
        SecretKey decKey = new SecretKeySpec(keyBytes, extractAlgorithm(symAlgorithm));

        //READ DATA
        Cipher symCipher = Cipher.getInstance(symAlgorithm);
        if (symAlgorithm.contains("/ECB/")) {
            symCipher.init(Cipher.DECRYPT_MODE, decKey);
        } else {
            IvParameterSpec initVector = new IvParameterSpec(iv);
            symCipher.init(Cipher.DECRYPT_MODE, decKey, initVector);
        }

        CipherInputStream cis = new CipherInputStream(bis, symCipher);
        int count;
        byte[] readBytes = new byte[4096];

        while ((count = cis.read(readBytes)) != -1) {
            fos.write(readBytes, 0, count);
        }

        fis.close();

        fos.flush();
        fos.close();

        return true;
    }

    @Override
    public List<String> getSupportedPaddings() {
        return List.of("PKCS1Padding",
                "OAEPWithSHA-1AndMGF1Padding",
                "OAEPWithSHA-256AndMGF1Padding",
                "OAEPWithSHA-512AndMGF1Padding");
    }

}
