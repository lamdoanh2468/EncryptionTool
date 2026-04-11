package model.text;

import java.util.*;

public class Substitution extends ATextCipher<String> {

    private static final String ENG_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String VN_ALPHABET = VN_ALPHABET_LOWER + VN_ALPHABET_UPPER;

    private String alphabet;
    private int alphabetSize;

    // ==================== DETECT ALPHABET ====================
    private void detectAlphabet(String text) {
        if (hasVietnamese(text)) {
            alphabet = VN_ALPHABET;
        } else {
            alphabet = ENG_ALPHABET;
        }
        alphabetSize = alphabet.length();
    }

    @Override
    public String genKey() {
        List<Character> letters = new ArrayList<>();

        for (char c : alphabet.toCharArray()) {
            letters.add(c);
        }

        Collections.shuffle(letters);

        StringBuilder key = new StringBuilder(alphabetSize);
        for (char c : letters) {
            key.append(c);
        }

        return key.toString();
    }

    @Override
    public void loadKey(String key) {
        if (key == null || key.length() != alphabetSize) {
            throw new IllegalArgumentException(
                    "Key không hợp lệ! Phải có đúng " + alphabetSize + " ký tự.");
        }
        this.currentKey = key;
    }

    @Override
    public String getKey() {
        return currentKey;
    }

    @Override
    public String parseKey(String keyString) {
        return keyString;
    }

    @Override
    public String encrypt(String plain, String key) {

        detectAlphabet(plain);

        Map<Character, Character> map = buildMapping(alphabet, key);
        StringBuilder result = new StringBuilder(plain.length());

        for (char c : plain.toCharArray()) {
            result.append(map.getOrDefault(c, c));
        }

        return result.toString();
    }
    @Override
    public String decrypt(String cipher, String key) {

        detectAlphabet(cipher);

        Map<Character, Character> reverseMap = buildMapping(key, alphabet);
        StringBuilder result = new StringBuilder(cipher.length());

        for (char c : cipher.toCharArray()) {
            result.append(reverseMap.getOrDefault(c, c));
        }

        return result.toString();
    }

    private Map<Character, Character> buildMapping(String from, String to) {

        Map<Character, Character> map = new HashMap<>(alphabetSize);

        for (int i = 0; i < alphabetSize; i++) {
            map.put(from.charAt(i), to.charAt(i));
        }

        return map;
    }

}