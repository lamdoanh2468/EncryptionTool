package model.text;

import java.util.Random;

public class Affine implements ITextCipher<int[]> {
    private static final String VN_ALPHABET_LOWER = "aáàảãạăắằẳẵặâấầẩẫậbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvwxyýỳỷỹỵ";

    private static final String VN_ALPHABET_UPPER = "AÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬBCDĐEÉÈẺẼẸÊẾỀỂỄỆFGHIÍÌỈĨỊJKLMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢPQRSTUÚÙỦŨỤƯỨỪỬỮỰVWXYÝỲỶỸỴ";

    private static final int ALPHABET_SIZE = VN_ALPHABET_LOWER.length();  // 92
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
        while (gcd(a, ALPHABET_SIZE) != 1) {
            a = rand.nextInt(ALPHABET_SIZE - 1) + 1;
        }
        int b = rand.nextInt(ALPHABET_SIZE);
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
        int n = ALPHABET_SIZE;

        if (gcd(a, 26) != 1) {
            return "Invalid key";
        }
        StringBuilder result = new StringBuilder();

        for (char ch : text.toCharArray()) {
            int index = VN_ALPHABET_LOWER.indexOf(ch);
            if (index != -1) { // Check if it has element
                // lowercase
                int newIdx = (a * index + b) % n;
                result.append(VN_ALPHABET_LOWER.charAt(newIdx));
            } else {
                index = VN_ALPHABET_UPPER.indexOf(ch);
                if (index != -1) {
                    // uppercase
                    int newIdx = (a * index + b) % n;
                    result.append(VN_ALPHABET_UPPER.charAt(newIdx));
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
        int n = ALPHABET_SIZE;

        if (gcd(a, 26) != 1) {
            return "Invalid key";
        }
        StringBuilder result = new StringBuilder();
        int a_1 = modInverse(a,n);
        for (char ch : text.toCharArray()) {
            int index = VN_ALPHABET_LOWER.indexOf(ch);

            if (index != -1) {
                // chữ thường
                int newIdx = (a_1 * ((index - b + n) % n)) % n;
                result.append(VN_ALPHABET_LOWER.charAt(newIdx));
            }
            else {
                index = VN_ALPHABET_UPPER.indexOf(ch);
                if (index != -1) {
                    // chữ hoa
                    int newIdx = (a_1 * ((index - b + n) % n)) % n;
                    result.append(VN_ALPHABET_UPPER.charAt(newIdx));
                }
                else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }


}

