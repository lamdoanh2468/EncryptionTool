package model.cipher.text;

import java.util.Random;

public class Affine extends ATextCipher<int[]> {

    private static final int ENG_LENGTH = ENG_LOWER.length();
    private static final int VN_LENGTH = VN_ALPHABET_LOWER.length();

    private int[] savedKey;

    @Override
    public int[] genKey() {

        Random rd = new Random();

        int a = 0;

        while (gcd(a, VN_LENGTH) != 1 || gcd(a, ENG_LENGTH) != 1) {

            a = rd.nextInt(VN_LENGTH - 1) + 1;
        }

        int bValue = rd.nextInt(VN_LENGTH);

        savedKey = new int[]{a, bValue};

        return savedKey;
    }

    @Override
    public String getKey() {

        if (savedKey == null) {
            return "Khóa Affine hiện không có";
        }

        return "a = " + savedKey[0] + ", b = " + savedKey[1];
    }

    @Override
    public int[] parseKey(String keyString) {

        if (keyString == null || keyString.trim().isEmpty()) {
            throw new IllegalArgumentException("Không có khóa");
        }

        try {

            String[] splitData = keyString.split(",");

            int a = Integer.parseInt(splitData[0].split("=")[1].trim());

            int b = Integer.parseInt(splitData[1].split("=")[1].trim());

            return new int[]{a, b};

        } catch (Exception ex) {
            return null;
        }
    }

    public int gcd(int a, int b) {

        // standard Euclid algo
        while (b != 0) {

            int temp = b;

            b = a % b;

            a = temp;
        }

        return a;
    }

    public int modInverse(int a, int n) {

        for (int i = 0; i < n; i++) {
            if ((a * i) % n == 1) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public String encrypt(String text, int[] key) {

        if (text == null) {
            text = "";
        }

        int a = key[0];
        int b = key[1];

        boolean vietnameseDetected = hasVietnamese(text);

        String lowerCaseAlphabet = vietnameseDetected ? VN_ALPHABET_LOWER : ENG_LOWER;

        String upperCaseAlphabet = vietnameseDetected ? VN_ALPHABET_UPPER : ENG_UPPER;

        int alphabetSize = vietnameseDetected ? VN_LENGTH : ENG_LENGTH;

        if (gcd(a, alphabetSize) != 1) {

            return "Invalid key";
        }

        StringBuilder encryptedText = new StringBuilder();

        for (char currentChar : text.toCharArray()) {
            int foundIndex = lowerCaseAlphabet.indexOf(currentChar);
            if (foundIndex != -1) {
                // lowercase case
                int encryptedIndex = ((a * foundIndex) + b) % alphabetSize;
                encryptedText.append(lowerCaseAlphabet.charAt(encryptedIndex));

            } else {
                foundIndex = upperCaseAlphabet.indexOf(currentChar);
                if (foundIndex != -1) {
                    // uppercase handling
                    int encryptedIndex = ((a * foundIndex) + b) % alphabetSize;
                    encryptedText.append(upperCaseAlphabet.charAt(encryptedIndex));

                } else {
                    encryptedText.append(currentChar);
                }
            }
        }

        return encryptedText.toString();
    }

    @Override
    public String decrypt(String text, int[] key) {

        if (text == null) {
            text = "";
        }

        int a = key[0];
        int b = key[1];

        boolean containsVN = hasVietnamese(text);

        String lowerAlpha = containsVN ? VN_ALPHABET_LOWER : ENG_LOWER;

        String upperAlpha = containsVN ? VN_ALPHABET_UPPER : ENG_UPPER;

        int modulo = containsVN ? VN_LENGTH : ENG_LENGTH;

        if (gcd(a, modulo) != 1) {

            return "Invalid key";
        }

        StringBuilder decrypted = new StringBuilder();

        int inverseA = modInverse(a, modulo);

        for (char charElement : text.toCharArray()) {

            int idx = lowerAlpha.indexOf(charElement);

            if (idx != -1) {
                // lowercase decrypt
                int decodedIndex = (inverseA * ((idx - b + modulo) % modulo)) % modulo;
                decrypted.append(lowerAlpha.charAt(decodedIndex));

            } else {
                idx = upperAlpha.indexOf(charElement);
                if (idx != -1) {
                    // uppercase decrypt
                    int decodedIndex = (inverseA * ((idx - b + modulo) % modulo)) % modulo;
                    decrypted.append(upperAlpha.charAt(decodedIndex));
                } else {
                    decrypted.append(charElement);
                }
            }
        }

        return decrypted.toString();
    }


}

