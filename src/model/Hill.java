package model;

public class Hill {

    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);

    }

    public static int det(int[][] matrix) {
        return matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1];
    }

    public int[][] inverseMatrix(int[][] key) {

        int det = det(key);
        det = (det % 26 + 26) % 26;

        int detInv = modInverse(det, 26);

        int[][] inverse = new int[2][2];

        inverse[0][0] = key[1][1];
        inverse[0][1] = -key[0][1];
        inverse[1][0] = -key[1][0];
        inverse[1][1] = key[0][0];

        for (int i = 0; i < inverse.length; i++) {
            for (int j = 0; j < inverse[i].length; j++) {
                inverse[i][j] = (inverse[i][j] * detInv) % 26;
                if (inverse[i][j] < 0)
                    inverse[i][j] += 26;
            }
        }

        return inverse;
    }

    private int modInverse(int a, int n) {
        for (int x = 1; x < n; x++) {
            if ((a * x % n) == 1) {
                return x;
            }
        }
        throw new IllegalArgumentException("Not found mod inversion between " + a + " mod " + n);
    }

    // === ENCRYPTION ===
    public String encrypt(String text, int[][] key) {

        // <<< Remove special text, whitespace >>>
        text = text.toLowerCase().replaceAll("[^a-z]", "");

        if (text.length() % 2 != 0)
            text = text + "x";

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i += 2) {

            // <<< Convert to 0 - 25 >>>
            int firstPlain = text.charAt(i) - 'a';
            int secondPlain = text.charAt(i + 1) - 'a';

            int firstCipher = (key[0][0] * firstPlain + key[0][1] * secondPlain) % 26;
            int secondCipher = (key[1][0] * firstPlain + key[1][1] * secondPlain) % 26;

            // <<< Prevent minus value >>>
            firstCipher = (firstCipher < 0) ? firstCipher + 26 : firstCipher;
            secondCipher = (secondCipher < 0) ? secondCipher + 26 : secondCipher;
            ;

            result.append((char) (firstCipher + 'a'));
            result.append((char) (secondCipher + 'a'));
        }

        return result.toString();
    }

    // ===DECRYPTION ===
    public String decrypt(String text, int[][] key) {

        text = text.toLowerCase().replaceAll("[^a-z]", "");

        // logic giải mã
        StringBuilder result = new StringBuilder();
        if (gcd(26, det(key)) == 1) {
            int[][] inverseKey = inverseMatrix(key);

            for (int i = 0; i < text.length(); i += 2) {
                // <<< Convert to 0 - 25 >>>
                int firstCipher = text.charAt(i) - 'a';
                int secondCipher = text.charAt(i + 1) - 'a';

                int firstPlain = (inverseKey[0][0] * firstCipher + inverseKey[0][1] * secondCipher) % 26;
                int secondPlain = (inverseKey[1][0] * firstCipher + inverseKey[1][1] * secondCipher) % 26;

                // <<< Prevent minus value >>>
                if (firstPlain < 0)
                    firstPlain += 26;
                if (secondPlain < 0)
                    secondPlain += 26;

                result.append((char) (firstPlain + 'a'));
                result.append((char) (secondPlain + 'a'));

            }
        }

        return result.toString();
    }

}
