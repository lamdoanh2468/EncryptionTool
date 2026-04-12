package model.text;

import java.util.*;

public class Substitution extends ATextCipher<String> {

    private static final String ENG_ALPHABET = ENG_LOWER+ ENG_UPPER;

    private int alphabetSize = ENG_ALPHABET.length();


    @Override
    public String genKey() {

        List<Character> letters = new ArrayList<>();
        for (char c : ENG_ALPHABET.toCharArray()) {
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
        if (key == null) {
            throw new IllegalArgumentException("Key không được null!");
        }

        if (key.length() != alphabetSize) {
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
        if (plain == null || plain.isEmpty()) return plain;

        loadKey(key);

        Map<Character, Character> map = buildMapping(ENG_ALPHABET, key);
        StringBuilder result = new StringBuilder(plain.length());

        for (char c : plain.toCharArray()) {
            result.append(map.getOrDefault(c, c));
        }

        return result.toString();
    }

    @Override
    public String decrypt(String cipher, String key) {
        if (cipher == null || cipher.isEmpty()) return cipher;

        loadKey(key);

        Map<Character, Character> reverseMap = buildMapping(key, ENG_ALPHABET);
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