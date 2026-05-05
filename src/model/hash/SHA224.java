package model.hash;

import java.util.List;

public class SHA224 extends AFileHash {

    public SHA224() {
        super("SHA-224", "");
        this.supportedAlgorithms = List.of("SHA-224");
    }

    public SHA224(String provider) {
        super("SHA-224", provider);
        this.supportedAlgorithms = List.of("SHA-224");
    }
}
