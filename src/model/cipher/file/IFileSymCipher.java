package model.cipher.file;

import javax.crypto.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public interface IFileSymCipher extends IFileCipher {
    SecretKey genKey(int keySize) throws NoSuchAlgorithmException, NoSuchProviderException;

    void loadKey(SecretKey key);

    byte[] encrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException;

    String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException;

}
