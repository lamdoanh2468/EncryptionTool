package model;

public class Vigenere {

    // === ENCRYPTION ===
    public String encrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;

        for (char element : text.toCharArray()) {
            if (Character.isLetter(element)) {

                boolean isUpper = Character.isUpperCase(element);
                char base = isUpper ? 'A' : 'a';

                char keyChar = key.charAt(keyIndex % key.length());
                int keyVal = Character.toUpperCase(keyChar) - 'A';

                int index = element - base;
                int cipherText = (index + keyVal) % 26;

                result.append((char) (cipherText + base));

                keyIndex++; // chỉ tăng ở đây
            } else {
                result.append(element);
            }
        }

        return result.toString();
    }

    // === DECRYPTION ===
    public String decrypt(String text, String key) {
        StringBuilder result = new StringBuilder();
        char[] chars = text.toCharArray();
        int keyIndex = 0;
        for (char element : chars) {
            if (Character.isLetter(element)) {
                // << Uppercase case >>
                boolean isUpper = Character.isUpperCase(element);
                char base = isUpper ? 'A' : 'a';

                char keyChar = key.charAt(keyIndex % key.length());
                int keyVal = Character.toUpperCase(keyChar) - 'A';
                int index = element - base;
                int plainText = (index - keyVal + 26) % 26;

                result.append((char) (plainText + base));
                keyIndex++;
            } else {
                result.append(element);
            }
        }
        return result.toString();
    }

}
