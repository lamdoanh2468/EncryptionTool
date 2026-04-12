package model.text;

import java.util.Random;

public class Hill extends ATextCipher<int[][]> {
    private static final int VN_MOD = VN_ALPHABET_LOWER.length();
    private static final int EN_MOD = ENG_LOWER.length();

    private int[][] currentKey;

    public static int det(int[][] matrix) {
        return matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1];
    }

    @Override
    public int[][] genKey() {
        Random rng = new Random();
        int[][] matrix;
        do {
            matrix = new int[][]{
                    {rng.nextInt(26), rng.nextInt(26)},
                    {rng.nextInt(26), rng.nextInt(26)}
            };
        } while (gcd(26, Math.abs(det(matrix))) != 1);
        currentKey = matrix;
        return currentKey;
    }

    @Override
    public void loadKey(int[][] key) {
        this.currentKey = key;
    }

    @Override
    public String getKey() {
        if (currentKey == null) return "Khóa Hill hiện không có";

        return "[" + currentKey[0][0] + "," + currentKey[0][1] + "]" + "\n"
                + "[" + currentKey[1][0] + "," + currentKey[1][1] + "]";
    }


    /**
     * [2,5]
     * [1,3]
     * **/
    @Override
    public int[][] parseKey(String keyString) {
        if (keyString == null || keyString.isEmpty()) {
            throw new IllegalArgumentException("Khóa hiện không có");
        }
        try {

            String[] rows = keyString.split("\\n");
            int[][] matrix = new int[2][2];

            for (int i = 0; i < 2; i++) {
                String row = rows[i]
                        .replace("[", "")
                        .replace("]", "")
                        .trim();

                String[] nums = row.split(",");

                matrix[i][0] = Integer.parseInt(nums[0].trim());
                matrix[i][1] = Integer.parseInt(nums[1].trim());
            }

            return matrix;

        } catch (Exception e) {
            return null;
        }
    }

    public int[][] inverseMatrix(int[][] key, int mod) {

        int det = det(key);
        det = (det % mod + mod) % mod;

        int detInv = modInverse(det, mod);

        int[][] inverse = new int[2][2];

        inverse[0][0] = key[1][1];
        inverse[0][1] = -key[0][1];
        inverse[1][0] = -key[1][0];
        inverse[1][1] = key[0][0];

        for (int i = 0; i < inverse.length; i++) {
            for (int j = 0; j < inverse[i].length; j++) {
                inverse[i][j] = (inverse[i][j] * detInv) % mod;
                if (inverse[i][j] < 0) inverse[i][j] += mod;
            }
        }

        return inverse;
    }

    // === ENCRYPTION ===
    @Override
    public String encrypt(String text, int[][] key) {
        if (text == null || key == null) return "";
        boolean isVN = hasVietnamese(text);
        String alphabet = isVN ? VN_ALPHABET_LOWER : ENG_LOWER;
        int mod = isVN ? VN_MOD : EN_MOD;

        // <<< Remove special text, whitespace >>>
        StringBuilder clean = new StringBuilder();
        String lowerText = text.toLowerCase();
        for (char c : lowerText.toCharArray()) {
            if (alphabet.indexOf(c) != -1) {
                clean.append(c);
            }
        }
        String cleaned = clean.toString();
        if (cleaned.length() % 2 != 0) {
            cleaned += "x"; // pad
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < cleaned.length(); i += 2) {

            // <<< Convert to 0 - 25 >>>
            int firstPlain = alphabet.indexOf(cleaned.charAt(i));
            int secondPlain = alphabet.indexOf(cleaned.charAt(i + 1));

            int firstCipher = (key[0][0] * firstPlain + key[0][1] * secondPlain) % mod;
            int secondCipher = (key[1][0] * firstPlain + key[1][1] * secondPlain) % mod;

            // <<< Prevent minus value >>>
            firstCipher = (firstCipher < 0) ? firstCipher + mod : firstCipher;
            secondCipher = (secondCipher < 0) ? secondCipher + mod : secondCipher;


            result.append(alphabet.charAt(firstCipher));
            result.append(alphabet.charAt(secondCipher));
        }

        return result.toString();
    }

    // ===DECRYPTION ===
    @Override
    public String decrypt(String text, int[][] key) {
        if (text == null || key == null) return "";
        boolean isVN = hasVietnamese(text);
        String alphabet = isVN ? VN_ALPHABET_LOWER : ENG_LOWER;
        int mod = isVN ? VN_MOD : EN_MOD;

        // <<< Remove special text, whitespace >>>
        StringBuilder clean = new StringBuilder();
        String lowerText = text.toLowerCase();
        for (char c : lowerText.toCharArray()) {
            if (alphabet.indexOf(c) != -1) {
                clean.append(c);
            }
        }

        StringBuilder result = new StringBuilder();
        int[][] invKey = inverseMatrix(key, mod);

        for (int i = 0; i < clean.length(); i += 2) {

            // <<< Convert to 0 - 25 >>>
            int firstPlain = alphabet.indexOf(clean.charAt(i));
            int secondPlain = alphabet.indexOf(clean.charAt(i + 1));

            int firstCipher = (invKey[0][0] * firstPlain + invKey[0][1] * secondPlain) % mod;
            int secondCipher = (invKey[1][0] * firstPlain + invKey[1][1] * secondPlain) % mod;

            // <<< Prevent minus value >>>
            firstCipher = (firstCipher < 0) ? firstCipher + mod : firstCipher;
            secondCipher = (secondCipher < 0) ? secondCipher + mod : secondCipher;


            result.append(alphabet.charAt(firstCipher));
            result.append(alphabet.charAt(secondCipher));
        }
        if (result.length() > 0 && result.charAt(result.length() - 1) == 'x') {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

}
