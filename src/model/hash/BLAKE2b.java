package model.hash;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;

public class BLAKE2b extends AFileHash {
    static {
        // Add BouncyCastleProvider if not already added
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    public BLAKE2b() {
        super("BLAKE2b-512", "");
    }

    public BLAKE2b(String provider) {
        super("BLAKE2b-512", provider);
    }
}