package model;

import javax.crypto.*;
import java.security.NoSuchAlgorithmException;

public class Affine {

    public SecretKey key;

    public SecretKey genKey() throws NoSuchAlgorithmException {

        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
        keyGenerator.init(56);
        key = keyGenerator.generateKey();
        return key;
    }

    public void loadKey(SecretKey key) {

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

    public String encrypt(String text, int a, int b) {
        if (text == null) text = "";
        StringBuilder result = new StringBuilder();
        if (gcd(a, 26) != 1) {        // Check GCD (a,n) == 1
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

    //===DECRYPTION ===
    public String decrypt(String text, int a, int b) {
        // logic giải mã
        if (text == null) text = "";
        StringBuilder result = new StringBuilder();
        if (gcd(a, 26) != 1) {        // Check GCD (a,n) == 1
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

    static void main() {
        System.out.println(new Affine().decrypt("Vuc btp tr", 3, 7));
    }
}
