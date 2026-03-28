package model;

import javax.crypto.*;

public interface IFileCipher {
    SecretKey genKey();

    void loadKey(SecretKey key);

    byte[] encrypt(String text);

    String encryptBase64(String text);

    String decrypt(byte[] data);

    boolean encryptFile(String src, String dest);

    boolean decryptFile(String src, String dest);
}
