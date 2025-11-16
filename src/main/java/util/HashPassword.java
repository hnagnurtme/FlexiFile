package util;

import org.mindrot.jbcrypt.BCrypt;

public class HashPassword {
    private static final int SALT_ROUNDS = 10;

    public static String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt(SALT_ROUNDS);
        String hashedPassword = BCrypt.hashpw(plainPassword, salt);
        return hashedPassword;
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$")) {
            throw new IllegalArgumentException("Invalid hashed password format");
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
