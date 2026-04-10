package model.text;

import java.util.*;

public class Substitution extends ATextCipher<String> {
    // Vietnamese Alphabet
    private static final String VN_ALPHABET = VN_ALPHABET_LOWER + VN_ALPHABET_UPPER;
    private final String alphabet;
    private final int alphabetSize;
    private String currentKey;

    // ==================== CONSTRUCTOR ====================
    public Substitution(String alphabet) {
        if (alphabet == null || alphabet.isEmpty()) {
            throw new IllegalArgumentException("Alphabet không được rỗng");
        }
        this.alphabet = alphabet;
        this.alphabetSize = alphabet.length();
    }

    // Factory method
    public static Substitution createEnglish() {
        return new Substitution("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    public static Substitution createVietnamese() {
        return new Substitution(VN_ALPHABET); // dùng constant của em
    }

    // ==================== GEN KEY (CHUNG) ====================
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

    // ==================== LOAD KEY ====================
    @Override
    public void loadKey(String key) {
        if (key == null || key.length() != alphabetSize) {
            throw new IllegalArgumentException("Key không hợp lệ! Phải có đúng " + alphabetSize + " ký tự.");
        }
        this.currentKey = key;
    }

    @Override
    public String getKey() {
        return currentKey;
    }

    // ==================== ENCRYPT ====================
    @Override
    public String encrypt(String plain, String key) {

        Map<Character, Character> map = buildMapping(alphabet, key);
        StringBuilder result = new StringBuilder(plain.length());

        for (char c : plain.toCharArray()) {
            result.append(map.getOrDefault(c, c));
        }
        return result.toString();
    }

    // ==================== DECRYPT ====================
    @Override
    public String decrypt(String cipher, String key) {
        if (key == null || key.length() != alphabetSize) {
            throw new IllegalArgumentException("Key không hợp lệ khi decrypt");
        }

        Map<Character, Character> reverseMap = buildMapping(key, alphabet);
        StringBuilder result = new StringBuilder(cipher.length());

        for (char c : cipher.toCharArray()) {
            result.append(reverseMap.getOrDefault(c, c));
        }
        return result.toString();
    }

    // ==================== HELPER ====================
    private Map<Character, Character> buildMapping(String from, String to) {
        Map<Character, Character> map = new HashMap<>(alphabetSize);
        for (int i = 0; i < alphabetSize; i++) {
            map.put(from.charAt(i), to.charAt(i));
        }
        return map;
    }

    public String encrypt(String plain) {
        if (currentKey == null) throw new IllegalStateException("Chưa load key");
        return encrypt(plain, currentKey);
    }

    public String decrypt(String cipher) {
        if (currentKey == null) throw new IllegalStateException("Chưa load key");
        return decrypt(cipher, currentKey);
    }
}
