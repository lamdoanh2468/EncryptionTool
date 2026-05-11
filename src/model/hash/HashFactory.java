package model.hash;

public final class HashFactory {

    private HashFactory() {} // Utility class

    public static AFileHash getInstance(String algo) {
        if (algo == null || algo.isBlank()) {
            return new SHA256();
        }

        return switch (algo) {
            // Legacy
            case "MD2" -> new MD2();
            case "MD5" -> new MD5();
            case "SHA-1", "SHA1" -> new SHA1();

            // SHA-2
            case "SHA-224" -> new SHA224();
            case "SHA-256" -> new SHA256();
            case "SHA-384" -> new SHA384();
            case "SHA-512" -> new SHA512();
            case "SHA-512/224", "SHA512/224", "SHA512224" -> new SHA_512_224();
            case "SHA-512/256", "SHA512/256", "SHA512256" -> new SHA_512_256();

            // SHA-3
            case "SHA3-224" -> new SHA3_224();
            case "SHA3-256" -> new SHA3_256();
            case "SHA3-384" -> new SHA3_384();
            case "SHA3-512" -> new SHA3_512();

            // BLAKE
            case "BLAKE2b" -> new BLAKE2b();
            case "BLAKE3" -> new BLAKE3();

            default -> throw new IllegalArgumentException("Hàm băm không được hỗ trợ: " + algo);
        };
    }
}