package model.cipher.text;

import java.util.Random;

public class Hill extends ATextCipher<int[][]> {
    private static final int VN_SIZE = VN_ALPHABET_LOWER.length();
    private static final int EN_SIZE = ENG_LOWER.length();

    private int[][] loadedKey;

    public static int det(int[][] mtx) {
        return (mtx[0][0] * mtx[1][1]) - (mtx[0][1] * mtx[1][0]);
    }

    @Override
    public int[][] genKey() {

        Random random = new Random();
        int[][] generated;

        do {

            generated = new int[][]{{random.nextInt(26), random.nextInt(26)}, {random.nextInt(26), random.nextInt(26)}};

        } while (gcd(26, Math.abs(det(generated))) != 1);

        loadedKey = generated;

        return generated;
    }

    @Override
    public String getKey() {

        if (loadedKey == null) {
            return "Khóa Hill hiện không có";
        }

        return "[" + loadedKey[0][0] + "," + loadedKey[0][1] + "]\n" + "[" + loadedKey[1][0] + "," + loadedKey[1][1] + "]";
    }

    @Override
    public int[][] parseKey(String keyString) {

        if (keyString == null || keyString.trim().isEmpty()) {
            throw new IllegalArgumentException("Khóa hiện không có");
        }

        int[][] parsed = new int[2][2];

        try {

            String[] lines = keyString.split("\\n");
            // assuming 2x2
            for (int rowIndex = 0; rowIndex < 2; rowIndex++) {

                String currentLine = lines[rowIndex].replace("[", "").replace("]", "").trim();
                String[] values = currentLine.split(",");

                parsed[rowIndex][0] = Integer.parseInt(values[0].trim());
                parsed[rowIndex][1] = Integer.parseInt(values[1].trim());
            }

        } catch (Exception ex) {
            return null;
        }

        return parsed;
    }

    public int[][] inverseMatrix(int[][] key, int mod) {

        int determinant = det(key);

        determinant = ((determinant % mod) + mod) % mod;

        int inverseDet = modInverse(determinant, mod);

        int[][] inverseMatrix = new int[2][2];

        // adjudicate matrix
        inverseMatrix[0][0] = key[1][1];
        inverseMatrix[0][1] = -key[0][1];

        inverseMatrix[1][0] = -key[1][0];
        inverseMatrix[1][1] = key[0][0];

        for (int row = 0; row < inverseMatrix.length; row++) {
            for (int col = 0; col < inverseMatrix[row].length; col++) {

                int value = inverseMatrix[row][col] * inverseDet;
                value = value % mod;

                if (value < 0) {
                    value += mod;
                }

                inverseMatrix[row][col] = value;
            }
        }

        return inverseMatrix;
    }

    // Encrypt
    @Override
    public String encrypt(String text, int[][] key) {

        if (text == null || key == null) {
            return "";
        }

        boolean vietnameseMode = hasVietnamese(text);

        String alphabet = vietnameseMode ? VN_ALPHABET_LOWER : ENG_LOWER;

        int modulo = vietnameseMode ? VN_SIZE : EN_SIZE;

        StringBuilder filteredText = new StringBuilder();

        String lowered = text.toLowerCase();

        for (char ch : lowered.toCharArray()) {

            if (alphabet.indexOf(ch) != -1) {
                filteredText.append(ch);
            }
        }

        String plain = filteredText.toString();

        // padding if odd length
        if (plain.length() % 2 != 0) {
            plain += "x";
        }

        StringBuilder encrypted = new StringBuilder();

        for (int idx = 0; idx < plain.length(); idx += 2) {

            int left = alphabet.indexOf(plain.charAt(idx));
            int right = alphabet.indexOf(plain.charAt(idx + 1));

            int enc1 = (key[0][0] * left) + (key[0][1] * right);

            int enc2 = (key[1][0] * left) + (key[1][1] * right);

            enc1 = enc1 % modulo;
            enc2 = enc2 % modulo;

            // not really needed often, but safer
            if (enc1 < 0) {
                enc1 += modulo;
            }

            if (enc2 < 0) {
                enc2 += modulo;
            }

            encrypted.append(alphabet.charAt(enc1));
            encrypted.append(alphabet.charAt(enc2));
        }

        return encrypted.toString();
    }

    // Decrypt
    @Override
    public String decrypt(String text, int[][] key) {

        if (text == null || key == null) {
            return "";
        }

        boolean vnMode = hasVietnamese(text);

        String alphabet = vnMode ? VN_ALPHABET_LOWER : ENG_LOWER;

        int modulo = vnMode ? VN_SIZE : EN_SIZE;

        StringBuilder cleanedInput = new StringBuilder();

        for (char c : text.toLowerCase().toCharArray()) {

            if (alphabet.indexOf(c) >= 0) {
                cleanedInput.append(c);
            }
        }

        int[][] reverseKey = inverseMatrix(key, modulo);

        StringBuilder decrypted = new StringBuilder();

        for (int i = 0; i < cleanedInput.length(); i += 2) {

            int first = alphabet.indexOf(cleanedInput.charAt(i));
            int second = alphabet.indexOf(cleanedInput.charAt(i + 1));

            int decoded1 = (reverseKey[0][0] * first) + (reverseKey[0][1] * second);
            int decoded2 = (reverseKey[1][0] * first) + (reverseKey[1][1] * second);

            decoded1 %= modulo;
            decoded2 %= modulo;

            if (decoded1 < 0) decoded1 += modulo;
            if (decoded2 < 0) decoded2 += modulo;

            decrypted.append(alphabet.charAt(decoded1));
            decrypted.append(alphabet.charAt(decoded2));
        }

        if (!decrypted.isEmpty() && decrypted.charAt(decrypted.length() - 1) == 'x') {

            decrypted.deleteCharAt(decrypted.length() - 1);
        }

        return decrypted.toString();
    }


}
