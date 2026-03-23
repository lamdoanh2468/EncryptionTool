package model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class CipherModel {
    /**
     * Map: "LOẠI MÃ" -> list các thuật toán có sẵn.
     * SelectorPanel sẽ lấy key để đổ vào typeCombo và đổi list theo giá trị map.
     */
    public static final Map<String, String[]> TYPE_TO_ALGOS;

    static {
        Map<String, String[]> m = new LinkedHashMap<>();
        // Giữ đúng tên thuật toán như SelectorPanel đang switch theo.
        m.put("Cổ điển", new String[]{"Affine", "Caesar", "Vigenère", "Rail Fence"});
        TYPE_TO_ALGOS = Collections.unmodifiableMap(m);
    }

    public static class Params {
        public int affineA;
        public int affineB;
        public int caesarShift;
        public String vigenereKey = "";
        public int railCount;
    }

    private final Caesar caesar = new Caesar();
    private final Vigenere vigenere = new Vigenere();
    private final Affine affine = new Affine();
    private final RailFenceCipher railFenceCipher = new RailFenceCipher();

    public String encrypt(String text, String algo, Params params) {
        if (algo == null) throw new IllegalArgumentException("Chưa chọn thuật toán");
        if (params == null) params = new Params();
        switch (algo) {
            case "Affine":
                return affine.encrypt(text, params.affineA, params.affineB);
            case "Caesar":
                return caesar.encrypt(text, params.caesarShift);
            case "Vigenère":
                return vigenere.encrypt(text, sanitizeVigenereKey(params.vigenereKey));
            case "Rail Fence":
                return railFenceCipher.encrypt(text, params.railCount);
            default:
                throw new IllegalArgumentException("Thuật toán chưa hỗ trợ: " + algo);
        }
    }

    public String decrypt(String text, String algo, Params params) {
        if (algo == null) throw new IllegalArgumentException("Chưa chọn thuật toán");
        if (params == null) params = new Params();
        switch (algo) {
            case "Affine":
                return affine.decrypt(text, params.affineA, params.affineB);
            case "Caesar":
                return caesar.decrypt(text, params.caesarShift);
            case "Vigenère":
                return vigenere.decrypt(text, sanitizeVigenereKey(params.vigenereKey));
            case "Rail Fence":
                return railFenceCipher.decrypt(text, params.railCount);
            default:
                throw new IllegalArgumentException("Thuật toán chưa hỗ trợ: " + algo);
        }
    }

    private String sanitizeVigenereKey(String key) {
        if (key == null) throw new IllegalArgumentException("Khóa Vigenère không được rỗng");
        // Chỉ giữ ký tự a-z/A-Z để thuật toán tính trị A..Z.
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if (Character.isLetter(c)) sb.append(Character.toUpperCase(c));
        }
        if (sb.length() == 0) throw new IllegalArgumentException("Khóa Vigenère không hợp lệ");
        return sb.toString();
    }

    /**
     * Rail Fence cipher (zig-zag) chỉ hoán vị các ký tự là chữ cái.
     * Các ký tự không phải chữ (khoảng trắng, dấu câu, ...) được giữ nguyên vị trí.
     */
    private static class RailFenceCipher {
        public String encrypt(String text, int rails) {
            if (text == null) text = "";
            if (rails < 2) return text;

            LetterStream ls = LetterStream.from(text);
            if (ls.letters.length <= 1) return text;

            int n = ls.letters.length;
            StringBuilder[] rail = new StringBuilder[rails];
            for (int i = 0; i < rails; i++) rail[i] = new StringBuilder();

            int row = 0;
            int dir = 1;
            for (int i = 0; i < n; i++) {
                rail[row].append(ls.letters[i]);
                if (row == 0) dir = 1;
                else if (row == rails - 1) dir = -1;
                row += dir;
            }

            char[] encryptedLetters = new char[n];
            int idx = 0;
            for (int r = 0; r < rails; r++) {
                for (int j = 0; j < rail[r].length(); j++) {
                    encryptedLetters[idx++] = rail[r].charAt(j);
                }
            }

            return ls.reinsertLetters(encryptedLetters);
        }

        public String decrypt(String text, int rails) {
            if (text == null) text = "";
            if (rails < 2) return text;

            LetterStream ls = LetterStream.from(text);
            if (ls.letters.length <= 1) return text;

            int n = ls.letters.length;

            // 1) Tạo railIndex theo đúng zig-zag pattern.
            int[] railIndex = new int[n];
            int row = 0;
            int dir = 1;
            for (int i = 0; i < n; i++) {
                railIndex[i] = row;
                if (row == 0) dir = 1;
                else if (row == rails - 1) dir = -1;
                row += dir;
            }

            // 2) Đếm số ký tự trên từng rail để biết cắt chuỗi ciphertext.
            int[] counts = new int[rails];
            for (int i = 0; i < n; i++) counts[railIndex[i]]++;

            char[] decryptedLetters = new char[n];

            // 3) Cắt ciphertext letters vào từng rail.
            char[][] railChars = new char[rails][];
            int pointer = 0;
            for (int r = 0; r < rails; r++) {
                railChars[r] = new char[counts[r]];
                for (int j = 0; j < counts[r]; j++) {
                    railChars[r][j] = ls.letters[pointer++];
                }
            }

            // 4) Zig-zag lại để lấy plaintext.
            int[] railPos = new int[rails];
            for (int i = 0; i < n; i++) {
                int r = railIndex[i];
                decryptedLetters[i] = railChars[r][railPos[r]++];
            }

            return ls.reinsertLetters(decryptedLetters);
        }

        /**
         * Tách chuỗi thành: mảng ký tự là chữ.
         * Khi reinsert, ký tự không phải chữ sẽ được giữ nguyên từ chuỗi gốc.
         */
        private static class LetterStream {
            private final char[] originalChars;
            private final char[] letters;

            private LetterStream(char[] originalChars, char[] letters) {
                this.originalChars = originalChars;
                this.letters = letters;
            }

            static LetterStream from(String text) {
                if (text == null) text = "";
                char[] original = text.toCharArray();

                int letterCount = 0;
                for (int i = 0; i < original.length; i++) {
                    if (Character.isLetter(original[i])) letterCount++;
                }

                char[] letters = new char[letterCount];
                int idx = 0;
                for (int i = 0; i < original.length; i++) {
                    char c = original[i];
                    if (Character.isLetter(c)) letters[idx++] = c;
                }

                return new LetterStream(original, letters);
            }

            String reinsertLetters(char[] newLetters) {
                StringBuilder out = new StringBuilder(originalChars.length);
                int letterIdx = 0;

                for (int i = 0; i < originalChars.length; i++) {
                    char orig = originalChars[i];
                    if (Character.isLetter(orig)) out.append(newLetters[letterIdx++]);
                    else out.append(orig);
                }

                return out.toString();
            }
        }
    }
}

