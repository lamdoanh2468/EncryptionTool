package model.cipher.file;

public class FileCipherFactory {
    public static AFileSymCipher getSymmetricCipher(String algoName) {
        if (algoName == null) return null;

        return switch (algoName) {
            case "AES" -> new AES();
            case "Blowfish" -> new Blowfish();
            case "Twofish" -> new Twofish();
            case "Camellia" -> new Camellia();
            case "DES" -> new DES();
            case "DESede" -> new DESede();
            case "RC5" -> new RC5();
            default -> throw new IllegalArgumentException("Thuật toán không hỗ trợ: " + algoName);
        };
    }

    public static AFileAsymCipher getAsymmetricCipher(String algoName) {
        if (algoName == null) return null;
        return switch (algoName) {
            case "RSA" -> new RSAFile();
            default -> throw new IllegalArgumentException("Thuật toán không hỗ trợ: " + algoName);
        };
    }
}
