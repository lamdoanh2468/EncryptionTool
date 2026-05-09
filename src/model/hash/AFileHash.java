package model.hash;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

public abstract class AFileHash implements IFileHash {

    protected String algorithm;
    protected String provider;
    protected List<String> supportedAlgorithms;

    public AFileHash(String algorithm, String provider) {
        this.algorithm = algorithm;
        this.provider = provider;
        this.supportedAlgorithms = List.of(algorithm);
    }

    @Override
    public String getAlgorithm() {
        return algorithm;
    }

    @Override
    public List<String> getSupportedAlgorithms() {
        return supportedAlgorithms;
    }

    protected MessageDigest getMessageDigest() throws NoSuchAlgorithmException, NoSuchProviderException {
        if (provider != null && !provider.isEmpty()) {
            return MessageDigest.getInstance(algorithm, provider);
        } else {
            return MessageDigest.getInstance(algorithm);
        }
    }

    @Override
    public byte[] hashText(String data) throws NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest digest = getMessageDigest();
        byte [] hashBytes = data.getBytes(StandardCharsets.UTF_8);
        return digest.digest(hashBytes);
    }

    @Override
    public byte[] hashFile(String src) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest digest = getMessageDigest();
        InputStream fis = new FileInputStream(src);
        DigestInputStream dis = new DigestInputStream(fis, digest);

        byte[] buffer = new byte[8192];
        int read;
        do {
            read = dis.read(buffer);
        } while (read != -1);

        dis.close();
        return digest.digest();

    }
    public String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte element : bytes) {
            String hexFormat = String.format("%02x", element);
            sb.append(hexFormat);
        }
        return sb.toString();
    }
}