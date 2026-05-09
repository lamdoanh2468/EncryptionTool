package model.hash;

public class SHA1 extends AFileHash {
    public SHA1() {
        super("SHA-1", "");
    }

    public SHA1(String provider) {
        super("SHA-1", provider);
    }
}