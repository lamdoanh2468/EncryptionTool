package model.cipher.text;
import java.util.*;
public class Permutation extends ATextCipher<int[]> {
    private int[] currentKey;

    @Override
    public int[] genKey() {
        // random length between 4 and 8
        int randomLength = (int) (Math.random() * 5) + 4;
        List<Integer> positions = new ArrayList<>();

        for (int i = 0; i < randomLength; i++) {

            positions.add(i);
        }

        // Shuffle position
        Collections.shuffle(positions);

        currentKey = new int[randomLength];
        for (int i = 0; i < randomLength; i++) {
            currentKey[i] = positions.get(i);
        }

        return currentKey;
    }

    @Override
    public void loadKey(int[] key) {
        this.currentKey = key;
    }

    @Override
    public String getKey() {
        if (currentKey == null) {
            return "";
        }
        StringBuilder keyBuilder = new StringBuilder();

        for (int i = 0; i < currentKey.length; i++) {
            keyBuilder.append(currentKey[i]);
            if (i < currentKey.length - 1) {
                keyBuilder.append(" ");
            }
        }
        return keyBuilder.toString();
    }

    @Override
    public int[] parseKey(String keyString) {

        if (keyString == null || keyString.trim().isEmpty()) {
            throw new IllegalArgumentException("Khóa hiện đang trống");
        }

        try {
            String[] separated = keyString.trim().split("\\s+");
            int[] parsedKey = new int[separated.length];

            for (int i = 0; i < separated.length; i++) {
                parsedKey[i] = Integer.parseInt(separated[i]);
            }
            return parsedKey;

        } catch (Exception ex) {
            return null;
        }
    }

    // Padding
    private String pad(String text, int blockSize) {

        if (text == null) {
            text = "";
        }
        StringBuilder paddedText = new StringBuilder(text);
        int remain = text.length() % blockSize;

        if (remain == 0) {
            return text;
        }

        int neededPadding = blockSize - remain;
        paddedText.append("_".repeat(Math.max(0, neededPadding)));
        return paddedText.toString();
    }

    // Encrypt
    @Override
    public String encrypt(String text, int[] key) {

        if (text == null || key == null || key.length == 0) {
            return "";
        }

        String paddedInput = pad(text, key.length);
        return permute(paddedInput, key);
    }

    private String permute(String text, int[] key) {

        char[] encryptedChars = new char[text.length()];
        char[] originalChars = text.toCharArray();

        for (int blockStart = 0; blockStart < originalChars.length; blockStart += key.length) {
            for (int j = 0; j < key.length; j++) {
                int shiftedIndex = key[j] + blockStart;
                if (shiftedIndex < originalChars.length) {
                    encryptedChars[shiftedIndex] = originalChars[blockStart + j];
                }
            }
        }
        return String.valueOf(encryptedChars);
    }

    // Decrypt
    @Override
    public String decrypt(String text, int[] key) {

        if (text == null || key == null || key.length == 0) {
            return "";
        }

        String plainText = inversePermute(text, key);
        return plainText.replaceAll("_+$", "");
    }

    private String inversePermute(String text, int[] key) {

        char[] plainChars = new char[text.length()];
        char[] encryptedInput = text.toCharArray();

        for (int start = 0; start < encryptedInput.length; start += key.length) {
            for (int j = 0; j < key.length && start + j < encryptedInput.length; j++) {

                int mappedIndex = key[j] + start;
                if (mappedIndex < encryptedInput.length) {
                    plainChars[start + j] = encryptedInput[mappedIndex];
                }
            }
        }
        return new String(plainChars);
    }


}