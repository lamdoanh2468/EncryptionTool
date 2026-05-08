package model.cipher.text;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAText extends ATextCipher<KeyPair> {

    private static final String ALGORITHM = "RSA";
    private static final String TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private KeyPair keyPair;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getPublicKeyText() {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }

    public String getPrivateKeyText() {
        byte[] privateKeyBytes = privateKey.getEncoded();
        return Base64.getEncoder().encodeToString(privateKeyBytes);
    }

    @Override
    public KeyPair genKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyGen.initialize(2048);
            keyPair = keyGen.generateKeyPair();
            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();
            return keyPair;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Không hỗ trợ thuật toán RSA", e);
        }
    }

    @Override
    public void loadKey(KeyPair key) {
        this.keyPair = key;
        this.publicKey = key.getPublic();
        this.privateKey = key.getPrivate();
    }

    @Override
    public String getKey() {
        if (keyPair == null) return "";
        return Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
    }

    @Override
    public KeyPair parseKey(String keyText) {

        return keyPair;
    }

    @Override
    public String encrypt(String plainText, KeyPair key) {
        try {

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key.getPublic());
            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi mã hóa RSA: " + e.getMessage(), e);
        }
    }

    @Override
    public String decrypt(String cipherText, KeyPair key) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, key.getPrivate());
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi giải mã RSA: " + e.getMessage(), e);
        }
    }

    public void loadPublicKeyFromText(String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey.trim());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            this.publicKey = keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi load Public Key: " + e.getMessage(), e);
        }
    }

    public void loadPrivateKeyFromText(String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey.trim());
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            this.privateKey = keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi load Private Key: " + e.getMessage(), e);
        }
    }
}
