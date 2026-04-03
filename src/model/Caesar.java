package model;

import java.util.Random;

public class Caesar implements ITextCipher<Integer> {

    // --- Add Vietnamese alphabet ---
    private static final String VN_ALPHABET_LOWER = "aáàảãạăắằẳẵặâấầẩẫậbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvwxyýỳỷỹỵ";

    private static final String VN_ALPHABET_UPPER = "AÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬBCDĐEÉÈẺẼẸÊẾỀỂỄỆFGHIÍÌỈĨỊJKLMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢPQRSTUÚÙỦŨỤƯỨỪỬỮỰVWXYÝỲỶỸỴ";

    private static final int ALPHABET_SIZE = VN_ALPHABET_LOWER.length();

    private int currentKey;

    @Override
    public Integer genKey() {
        currentKey = new Random().nextInt(25) + 1; // 1 – 25
        return currentKey;
    }

    @Override
    public void loadKey(Integer key) {
        this.currentKey = key;
    }

    // === ENCRYPTION ===
    @Override
    public String encrypt(String text, Integer key) {
        if (text == null || key == null) {
            text = "";
        }
        boolean hasVietnameseUnicode = false;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c) && !((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                hasVietnameseUnicode = true;
            }
        }

        if (hasVietnameseUnicode) {
            return encryptVN(text, key);
        } else return encryptEng(text, key);
    }

    public String encryptVN(String text, Integer key) {
        if (text == null) text = "";
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            int vnIdx = VN_ALPHABET_LOWER.indexOf(c);
            int vnIdxUpper = VN_ALPHABET_UPPER.indexOf(c);

            if (vnIdx != -1) {
                // Lowercase
                int shifted = (vnIdx + key) % ALPHABET_SIZE;
                result.append(VN_ALPHABET_LOWER.charAt(shifted));

            } else if (vnIdxUpper != -1) {
                // Uppercase
                int shifted = (vnIdxUpper + key) % ALPHABET_SIZE;
                result.append(VN_ALPHABET_UPPER.charAt(shifted));

            } else {
                // Others
                result.append(c);
            }
        }
        return result.toString();
    }

    public String encryptEng(String text, Integer key) {
        if (text == null) text = "";
        int k = ((key % 26) + 26) % 26;
        StringBuilder result = new StringBuilder();
        char[] divText = text.toCharArray();
        for (char charElement : divText) {
            // Check if is not a letter
            if (Character.isLetter(charElement)) {
                boolean upper = Character.isUpperCase(charElement);
                char base = upper ? 'A' : 'a';
                int index = charElement - base; // 0 - 25
                int cipherText = (index + k) % 26;
                result.append((char) (cipherText + base));

            } else result.append(charElement);

        }
        return result.toString();
    }

    // ===DECRYPTION ===
    @Override
    public String decrypt(String text, Integer key) {
        if (text == null || key == null) {
            text = "";
        }
        boolean hasVietnameseUnicode = false;

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c) && !((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                hasVietnameseUnicode = true;
            }
        }

        if (hasVietnameseUnicode) {
            return decryptVN(text, key);
        } else return decryptEng(text, key);

    }

    public String decryptEng(String text, Integer key) {
        if (text == null) text = "";

        int k = ((key % 26) + 26) % 26;
        StringBuilder result = new StringBuilder();
        char[] divText = text.toCharArray();

        for (char charElement : divText) {
            // Check if is not a number
            if (Character.isLetter(charElement)) {
                boolean upper = Character.isUpperCase(charElement);
                char base = upper ? 'A' : 'a';
                int index = charElement - base;
                int plainText = (index - k + 26) % 26;
                result.append((char) (plainText + base));
            } else result.append(charElement);

        }
        return result.toString();
    }

    public String decryptVN(String text, Integer key) {
        if (text == null) text = "";
        StringBuilder result = new StringBuilder();

        for (char c : text.toCharArray()) {
            int vnIdx = VN_ALPHABET_LOWER.indexOf(c);
            int vnIdxUpper = VN_ALPHABET_UPPER.indexOf(c);

            if (vnIdx != -1) {
                // Lowercase
                int shifted = (vnIdx - key + ALPHABET_SIZE) % ALPHABET_SIZE;
                result.append(VN_ALPHABET_LOWER.charAt(shifted));

            } else if (vnIdxUpper != -1) {
                // Uppercase
                int shifted = (vnIdxUpper - key + ALPHABET_SIZE) % ALPHABET_SIZE;
                result.append(VN_ALPHABET_UPPER.charAt(shifted));

            } else {
                // Others
                result.append(c);
            }
        }
        return result.toString();
    }
}
