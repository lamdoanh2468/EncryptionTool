package model.hash;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class BLAKE3 extends AFileHash {
    static {
        // Add BouncyCastleProvider if not already added
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    public BLAKE3() {
        super("BLAKE3-256", "");
    }

    public BLAKE3(String provider) {
        super("BLAKE3-256", provider);
    }
}
