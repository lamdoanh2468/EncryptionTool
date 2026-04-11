package model.text;

public abstract class ATextCipher<K> implements ITextCipher<K> {

    protected K currentKey;

    protected static final String ENG_LOWER = "abcdefghijklmnopqrstuvwxyz";
    protected static final String ENG_UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    protected static final String VN_ALPHABET_LOWER = "aáàảãạăắằẳẵặâấầẩẫậbcdđeéèẻẽẹêếềểễệfghiíìỉĩịjklmnoóòỏõọôốồổỗộơớờởỡợpqrstuúùủũụưứừửữựvwxyýỳỷỹỵ";
    protected static final String VN_ALPHABET_UPPER = "AÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬBCDĐEÉÈẺẼẸÊẾỀỂỄỆFGHIÍÌỈĨỊJKLMNOÓÒỎÕỌÔỐỒỔỖỘƠỚỜỞỠỢPQRSTUÚÙỦŨỤƯỨỪỬỮỰVWXYÝỲỶỸỴ";

    @Override
    public void loadKey(K key) {
        this.currentKey = key;
    }
    public abstract String getKey();
    public abstract K parseKey(String keyString) ;
    public K getCurrentKey() {
        return this.currentKey;
    }
    protected boolean hasVietnamese(String text) {
        if (text == null) return false;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c) && !((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                return true;
            }
        }
        return false;
    }

    protected int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    protected int modInverse(int a, int n) {
        for (int x = 0; x < n; x++) {
            if ((a * x) % n == 1) {
                return x;
            }
        }
        return -1; // hoặc throw exception tùy bạn
    }
}