package model.file;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class DES extends ACipher {

	@Override
	public SecretKey genKey() throws NoSuchAlgorithmException {
		// TODO Auto-generated method stub
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
		keyGenerator.init(56);
		key = keyGenerator.generateKey();
		return key;
	}

	@Override
	public byte[] encrypt(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		// TODO Auto-generated method stub
		IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, this.key, initVector);

		byte[] data = text.getBytes();
		return cipher.doFinal(data);

	}

	@Override
	public String decrypt(byte[] text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
		// TODO Auto-generated method stub
		IvParameterSpec initVector = new IvParameterSpec(new byte[8]);
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, this.key,initVector);
		byte[] data = cipher.doFinal(text);

		return new String(data, StandardCharsets.UTF_8);

	}

	@Override
	public void loadKey(SecretKey key) {
		// TODO Auto-generated method stub
		this.key = key;
	}

	/*
	 * BIS ----CIS-encrypt-read->>>>>>>> BOS
	 *
	 *
	 *
	 */

	@Override
	public boolean encryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException {
		// TODO Auto-generated method stub

		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, this.key);

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
		CipherInputStream cis = new CipherInputStream(bis, cipher);

		int count;
		byte[] readByte = new byte[1024];
		while ((count = cis.read(readByte)) != -1) {
			bos.write(readByte, 0, count);
		}

		// ===== CHECK IF HAS NO BYTES LEFT ====
		readByte = cipher.doFinal();
		if (readByte != null) {
			bos.write(readByte);
		}
		// ==================================
		cis.close();
		bos.flush();
		bos.close();

		return true;
	}

	@Override
	public boolean decryptFile(String src, String des) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IOException, IllegalBlockSizeException, BadPaddingException {

		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);

		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(src));
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(des));
		CipherOutputStream cos = new CipherOutputStream(bos, cipher);

		int count = 0;
		byte[] readByte = new byte[1024];
		while ((count = bis.read(readByte)) != -1) {
			cos.write(readByte, 0, count);
		}

		// ===== CHECK IF HAS NO BYTES LEFT ====
		readByte = cipher.doFinal();
		if (readByte != null) {
			cos.write(readByte);
		}

		bis.close();
		bos.flush();
		bos.close();

		// ==================================

		return true;
	}

	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			BadPaddingException, IllegalBlockSizeException, IOException, InvalidAlgorithmParameterException {
		String data = "Hạ nghị sĩ Đảng Dân chủ Seth Moulton (bang Massachusetts), thành viên Ủy ban Quân dịch Hạ viện, đã công khai chỉ trích cách tiếp cận của Tổng thống Donald Trump đối với cuộc xung đột tại Iran sau sự cố máy bay chiến đấu Mỹ bị bắn hạ.";

		DES des = new DES();
		des.genKey();

		byte[] encryptText = des.encrypt(data);

		System.out.println(Base64.getEncoder().encodeToString(encryptText));
		String decryptText = des.decrypt(encryptText);
		System.out.println(decryptText);

//		String src = "C:\\Users\\Admin\\Desktop\\cars.csv";
//		String encrypted = "C:\\Users\\Admin\\Desktop\\cars1.csv";
//		String decrypted = "C:\\Users\\Admin\\Desktop\\cars2.csv";
//
//		des.encryptFile(src, encrypted);
//		des.decryptFile(encrypted, decrypted);

	}
}
