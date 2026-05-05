package model.hash;

import java.util.Arrays;
import java.util.List;

public class MD5 extends AFileHash {

    public MD5() {
        super("MD5", "");
        this.supportedAlgorithms = List.of("MD5");
    }

    public MD5(String provider) {
        super("MD5", provider);
        this.supportedAlgorithms = List.of("MD5");
    }
}