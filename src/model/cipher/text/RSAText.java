package model.cipher.text;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RSAText extends ATextCipher<KeyPair> {

    private static final String RSA_TYPE = "RSA";
    private static final String RSA_TRANSFORM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";

    private KeyPair currentKeys;

    private PublicKey pubKey;
    private PrivateKey priKey;

    public KeyPair getKeyPair() {
        return currentKeys;
    }

    public PublicKey getPublicKey() {
        return pubKey;
    }

    public PrivateKey getPrivateKey() {
        return priKey;
    }

    public String getPublicKeyText() {

        if (pubKey == null) {
            return "";
        }

        byte[] bytes = pubKey.getEncoded();

        return Base64.getEncoder().encodeToString(bytes);
    }

    public String getPrivateKeyText() {

        if (priKey == null) {
            return "";
        }

        byte[] bytes = priKey.getEncoded();

        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public KeyPair genKey() {

        try {

            KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_TYPE);

            // 2048 is enough for normal use
            generator.initialize(2048);

            currentKeys = generator.generateKeyPair();

            pubKey = currentKeys.getPublic();
            priKey = currentKeys.getPrivate();

            return currentKeys;

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("Không hỗ trợ thuật toán RSA", e);
        }
    }

    @Override
    public void loadKey(KeyPair key) {

        if (key == null) {
            return;
        }

        this.currentKeys = key;
        this.pubKey = key.getPublic();
        this.priKey = key.getPrivate();
    }

    @Override
    public String getKey() {

        if (currentKeys == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(currentKeys.getPrivate().getEncoded());
    }

    @Override
    public KeyPair parseKey(String keyText) {

        return currentKeys;
    }

    @Override
    public String encrypt(String plainText, KeyPair key) {

        try {

            Cipher rsaCipher = Cipher.getInstance(RSA_TRANSFORM);

            rsaCipher.init(Cipher.ENCRYPT_MODE, key.getPublic());

            byte[] encryptedBytes = rsaCipher.doFinal(plainText.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);

        } catch (Exception ex) {

            throw new RuntimeException("Lỗi mã hóa RSA: " + ex.getMessage(), ex);
        }
    }

    @Override
    public String decrypt(String cipherText, KeyPair key) {

        try {

            Cipher rsaCipher = Cipher.getInstance(RSA_TRANSFORM);

            rsaCipher.init(Cipher.DECRYPT_MODE, key.getPrivate());

            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);

            byte[] decryptedData = rsaCipher.doFinal(decodedBytes);

            return new String(decryptedData);

        } catch (Exception e) {

            throw new RuntimeException("Lỗi giải mã RSA: " + e.getMessage(), e);
        }
    }

    public void loadPublicKeyFromText(String base64PublicKey) {

        try {

            String cleanedKey = base64PublicKey.trim();

            byte[] rawBytes = Base64.getDecoder().decode(cleanedKey);

            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(rawBytes);

            KeyFactory factory = KeyFactory.getInstance(RSA_TYPE);

            this.pubKey = factory.generatePublic(publicSpec);

        } catch (Exception ex) {

            throw new RuntimeException("Lỗi load Public Key: " + ex.getMessage(), ex);
        }
    }

    public void loadPrivateKeyFromText(String base64PrivateKey) {

        try {

            byte[] rawBytes = Base64.getDecoder().decode(base64PrivateKey.trim());

            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(rawBytes);

            KeyFactory factory = KeyFactory.getInstance(RSA_TYPE);

            this.priKey = factory.generatePrivate(privateSpec);

        } catch (Exception e) {

            throw new RuntimeException("Lỗi load Private Key: " + e.getMessage(), e);
        }
    }
}
