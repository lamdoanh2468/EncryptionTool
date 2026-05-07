package model.hash;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;

public abstract class AFileHash implements IFileHash {

    protected String algorithm;
    protected String provider;
    protected List<String> supportedAlgorithms;

    public AFileHash(String algorithm, String provider) {
        this.algorithm = algorithm;
        this.provider = provider;
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
    public byte[] hash(String data) throws NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest digest = getMessageDigest();
        return digest.digest(data.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public byte[] hashFile(String src) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
        MessageDigest digest = getMessageDigest();
        try (InputStream fis = new FileInputStream(src);
             DigestInputStream dis = new DigestInputStream(fis, digest)) {
            byte[] buffer = new byte[8192];
            while (dis.read(buffer) != -1) {}
            return digest.digest();
        }
    }

    @Override
    public boolean verify(String data, byte[] expectedHash) throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] actual = hash(data);
        return MessageDigest.isEqual(actual, expectedHash);
    }

    @Override
    public boolean verifyFile(String src, byte[] expectedHash) throws IOException, NoSuchAlgorithmException, NoSuchProviderException {
        byte[] actual = hashFile(src);
        return MessageDigest.isEqual(actual, expectedHash);
    }

    public String hashToHex(String data) throws NoSuchAlgorithmException, NoSuchProviderException {
        byte[] hashBytes = hash(data);
        StringBuilder hex = new StringBuilder();
        for (byte b : hashBytes) {
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }
}