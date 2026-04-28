package model.text;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.security.*;
import javax.crypto.*;

public class RSA {

	KeyPair keyPair;
	PublicKey publicKey;
	PrivateKey privateKey;
	
	/**
	 * @encrypt:
	 * 
	 * 		  toByteArray()			doFinal(data)			encodeBase64()
	 * Input =============> Cipher =============> Output ------------------> CipherText
	 * 
	 * 
	 * @decrypt:
	 * 
	 *  	 decodeBase64()			 doFinal(encodeData)			new String()
	 * Input =============> Cipher ======================> Output ------------------> PlainText
	 * 
	 * 
	 * @Similarity :
	 * 
	 * + Output returned is byte array . Must be converted into String type (encoderToString, new String())
	 * 
	 * @Difference :
	 * 
	 * - Encryption use byte array from data
	 * 
	 * - Decryption use byte array from decoding encrypted data 
	 * 
	 **/
	
	public String encryptBase64(String data) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		return Base64.getEncoder().encodeToString(encrypt(data));
	}

	private byte[] encrypt(String data) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
		// TODO Auto-generated method stub
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte[] input = data.getBytes(StandardCharsets.UTF_8);
		byte[] result = cipher.doFinal(input);
		return result;
	}

	public String decrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] input = Base64.getDecoder().decode(data);
		byte[] result = cipher.doFinal(input);

		return new String(result, StandardCharsets.UTF_8);

	}

	public void genKey() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
		keyGenerator.initialize(2048);

		keyPair = keyGenerator.generateKeyPair();
		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();

	}

	public static void main(String[] args) throws Exception {
		String text = "Tổng thống Mỹ Donald Trump tiếp tục gia tăng "
				+ "sức ép lên Iran trước thềm các cuộc đàm phán diễn ra hôm nay. "
				+ "Ông cho rằng Tehran “không có lá bài nào” ngoài việc “gây sức ép ngắn hạn”";
		RSA rsa = new RSA();
		rsa.genKey();
		String encryptText = rsa.encryptBase64(text);
		String decryptText = rsa.decrypt(encryptText);

		System.out.println(encryptText + "\n" + decryptText);

	}

}
