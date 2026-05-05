package model.hash;

import java.util.List;

public class SHA512 extends AFileHash {

    public SHA512() {
        super("SHA-512", "");
        this.supportedAlgorithms = List.of("SHA-512");
    }

    public SHA512(String provider) {
        super("SHA-512", provider);
        this.supportedAlgorithms = List.of("SHA-512");
    }
}
