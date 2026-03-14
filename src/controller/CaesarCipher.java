package controller;

public class CaesarCipher {

    // === CREATE SUBSCRIPTION TABLE ===
    public char[] createEncryptTable(int n, int key) {
        char[] cipher = new char[n];
        for (int x = 0; x < n; x++) {
            int encrypt = (x + key) % n;
            cipher[x] = (char) ('a' + encrypt);
        }
        return cipher;
    }

    public char[] createDecryptTable(int n, int key) {
        char[] plain = new char[n];
        for (int y = 0; y < n; y++) {
            int decrypt = (y - key + n) % n;
            plain[y] = (char) ('a' + decrypt);
        }
        return plain;
    }

    // === ENCRYPTION ===
    public String encrypt(String text, int key) {

        String result = "";
        char[] divText = text.toCharArray();
        char[] subTable = createEncryptTable(26, 3);
        for (char charElement : divText) {
            // Check if is not a number
            if (Character.isLetter(charElement)) {
                int index = charElement - 'a';
                result += subTable[index];
            }
        }
        return result;
    }

    // === DECRYPTION ===
    public String decrypt(String text, int key) {
        // logic giải mã
        String result = "";
        char[] divText = text.toCharArray();
        char[] subTable = createDecryptTable(26, 3);
        for (char charElement : divText) {
            // Check if is not a number
            if (Character.isLetter(charElement)) {
                int index = charElement - 'a';
                result += subTable[index];
            }
        }
        return result;

    }

    static void main() {
        System.out.println(new CaesarCipher().decrypt("dqkbhxhp", 3));
    }
}
