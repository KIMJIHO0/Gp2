package util;

import java.security.MessageDigest;
import java.util.Base64;

public final class Passwords {
    public static String hash(String pw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder()
                    .encodeToString(md.digest(pw.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static boolean matches(String raw, String encoded) {
        return hash(raw).equals(encoded);
    }
}
