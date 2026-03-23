package model;

public class Caesar {

    // === ENCRYPTION ===
    public String encrypt(String text, int key) {
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
            } else
                result.append(charElement);

        }
        return result.toString();
    }

    //===DECRYPTION ===
    public String decrypt(String text, int key) {
        // logic giải mã
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
            } else
                result.append(charElement);

        }
        return result.toString();

    }

    static void main() {
        System.out.println(new Caesar().decrypt("kxb yx dq oro", 3));
    }
}
