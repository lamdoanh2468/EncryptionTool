package model.text;

import java.util.Random;

public class Affine implements ITextCipher<int[]> {
    public static final int VIETNAMESE = 1;  // Mode 1: Vietnamese
    public static final int ENGLISH = 2;  // Mode 2: English
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

    public String encrypt(String text, int[] key, int mode) {
        if (text == null) text = "";

        int a = key[0];
        int b = key[1];

        StringBuilder result = new StringBuilder();
        if (gcd(a, 26) != 1) {
            return "Invalid key";
        }

        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch) && ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))) {
                // Chỉ mã hóa chữ cái Latin cơ bản (A-Z a-z)
                boolean upper = Character.isUpperCase(ch);
                char base = upper ? 'A' : 'a';
                int index = ch - base;
                int cipherText = ((index * a) + b) % 26;
                result.append((char) (cipherText + base));
            } else {
                // Giữ nguyên mọi ký tự khác: dấu tiếng Việt, đ, số, dấu câu, khoảng trắng...
                result.append(ch);
            }
        }
        return result.toString();
    }

    public String decrypt(String text, int[] key, int mode) {
        if (text == null) text = "";

        int a = key[0];
        int b = key[1];

        StringBuilder result = new StringBuilder();
        if (gcd(a, 26) != 1) {
            return "Invalid key";
        }

        int a_1 = modInverse(a, 26);
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch) && ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z'))) {
                boolean upper = Character.isUpperCase(ch);
                char base = upper ? 'A' : 'a';
                int index = ch - base;
                int plainText = (a_1 * (index - b + 26)) % 26;
                plainText = (plainText + 26) % 26;
                result.append((char) (plainText + base));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    @Override
    public String encrypt(String text, int[] key) {
        return encrypt(text, key, ENGLISH);
    }

    @Override
    public String decrypt(String text, int[] key) {
        return decrypt(text, key, ENGLISH);
    }

    static void main(String[] args) {

        Affine affine = new Affine();

        int[] key = affine.genKey();   // key hợp lệ

        String plaintext = "anh nhớ em ";

        String cipher = affine.encrypt(plaintext, key);
        String decrypted = affine.decrypt(cipher, key);

        System.out.println("Plaintext : " + plaintext);
        System.out.println("Ciphertext: " + cipher);
        System.out.println("Decrypted : " + decrypted);
    }
    }

