package model.hash;

import java.security.*;

public class ListAllHashes {
    public static void main(String[] args) {
        System.out.println("=== Tất cả thuật toán băm được hỗ trợ ===");

        for (String algo : Security.getAlgorithms("MessageDigest")) {
            System.out.println(" - " + algo);
        }
    }
}
