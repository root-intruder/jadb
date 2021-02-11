package se.vidstige.jadb.managers;

import java.nio.charset.StandardCharsets;

public class Bash {
    private Bash() {
        throw new IllegalStateException("Utility class");
    }

    public static String quote(String s) {
        for (byte b : s.getBytes(StandardCharsets.UTF_8)) {
            if (b < 32 || b > 126) {
                throw new IllegalArgumentException("Illegal character in argument. Only use UTF-8 characters with indexes between 32 and 126");
            }
        }
        return "'" + s.replace("'", "'\\''") + "'";
    }
}
