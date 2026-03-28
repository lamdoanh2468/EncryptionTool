package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Substitution {
    public static String generateKey() {
        List<Character> letters = new ArrayList<Character>();
        StringBuilder key = new StringBuilder();

        for (char c = 'A'; c < 'Z'; c++) {
            letters.add(c);
        }
        // Shuffle characters
        Collections.shuffle(letters);

        for (char c : letters) {
            key.append(c);
        }
        return key.toString();
    }

    static void main() {
        Substitution s = new Substitution();
        String key = generateKey();
        String encrypted = s.encrypt("anhyeuem", key);
        String decrypted = s.decrypt(encrypted, key);
        System.out.println(decrypted);

    }

    public String encrypt(String plain, String key) {
        plain = plain.toUpperCase();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder();
        for (char c : plain.toCharArray()) {
            //  1. Get index in alphabet
            //  2. Convert char in key
            int index = alphabet.indexOf(c);
            result.append(key.charAt(index));
        }
        return result.toString();
    }

    public String decrypt(String cipher, String key) {
        cipher = cipher.toUpperCase();
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder result = new StringBuilder();

        for (char c : cipher.toCharArray()) {
            int index = key.indexOf(c);
            result.append(alphabet.charAt(index));
        }
        return result.toString();
    }
}
