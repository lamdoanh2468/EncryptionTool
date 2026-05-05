package model.cipher.file;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.List;

public class RC5 extends AFileSymCipher {

    static {
        // Add BouncyCastleProvider if not already added
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public RC5() {
        super("RC5", "CBC", "PKCS5Padding");
        keySizes = List.of(128);
    }


    @Override
    public String getAlgorithm() {
        return "RC5";
    }

    @Override
    public List<String> getSupportedModes() {
        return Arrays.asList("ECB", "CBC", "CFB", "OFB", "CTR");
    }

    @Override
    public List<String> getSupportedPaddings() {
        return Arrays.asList("PKCS5Padding", "NoPadding");
    }


    @Override
    public SecretKey genKey(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException {
        // TODO Auto-generated method stub
        validateKeySize(keySize);
        KeyGenerator keyGen = KeyGenerator.getInstance("RC5", "BC");
        keyGen.init(keySize);
        key = keyGen.generateKey();
        return key;
    }

    // encrypt / decrypt text
    @Override
    public byte[] encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {
        // TODO Auto-generated method stub
        Cipher cipher = Cipher.getInstance(getTransformation(), "BC");
        if (getTransformation().contains("/ECB/")) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
            cipher.init(Cipher.ENCRYPT_MODE, key, initVector);
        }
        byte[] cipherData = data.getBytes(StandardCharsets.UTF_8);
        return cipher.doFinal(cipherData);
    }

    @Override
    public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException {
        // TODO Auto-generated method stub
        Cipher cipher = Cipher.getInstance(getTransformation(), "BC");
        if (getTransformation().contains("/ECB/")) {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
            cipher.init(Cipher.DECRYPT_MODE, key, initVector);
        }
        byte[] decryptedData = cipher.doFinal(data);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    // encryptFileSymmetric / decryptFileSymmetric
    @Override
    public boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, NoSuchProviderException {
        Cipher cipher = Cipher.getInstance(getTransformation(), "BC");
        if (getTransformation().contains("/ECB/")) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
            cipher.init(Cipher.ENCRYPT_MODE, key, initVector);
        }

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
        CipherInputStream cis = new CipherInputStream(bis, cipher);

        int count;
        byte[] readBuffer = new byte[1024];
        while ((count = cis.read(readBuffer)) != -1) {
            bos.write(readBuffer, 0, count);
        }
//
        bis.close();
        bos.flush();
        bos.close();

        return true;
    }

    @Override
    public boolean decryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, NoSuchProviderException {
        // TODO Auto-generated method stub
        Cipher cipher = Cipher.getInstance(getTransformation(), "BC");
        if (getTransformation().contains("/ECB/")) {
            cipher.init(Cipher.DECRYPT_MODE, key);
        } else {
            IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
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


