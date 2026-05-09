package model.hash;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.List;

public interface IFileHash {

    byte[] hashText(String data) throws NoSuchAlgorithmException, NoSuchProviderException;

    byte[] hashFile(String src) throws IOException, NoSuchAlgorithmException, NoSuchProviderException;

    String getAlgorithm();

    List<String> getSupportedAlgorithms();
}