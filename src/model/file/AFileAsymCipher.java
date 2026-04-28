package model.file;

import javax.crypto.SecretKey;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.util.Base64;
import java.util.List;

public abstract class AFileAsymCipher implements IFileCipher {


    protected static final String DEFAULT_PUBLIC_KEY_PATH = "public.key";
    protected static final String DEFAULT_PRIVATE_KEY_PATH = "private.key";

    // Asymmetric Key
    protected static PublicKey publicKey;
    protected static PrivateKey privateKey;
    protected KeyPair keyPair;
    protected String asymAlgorithm;
    protected String asymMode;
    protected String asymPadding;
    protected List<Integer> keySizes;

    // Symmetric Key
    protected  SecretKey symKey;
    protected AFileSymCipher symCipher;

    public AFileAsymCipher(String asymAlgorithm, String defaultMode, String defaultPadding,AFileSymCipher symCipher) {
        this.asymAlgorithm = asymAlgorithm;
        this.asymMode = defaultMode;
        this.asymPadding = defaultPadding;
        this.symCipher = symCipher;
    }
    public AFileAsymCipher(String asymAlgorithm, String defaultMode, String defaultPadding) {
        this(asymAlgorithm, defaultMode, defaultPadding, null);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public  PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getAsymAlgorithm() {
        return asymAlgorithm;
    }

    public List<Integer> getKeySizes() {
        return keySizes;
    }

    public abstract List<String> getSupportedPaddings();

    public void setASymPadding(String asymPadding) {
        if (getSupportedPaddings().contains(asymPadding)) {
            this.asymPadding = asymPadding;
        } else {
            throw new IllegalArgumentException("Padding " + asymPadding + " không được hỗ trợ cho " + getAsymAlgorithm());
        }
    }

    public String getTransformation() {
        return asymAlgorithm + "/" + asymMode + "/" + asymPadding;
    }

    public void genKeyPair(String algorithm,int keySize, String dest) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algorithm);
        keyGenerator.initialize(keySize);

        keyPair = keyGenerator.genKeyPair();
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        exportPublicKey(publicKey, dest + File.separator + DEFAULT_PUBLIC_KEY_PATH);
        exportPrivateKey(privateKey, dest + File.separator + DEFAULT_PRIVATE_KEY_PATH);
    }

    public String exportPublicKey(PublicKey publicKey, String dest) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
        byte[] publicKeyEncoded = publicKey.getEncoded();
        String pubKeyText = Base64.getEncoder().encodeToString(publicKeyEncoded);
        writer.write(pubKeyText);
        writer.flush();
        writer.close();
        System.out.println("Export public key successful");

        return pubKeyText;
    }

    public String exportPrivateKey(PrivateKey privateKey, String dest) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(dest));
        byte[] privateKeyEncoded = privateKey.getEncoded();
        String privateKeyText = Base64.getEncoder().encodeToString(privateKeyEncoded);
        writer.write(privateKeyText);
        writer.flush();
        writer.close();
        System.out.println("Export private key successful");
        return privateKeyText;
    }

    public AFileSymCipher getSymCipher() {
        return symCipher;
    }

    public void setSymCipher(AFileSymCipher symCipher) {
        this.symCipher = symCipher;
    }
}
