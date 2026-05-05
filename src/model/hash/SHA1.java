package model.hash;

import java.util.List;

public class SHA1 extends AFileHash {
    public SHA1() {
        super("SHA-1", "");
        this.supportedAlgorithms = List.of("SHA-1");
    }

    public SHA1(String provider) {
        super("SHA-1", provider);
        this.supportedAlgorithms = List.of("SHA-1");
    }
}