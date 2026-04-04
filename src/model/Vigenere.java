package model;

public class Vigenere {

    // === BẢNG CHỮ CÁI TIẾNG VIỆT (copy từ Caesar) ===
    private static final String VN_ALPHABET_LOWER = "aáàảãạăắằẳẵặâấầẩẫậbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvwxyýỳỷỹỵ";
    private static final String VN_ALPHABET_UPPER = "AÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬBCDĐEÉÈẺẼẸÊẾỀỂỄỆFGHIÍÌỈĨỊJKLMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢPQRSTUÚÙỦŨỤƯỨỪỬỮỰVWXYÝỲỶỸỴ";
    private static final int VN_ALPHABET_SIZE = VN_ALPHABET_LOWER.length();

    // === ENCRYPTION ===
    public String encrypt(String text, String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        // Lọc key chỉ giữ chữ cái (tránh lỗi nếu key có số, dấu...)
        String cleanedKey = key.replaceAll("[^A-Za-zÀ-ỹ]", "").toUpperCase();
        if (cleanedKey.isEmpty()) {
            throw new IllegalArgumentException("Key must contain at least one letter");
        }

        // Auto detect có tiếng Việt không
        boolean hasVietnamese = false;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c) && !((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                hasVietnamese = true;
                break;
            }
        }

        return hasVietnamese ? encryptVN(text, cleanedKey) : encryptEng(text, cleanedKey);
    }

    private String encryptEng(String text, String key) {
        // Logic cũ của bạn (chỉ A-Z a-z)
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        int keyLen = key.length();

        for (char element : text.toCharArray()) {
            if ((element >= 'A' && element <= 'Z') || (element >= 'a' && element <= 'z')) {
                boolean isUpper = Character.isUpperCase(element);
                char base = isUpper ? 'A' : 'a';

                char keyChar = key.charAt(keyIndex % keyLen);
                int shift = keyChar - 'A';

                int index = element - base;
                int encrypted = (index + shift) % 26;

                result.append((char) (encrypted + base));
                keyIndex++;
            } else {
                result.append(element);
            }
        }
        return result.toString();
    }

    private String encryptVN(String text, String key) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        int keyLen = key.length();

        for (char c : text.toCharArray()) {
            int lowerIdx = VN_ALPHABET_LOWER.indexOf(c);
            int upperIdx = VN_ALPHABET_UPPER.indexOf(c);

            if (lowerIdx != -1) {
                // Lowercase Vietnamese
                char keyChar = key.charAt(keyIndex % keyLen);
                int shift = VN_ALPHABET_LOWER.indexOf(Character.toLowerCase(keyChar)); // key luôn upper
                if (shift == -1) shift = 0; // fallback
                int newIdx = (lowerIdx + shift) % VN_ALPHABET_SIZE;
                result.append(VN_ALPHABET_LOWER.charAt(newIdx));
                keyIndex++;
            } else if (upperIdx != -1) {
                // Uppercase Vietnamese
                char keyChar = key.charAt(keyIndex % keyLen);
                int shift = VN_ALPHABET_UPPER.indexOf(keyChar);
                if (shift == -1) shift = 0;
                int newIdx = (upperIdx + shift) % VN_ALPHABET_SIZE;
                result.append(VN_ALPHABET_UPPER.charAt(newIdx));
                keyIndex++;
            } else {
                // Ký tự khác (khoảng trắng, dấu câu, số...) giữ nguyên
                result.append(c);
            }
        }
        return result.toString();
    }

    // === DECRYPTION ===
    public String decrypt(String text, String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        String cleanedKey = key.replaceAll("[^A-Za-zÀ-ỹ]", "").toUpperCase();
        if (cleanedKey.isEmpty()) {
            throw new IllegalArgumentException("Key must contain at least one letter");
        }

        boolean hasVietnamese = false;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c) && !((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                hasVietnamese = true;
                break;
            }
        }

        return hasVietnamese ? decryptVN(text, cleanedKey) : decryptEng(text, cleanedKey);
    }

    private String decryptEng(String text, String key) {
        // Logic cũ
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        int keyLen = key.length();

        for (char c : text.toCharArray()) {
            if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
                boolean isUpper = Character.isUpperCase(c);
                char base = isUpper ? 'A' : 'a';

                char keyChar = key.charAt(keyIndex % keyLen);
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

    private String decryptVN(String text, String key) {
        StringBuilder result = new StringBuilder();
        int keyIndex = 0;
        int keyLen = key.length();

        for (char c : text.toCharArray()) {
            int lowerIdx = VN_ALPHABET_LOWER.indexOf(c);
            int upperIdx = VN_ALPHABET_UPPER.indexOf(c);

            if (lowerIdx != -1) {
                char keyChar = key.charAt(keyIndex % keyLen);
                int shift = VN_ALPHABET_LOWER.indexOf(Character.toLowerCase(keyChar));
                if (shift == -1) shift = 0;
                int newIdx = (lowerIdx - shift + VN_ALPHABET_SIZE) % VN_ALPHABET_SIZE;
                result.append(VN_ALPHABET_LOWER.charAt(newIdx));
                keyIndex++;
            } else if (upperIdx != -1) {
                char keyChar = key.charAt(keyIndex % keyLen);
                int shift = VN_ALPHABET_UPPER.indexOf(keyChar);
                if (shift == -1) shift = 0;
                int newIdx = (upperIdx - shift + VN_ALPHABET_SIZE) % VN_ALPHABET_SIZE;
                result.append(VN_ALPHABET_UPPER.charAt(newIdx));
                keyIndex++;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}