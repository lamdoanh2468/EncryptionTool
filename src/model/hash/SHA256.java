package model.hash;

import java.util.List;

public class SHA256 extends AFileHash {

    public SHA256() {
        super("SHA-256", "");
        this.supportedAlgorithms = List.of("SHA-256");
    }

    public SHA256(String provider) {
        super("SHA-256", provider);
        this.supportedAlgorithms = List.of("SHA-256");
    }
}
