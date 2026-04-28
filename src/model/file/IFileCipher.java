package model.file;

import javax.crypto.*;
import java.io.*;
import java.security.*;

public interface IFileCipher {
    boolean encryptFile(String src, String dest) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IOException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException;

    boolean decryptFile(String src, String dest) throws
            NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException;
}
