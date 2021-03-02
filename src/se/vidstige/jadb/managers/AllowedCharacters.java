package se.vidstige.jadb.managers;

import java.nio.charset.StandardCharsets;

public class AllowedCharacters {

    private AllowedCharacters() {
        throw new IllegalStateException("Utility class");
    }

    public static String probeCharacters(String s) {
        for (byte b : s.getBytes(StandardCharsets.UTF_8)) {
            if (b < 32 || b > 126) {
                throw new IllegalArgumentException("Illegal character in argument. Character: " + new String(new byte[]{b}) + " is not allowed.");
            }
        }
        return s;
    }
}
