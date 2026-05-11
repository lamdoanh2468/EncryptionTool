package model.cipher.text;

public class TextCipherFactory {
    public static ATextCipher<?> getCipher(String algoName) {
        if (algoName == null) {
            return null;
        }
        switch (algoName) {
            case "Caesar":
                return new Caesar();
            case "Affine":
                return new Affine();
            case "Vigenere":
                return new Vigenere();
            case "Hill":
                return new Hill();
            case "Thay thế":
                return new Substitution();
            case "Hoán vị":
                return new Permutation();
            default:
                return null;
        }
    }
}
