package com.foodrescue.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {

    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 600_000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private PasswordUtil() {
        // Utility class
    }

    /**
     * Creates a secure PBKDF2 password hash.
     *
     * Stored format:
     * iterations:salt:hash
     */
    public static String hashPassword(String password) {

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty"
            );
        }

        byte[] salt = new byte[SALT_LENGTH];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(salt);

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                KEY_LENGTH
        );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(ALGORITHM);

            byte[] hash =
                    factory.generateSecret(spec).getEncoded();

            return ITERATIONS + ":"
                    + Base64.getEncoder().encodeToString(salt)
                    + ":"
                    + Base64.getEncoder().encodeToString(hash);

        } catch (NoSuchAlgorithmException |
                 InvalidKeySpecException e) {

            throw new IllegalStateException(
                    "Password hashing failed",
                    e
            );

        } finally {

            spec.clearPassword();
        }
    }

    /**
     * Verifies a plain-text password against
     * the stored password hash.
     */
    public static boolean verifyPassword(
            String password,
            String storedPassword) {

        if (password == null || storedPassword == null) {
            return false;
        }

        try {

            String[] parts = storedPassword.split(":");

            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);

            byte[] salt =
                    Base64.getDecoder().decode(parts[1]);

            byte[] expectedHash =
                    Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(
                    password.toCharArray(),
                    salt,
                    iterations,
                    expectedHash.length * 8
            );

            try {

                SecretKeyFactory factory =
                        SecretKeyFactory.getInstance(ALGORITHM);

                byte[] actualHash =
                        factory.generateSecret(spec).getEncoded();

                return constantTimeEquals(
                        actualHash,
                        expectedHash
                );

            } finally {

                spec.clearPassword();
            }

        } catch (IllegalArgumentException |
                 NoSuchAlgorithmException |
                 InvalidKeySpecException e) {

            return false;
        }
    }

    /**
     * Compares two byte arrays in constant time.
     */
    private static boolean constantTimeEquals(
            byte[] first,
            byte[] second) {

        if (first == null || second == null) {
            return false;
        }

        if (first.length != second.length) {
            return false;
        }

        int result = 0;

        for (int i = 0; i < first.length; i++) {
            result |= first[i] ^ second[i];
        }

        return result == 0;
    }
}