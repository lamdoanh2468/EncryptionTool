package model;

public class Permutation implements ITextCipher<int[]> {
    /*
     *   Ex: H E L L O << Method 2 >>
     *       0 1 2 3 4
     *       key = [3,1,2,4,0] << Method 1 >>
     *   Method 1 : cipher[i] = text.charAt[key[i]]
     *   Method 2 : cipher[key[i]] = text[i]
     * */
    @Override
    public int[] genKey() {
        return new int[0];
    }

    @Override
    public void loadKey(int[] key) {

    }

    private String pad(String text, int block) {
        StringBuilder sb = new StringBuilder(text);
        int remainder = text.length() % block;
        if (remainder == 0) {
            return text;
        }
        int pad = block - remainder;
        sb.append("X".repeat(Math.max(0, pad)));
        return sb.toString();
    }

    @Override
    public String encrypt(String plaintext, int[] key) {
        // --- Padding ---
        plaintext = pad(plaintext, key.length);

        char[] cipher = new char[plaintext.length()];
        char[] chars = plaintext.toCharArray();

        for (int i = 0; i < chars.length; i += key.length) {
            for (int j = 0; j < key.length; j++) {
                int index = key[j] + i;
                cipher[index] = chars[i + j];
            }
        }
        return String.valueOf(cipher);
    }

    @Override
    public String decrypt(String ciphertext, int[] key) {

        char[] plain = new char[ciphertext.length()];
        char[] chars = ciphertext.toCharArray();

        for (int i = 0; i < chars.length; i += key.length) {
            for (int j = 0; j < key.length && i + j < chars.length; j++) {
                int index = key[j] + i;
                plain[i + j] = chars[index];
            }
        }
        // --- Remove padding ---

        return new String(plain).replaceAll("X+$", "");
    }

    static void main(String[] args) {
        Permutation permutation = new Permutation();
        int[] key = new int[]{
                3, 1, 4, 2, 0
        }; // HELLO
        String cipherText = permutation.encrypt("HELLO WORLD", key);
        String plainText = permutation.decrypt(cipherText, key);
        System.out.println(cipherText + "\n" + plainText);
    }
}
