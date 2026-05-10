package model.cipher.text;

public class Vigenere extends ATextCipher<String> {
    private static final int VN_SIZE = VN_ALPHABET_LOWER.length();

    private String savedKey;

    @Override
    public String genKey() {

        StringBuilder randomKey = new StringBuilder();

        for (int i = 0; i < 6; i++) {

            char generated = (char) ('A' + (int) (Math.random() * 26));
            randomKey.append(generated);
        }

        savedKey = randomKey.toString();

        return savedKey;
    }

    @Override
    public void loadKey(String key) {

        this.savedKey = key;
    }

    @Override
    public String getKey() {

        return savedKey;
    }

    @Override
    public String parseKey(String keyString) {
        return this.savedKey;
    }

    // Encrypt
    @Override
    public String encrypt(String text, String key) {

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        // remove weird chars from key
        String cleanedKey = key.replaceAll("[^A-Za-zÀ-ỹ]", "").toUpperCase();

        if (cleanedKey.isEmpty()) {

            throw new IllegalArgumentException("Key must contain at least one letter");
        }

        boolean vietnameseDetected = false;

        for (char currentChar : text.toCharArray()) {

            boolean englishOnly = (currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z');

            if (Character.isLetter(currentChar) && !englishOnly) {

                vietnameseDetected = true;
                break;
            }
        }

        if (vietnameseDetected) {

            return encryptVN(text, cleanedKey);

        } else {

            return encryptEng(text, cleanedKey);
        }
    }

    private String encryptEng(String text, String key) {

        StringBuilder encrypted = new StringBuilder();

        int keyPointer = 0;
        int keyLength = key.length();

        for (char currentChar : text.toCharArray()) {
            boolean validLetter = (currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z');

            if (validLetter) {

                boolean upperCase = Character.isUpperCase(currentChar);
                char base = upperCase ? 'A' : 'a';
                char keyChar = key.charAt(keyPointer % keyLength);
                int shift = keyChar - 'A';
                int oldIndex = currentChar - base;
                int encryptedIndex = (oldIndex + shift) % 26;

                encrypted.append((char) (encryptedIndex + base));

                keyPointer++;

            } else {
                encrypted.append(currentChar);
            }
        }

        return encrypted.toString();
    }

    private String encryptVN(String text, String key) {

        StringBuilder encrypted = new StringBuilder();

        int keyPos = 0;
        int keyLen = key.length();

        for (char currentChar : text.toCharArray()) {

            int lowerIndex = VN_ALPHABET_LOWER.indexOf(currentChar);
            int upperIndex = VN_ALPHABET_UPPER.indexOf(currentChar);

            if (lowerIndex != -1) {
                char keyChar = key.charAt(keyPos % keyLen);
                int shift = VN_ALPHABET_LOWER.indexOf(Character.toLowerCase(keyChar));

                if (shift == -1) {
                    shift = 0;
                }

                int encryptedIndex = (lowerIndex + shift) % VN_SIZE;
                encrypted.append(VN_ALPHABET_LOWER.charAt(encryptedIndex));

                keyPos++;

            } else if (upperIndex != -1) {

                char keyChar = key.charAt(keyPos % keyLen);
                int shift = VN_ALPHABET_UPPER.indexOf(keyChar);

                if (shift == -1) {
                    shift = 0;
                }

                int encryptedIndex = (upperIndex + shift) % VN_SIZE;
                encrypted.append(VN_ALPHABET_UPPER.charAt(encryptedIndex));
                keyPos++;

            } else {
                encrypted.append(currentChar);
            }
        }

        return encrypted.toString();
    }

    // Encrypt
    @Override
    public String decrypt(String text, String key) {

        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        String cleanedKey = key.replaceAll("[^A-Za-zÀ-ỹ]", "").toUpperCase();

        if (cleanedKey.isEmpty()) {
            throw new IllegalArgumentException("Key must contain at least one letter");
        }

        boolean hasVietnamese = false;

        for (char currentChar : text.toCharArray()) {
            boolean englishOnly = (currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z');

            if (Character.isLetter(currentChar) && !englishOnly) {
                hasVietnamese = true;
                break;
            }
        }

        if (hasVietnamese) {
            return decryptVN(text, cleanedKey);

        } else {
            return decryptEng(text, cleanedKey);
        }
    }

    private String decryptEng(String text, String key) {

        StringBuilder decrypted = new StringBuilder();
        int keyIndex = 0;
        int keyLength = key.length();

        for (char currentChar : text.toCharArray()) {

            boolean isAlphabet = (currentChar >= 'A' && currentChar <= 'Z') || (currentChar >= 'a' && currentChar <= 'z');

            if (isAlphabet) {

                boolean upper = Character.isUpperCase(currentChar);
                char base = upper ? 'A' : 'a';
                char keyChar = key.charAt(keyIndex % keyLength);
                int shift = keyChar - 'A';
                int currentIndex = currentChar - base;
                int decryptedIndex = (currentIndex - shift + 26) % 26;

                decrypted.append((char) (decryptedIndex + base));

                keyIndex++;

            } else {
                decrypted.append(currentChar);
            }
        }

        return decrypted.toString();
    }

    private String decryptVN(String text, String key) {

        StringBuilder decrypted = new StringBuilder();
        int keyPosition = 0;
        int keyLength = key.length();

        for (char currentChar : text.toCharArray()) {

            int lowerIndex = VN_ALPHABET_LOWER.indexOf(currentChar);
            int upperIndex = VN_ALPHABET_UPPER.indexOf(currentChar);

            if (lowerIndex != -1) {

                char keyChar = key.charAt(keyPosition % keyLength);
                int shift = VN_ALPHABET_LOWER.indexOf(Character.toLowerCase(keyChar));

                if (shift == -1) {
                    shift = 0;
                }

                int decryptedIndex = (lowerIndex - shift + VN_SIZE) % VN_SIZE;
                decrypted.append(VN_ALPHABET_LOWER.charAt(decryptedIndex));
                keyPosition++;

            } else if (upperIndex != -1) {

                char keyChar = key.charAt(keyPosition % keyLength);
                int shift = VN_ALPHABET_UPPER.indexOf(keyChar);

                if (shift == -1) {
                    shift = 0;
                }

                int decryptedIndex = (upperIndex - shift + VN_SIZE) % VN_SIZE;

                decrypted.append(VN_ALPHABET_UPPER.charAt(decryptedIndex));
                keyPosition++;

            } else {
                decrypted.append(currentChar);
            }
        }
        return decrypted.toString();
    }

}