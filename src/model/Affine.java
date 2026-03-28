package model;

import java.util.Random;

public class Affine implements ITextCipher<int[]> {
    private static final int[] VALID_A = {1, 3, 5, 7, 9, 11, 15, 17, 19, 21, 23, 25};

    private int[] currentKey;

    @Override
    public int[] genKey() {
        Random rand = new Random();
        int a = VALID_A[rand.nextInt(VALID_A.length)];
        int b = rand.nextInt(26);
        currentKey = new int[]{a, b};
        return currentKey;
    }

    @Override
    public void loadKey(int[] key) {
        this.currentKey = key;
    }

    public int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public int modInverse(int a, int n) {
        for (int x = 0; x < n; x++) {
            if ((a * x) % n == 1) {
                return x;
            }
        }
        return -1;
    }

    public String encrypt(String text, int[] key) {
        if (text == null)
            text = "";

        // --- Load key ---
        int a = key[0];
        int b = key[1];

        StringBuilder result = new StringBuilder();
        if (gcd(a, 26) != 1) { // Check GCD (a,n) == 1
            return "Invalid key";
        }

        char[] divText = text.toCharArray();
        for (char charElement : divText) {
            // Check if is not a letter
            if (Character.isLetter(charElement)) {
                boolean upper = Character.isUpperCase(charElement);
                char base = upper ? 'A' : 'a';
                int index = charElement - base; // 0 - 25
                int cipherText = ((index * a) + b) % 26;
                result.append((char) (cipherText + base));
            } else {
                result.append(charElement);
            }
        }
        return result.toString();
    }

    // ===DECRYPTION ===
    public String decrypt(String text, int[] key) {

        if (text == null)
            text = "";

        // --- Load key ---
        int a = key[0];
        int b = key[1];

        StringBuilder result = new StringBuilder();
        if (gcd(a, 26) != 1) { // Check GCD (a,n) == 1
            return "Invalid key";
        }
        int a_1 = modInverse(a, 26);
        char[] divText = text.toCharArray();
        for (char charElement : divText) {
            // Check if is not a number
            if (Character.isLetter(charElement)) {
                boolean upper = Character.isUpperCase(charElement);
                char base = upper ? 'A' : 'a';
                int index = charElement - base;
                int plainText = (a_1 * (index - b + 26)) % 26;
                plainText = (plainText + 26) % 26;
                result.append((char) (plainText + base));
            } else
                result.append(charElement);

        }
        return result.toString();

    }
}
