package model.file;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.*;

public abstract class ACipher implements IFileCipher {
	SecretKey key;

	public abstract SecretKey genKey() throws NoSuchAlgorithmException;

	public  void loadKey(SecretKey key){
		if (key == null) {
			throw new IllegalArgumentException("Key không được bỏ trống");
		}
		this.key = key;
	}
	public String encryptBase64(String text) throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
		byte[] encrypted = encrypt(text);
		return Base64.getEncoder().encodeToString(encrypted);
	}


	public abstract byte[] encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException;

	public abstract String decrypt(byte[] data)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException;

	public abstract boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException;

	public abstract boolean decryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException;

}
