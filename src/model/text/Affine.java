package model.text;

import java.util.Random;

public class Affine extends ATextCipher<int[]> {
    private static final int ENG_SIZE = ENG_LOWER.length();
    private static final int VN_SIZE = VN_ALPHABET_LOWER.length();  // 92

    private int[] currentKey;

    // ==================== TEST ====================
    public static void main(String[] args) {
        Affine affine = new Affine();
        int[] key = affine.genKey();

        String plaintext = "Anh nhớ em rất nhiều! I love you. ❤️";

        String cipher = affine.encrypt(plaintext, key);
        String decrypted = affine.decrypt(cipher, key);

        System.out.println("Plaintext : " + plaintext);
        System.out.println("Key       : a = " + key[0] + ", b = " + key[1]);
        System.out.println("Ciphertext: " + cipher);
        System.out.println("Decrypted : " + decrypted);
    }

    @Override
    public int[] genKey() {
        Random rand = new Random();
        int a = 0;
        while (gcd(a, VN_SIZE) != 1 || gcd(a, ENG_SIZE) != 1) {
            a = rand.nextInt(VN_SIZE - 1) + 1;
        }
        int b = rand.nextInt(VN_SIZE);
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

    @Override
    public String encrypt(String text, int[] key) {
        if (text == null) text = "";

        int a = key[0];
        int b = key[1];
        boolean hasVNChar = hasVietnamese(text);

        String lower = hasVNChar ? VN_ALPHABET_LOWER : ENG_LOWER;
        String upper = hasVNChar ? VN_ALPHABET_UPPER : ENG_UPPER;
        int n = hasVietnamese(text) ? VN_SIZE : ENG_SIZE;

        if (gcd(a, n) != 1) {
            return "Invalid key";
        }
        StringBuilder result = new StringBuilder();

        for (char ch : text.toCharArray()) {
            int index = lower.indexOf(ch);
            if (index != -1) { // Check if it has element
                // lowercase
                int newIdx = (a * index + b) % n;
                result.append(lower.charAt(newIdx));
            } else {
                index = upper.indexOf(ch);
                if (index != -1) {
                    // uppercase
                    int newIdx = (a * index + b) % n;
                    result.append(upper.charAt(newIdx));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }


    @Override
    public String decrypt(String text, int[] key) {
        if (text == null) text = "";

        int a = key[0];
        int b = key[1];
        boolean hasVNChar = hasVietnamese(text);

        String lower = hasVNChar ? VN_ALPHABET_LOWER : ENG_LOWER;
        String upper = hasVNChar ? VN_ALPHABET_UPPER : ENG_UPPER;
        int n = hasVNChar ? VN_SIZE : ENG_SIZE;

        if (gcd(a, n) != 1) {
            return "Invalid key";
        }
        StringBuilder result = new StringBuilder();
        int a_1 = modInverse(a, n);
        for (char ch : text.toCharArray()) {
            int index = lower.indexOf(ch);
            if (index != -1) {
                // chữ thường
                int newIdx = (a_1 * ((index - b + n) % n)) % n;
                result.append(lower.charAt(newIdx));
            } else {
                index = upper.indexOf(ch);
                if (index != -1) {
                    // chữ hoa
                    int newIdx = (a_1 * ((index - b + n) % n)) % n;
                    result.append(upper.charAt(newIdx));
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }


}

