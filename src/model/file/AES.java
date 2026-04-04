package model.file;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class AES extends ACipher {

	@Override
	public SecretKey genKey() throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadKey(SecretKey key) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encryptBase64(String text) {
		return "";
	}

	@Override
	public String decrypt(byte[] data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean decryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
