package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Permutation implements ITextCipher<int[]> {

    private int[] currentKey;

    @Override
    public int[] genKey() {
        int keyLength = (int) (Math.random() * 5) + 4;   // 4 – 8
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < keyLength; i++) {
            indices.add(i);
        }

        Collections.shuffle(indices);

        currentKey = new int[keyLength];
        for (int i = 0; i < keyLength; i++) {
            currentKey[i] = indices.get(i);
        }
        return currentKey;
    }

    @Override
    public void loadKey(int[] key) {
        this.currentKey = key;
    }

    // CREATE PADDING
    private String pad(String text, int block) {
        if (text == null) text = "";
        StringBuilder sb = new StringBuilder(text);
        int remainder = text.length() % block;
        if (remainder == 0) {
            return text;
        }
        int pad = block - remainder;
        sb.append("X".repeat(Math.max(0, pad)));
        return sb.toString();
    }

    // === ENCRYPTION ===
    @Override
    public String encrypt(String text, int[] key) {
        if (text == null || key == null || key.length == 0) {
            return "";
        }

        String padded = pad(text, key.length);
        return permute(padded, key);
    }

    private String permute(String text, int[] key) {
        char[] cipher = new char[text.length()];
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i += key.length) {
            for (int j = 0; j < key.length; j++) {
                int index = key[j] + i;
                if (index < chars.length) {
                    cipher[index] = chars[i + j];
                }
            }
        }
        return String.valueOf(cipher);
    }

    // === DECRYPTION ===
    @Override
    public String decrypt(String text, int[] key) {
        if (text == null || key == null || key.length == 0) {
            return "";
        }

        String decrypted = inversePermute(text, key);
        //Remove padding
        return decrypted.replaceAll("X+$", "");
    }

    private String inversePermute(String text, int[] key) {
        char[] plain = new char[text.length()];
        char[] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i += key.length) {
            for (int j = 0; j < key.length && i + j < chars.length; j++) {
                int index = key[j] + i;
                if (index < chars.length) {
                    plain[i + j] = chars[index];
                }
            }
        }
        return new String(plain);
    }
}