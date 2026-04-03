package model;

public class Vigenere {

    // === ENCRYPTION ===
    public String encrypt(String text, String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        String upperKey = key.toUpperCase();  // chuẩn hóa key một lần

        for (char element : text.toCharArray()) {
            if (Character.isLetter(element)) {
                boolean isUpper = Character.isUpperCase(element);
                char base = isUpper ? 'A' : 'a';

                char keyChar = upperKey.charAt(keyIndex % upperKey.length());
                int shift = keyChar - 'A';

                int index = element - base;
                int encrypted = (index + shift) % 26;

                result.append((char) (encrypted + base));
                keyIndex++;
            } else {
                result.append(element);  // giữ nguyên khoảng trắng, dấu câu, ký tự tiếng Việt...
            }
        }
        return result.toString();
    }

    public String decrypt(String text, String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        String upperKey = key.toUpperCase();

        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                boolean isUpper = Character.isUpperCase(c);
                char base = isUpper ? 'A' : 'a';

                char keyChar = upperKey.charAt(keyIndex % upperKey.length());
                int shift = keyChar - 'A';

                int index = c - base;
                int decrypted = (index - shift + 26) % 26;

                result.append((char) (decrypted + base));
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    static void main(String[] args) {
        Vigenere v = new Vigenere();
        String encrypted = v.encrypt("ATTACKATDAWN", "LEMON");
        System.out.println(encrypted);   // → LXFOPVEFRNHR   (đúng)

        String decrypted = v.decrypt(encrypted, "LEMON");
        System.out.println(decrypted);   // → ATTACKATDAWN   (đúng)
    }
}
