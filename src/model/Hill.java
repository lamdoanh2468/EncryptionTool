package model;

public class Hill {
    private static final String VN_ALPHABET = "aăâbcdđeêghiklmnoôơpqrstuưvxy" + "áàảãạắằẳẵặấầẩẫậ" + "éèẻẽẹếềểễệ" + "íìỉĩị" + "óòỏõọốồổỗộớờởỡợ" + "úùủũụứừửữự" + "ýỳỷỹỵ";
    private static final int VN_MOD = VN_ALPHABET.length();
    private static final String ENG_ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int EN_MOD = ENG_ALPHABET.length();


    public static int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);

    }

    public static int det(int[][] matrix) {
        return matrix[0][0] * matrix[1][1] - matrix[1][0] * matrix[0][1];
    }

    public int[][] inverseMatrix(int[][] key,int mod) {

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
        if (text == null || key == null) return "";
        boolean isVN = hasVietnamese(text);
        String alphabet = isVN ? VN_ALPHABET : ENG_ALPHABET;
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
            int secondPlain = alphabet.indexOf(cleaned.charAt(i+1));

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

    private boolean hasVietnamese(String text) {
        for(char c : text.toCharArray()) {
            if (Character.isLetter(c) && !((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
               return true;
            }
        }
        return false;
    }

    // ===DECRYPTION ===
    public String decrypt(String text, int[][] key) {
        if (text == null || key == null) return "";
        boolean isVN = hasVietnamese(text);
        String alphabet = isVN ? VN_ALPHABET : ENG_ALPHABET;
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
        int[][] invKey = inverseMatrix(key, mod);

        for (int i = 0; i < cleaned.length(); i += 2) {

            // <<< Convert to 0 - 25 >>>
            int firstPlain = alphabet.indexOf(cleaned.charAt(i));
            int secondPlain = alphabet.indexOf(cleaned.charAt(i+1));

            int firstCipher = (invKey[0][0] * firstPlain + invKey[0][1] * secondPlain) % mod;
            int secondCipher = (invKey[1][0] * firstPlain + invKey[1][1] * secondPlain) % mod;

            // <<< Prevent minus value >>>
            firstCipher = (firstCipher < 0) ? firstCipher + mod : firstCipher;
            secondCipher = (secondCipher < 0) ? secondCipher + mod : secondCipher;


            result.append(alphabet.charAt(firstCipher));
            result.append(alphabet.charAt(secondCipher));
        }

        return result.toString();
    }
}
