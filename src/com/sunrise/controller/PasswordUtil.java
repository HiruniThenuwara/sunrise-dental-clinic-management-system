package com.sunrise.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Password hashing.
 *
 * The database never keeps the real password. It keeps the SHA-256 value of
 * (salt + password) as a hex text, exactly like the MySQL function
 * SHA2(CONCAT(salt, password), 256) used in seed_data.sql.
 */
public class PasswordUtil {

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	private static final int SALT_LENGTH = 8;

	private static final SecureRandom random = new SecureRandom();

	/** Makes a new random salt for a new user. */
	public static String generateSalt() {
		StringBuilder salt = new StringBuilder();
		for (int i = 0; i < SALT_LENGTH; i++) {
			salt.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
		}
		return salt.toString();
	}

	/** Hashes the password with the salt. */
	public static String hash(String password, String salt) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] bytes = digest.digest((salt + password).getBytes(StandardCharsets.UTF_8));

			// change the bytes into a hex text
			StringBuilder hex = new StringBuilder();
			for (byte b : bytes) {
				hex.append(String.format("%02x", b));
			}
			return hex.toString();

		} catch (NoSuchAlgorithmException e) {
			// SHA-256 is always available in java, so this should never happen
			throw new RuntimeException("SHA-256 is not available", e);
		}
	}

	/** Checks the password the user typed against the value in the database. */
	public static boolean check(String password, String salt, String storedHash) {
		if (password == null || salt == null || storedHash == null) {
			return false;
		}
		return hash(password, salt).equalsIgnoreCase(storedHash);
	}
}
