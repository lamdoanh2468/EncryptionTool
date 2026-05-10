package model.cipher.text;

import java.util.Random;

public class Caesar extends ATextCipher<Integer> {
    private static final int VN_ALPHABET_SIZE = VN_ALPHABET_LOWER.length();

    @Override
    public Integer genKey() {

        currentKey = new Random().nextInt(25) + 1;

        return currentKey;
    }

    @Override
    public String getKey() {

        return String.valueOf(currentKey);
    }

    @Override
    public Integer parseKey(String keyString) {
        return Integer.parseInt(keyString);
    }

    // Encrypt
    @Override
    public String encrypt(String text, Integer key) {

        if (text == null || key == null) {
            text = "";
        }

        boolean containsVietnamese = false;

        // detect Vietnamese chars
        for (char c : text.toCharArray()) {
            boolean englishChar = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');

            if (Character.isLetter(c) && !englishChar) {
                containsVietnamese = true;
                break;
            }
        }

        if (containsVietnamese) {
            return encryptVN(text, key);
        } else {
            return encryptEng(text, key);
        }
    }

    public String encryptVN(String text, Integer key) {

        if (text == null) {
            text = "";
        }

        StringBuilder encrypted = new StringBuilder();

        for (char currentChar : text.toCharArray()) {

            int lowerIndex = VN_ALPHABET_LOWER.indexOf(currentChar);
            int upperIndex = VN_ALPHABET_UPPER.indexOf(currentChar);

            if (lowerIndex != -1) {
                // lowercase Vietnamese
                int shiftedIndex = (lowerIndex + key) % VN_ALPHABET_SIZE;
                encrypted.append(VN_ALPHABET_LOWER.charAt(shiftedIndex));

            } else if (upperIndex != -1) {
                // uppercase Vietnamese
                int shiftedIndex = (upperIndex + key) % VN_ALPHABET_SIZE;
                encrypted.append(VN_ALPHABET_UPPER.charAt(shiftedIndex));

            } else {
                encrypted.append(currentChar);
            }
        }

        return encrypted.toString();
    }

    public String encryptEng(String text, Integer key) {

        if (text == null) {
            text = "";
        }

        // normalize negative keys
        int normalizedKey = ((key % 26) + 26) % 26;
        StringBuilder encrypted = new StringBuilder();
        char[] separatedChars = text.toCharArray();

        for (char currentChar : separatedChars) {
            if (Character.isLetter(currentChar)) {
                boolean upperCase = Character.isUpperCase(currentChar);
                char baseChar = upperCase ? 'A' : 'a';

                // convert A-Z => 0-25
                int oldIndex = currentChar - baseChar;
                int encryptedIndex = (oldIndex + normalizedKey) % 26;
                encrypted.append((char) (encryptedIndex + baseChar));

            } else {
                encrypted.append(currentChar);
            }
        }

        return encrypted.toString();
    }

    // Decrypt
    @Override
    public String decrypt(String text, Integer key) {

        if (text == null || key == null) {
            text = "";
        }

        boolean containsVietnamese = false;

        for (char charElement : text.toCharArray()) {
            boolean englishOnly = (charElement >= 'A' && charElement <= 'Z') || (charElement >= 'a' && charElement <= 'z');

            if (Character.isLetter(charElement) && !englishOnly) {
                containsVietnamese = true;
                break;
            }
        }

        if (containsVietnamese) {
            return decryptVN(text, key);

        } else {
            return decryptEng(text, key);
        }
    }

    public String decryptEng(String text, Integer key) {

        if (text == null) {
            text = "";
        }

        int normalizedKey = ((key % 26) + 26) % 26;
        StringBuilder decrypted = new StringBuilder();
        char[] splitText = text.toCharArray();

        for (char currentChar : splitText) {
            // only decrypt alphabet chars
            if (Character.isLetter(currentChar)) {

                boolean isUpper = Character.isUpperCase(currentChar);
                char base = isUpper ? 'A' : 'a';
                int currentIndex = currentChar - base;
                int plainIndex = (currentIndex - normalizedKey + 26) % 26;
                decrypted.append((char) (plainIndex + base));

            } else {
                decrypted.append(currentChar);
            }
        }

        return decrypted.toString();
    }

    public String decryptVN(String text, Integer key) {

        if (text == null) {
            text = "";
        }

        StringBuilder decrypted = new StringBuilder();

        for (char currentChar : text.toCharArray()) {

            int lowerPos = VN_ALPHABET_LOWER.indexOf(currentChar);
            int upperPos = VN_ALPHABET_UPPER.indexOf(currentChar);

            if (lowerPos != -1) {
                // lowercase decrypt
                int shifted = (lowerPos - key + VN_ALPHABET_SIZE) % VN_ALPHABET_SIZE;
                decrypted.append(VN_ALPHABET_LOWER.charAt(shifted));
            } else if (upperPos != -1) {
                // uppercase decrypt
                int shifted = (upperPos - key + VN_ALPHABET_SIZE) % VN_ALPHABET_SIZE;
                decrypted.append(VN_ALPHABET_UPPER.charAt(shifted));
            } else {
                decrypted.append(currentChar);
            }
        }
        return decrypted.toString();
    }

}
