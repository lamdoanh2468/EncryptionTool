package model.hash;

import java.util.List;

public class SHA384 extends AFileHash {

    public SHA384() {
        super("SHA-384", "");
        this.supportedAlgorithms = List.of("SHA-384");
    }

    public SHA384(String provider) {
        super("SHA-384", provider);
        this.supportedAlgorithms = List.of("SHA-384");
    }
}
