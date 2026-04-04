package model.text;

public interface ITextCipher<K> {
    K genKey();

    void loadKey(K key);

    String encrypt(String plaintext, K key);

    String decrypt(String ciphertext, K key);

}
