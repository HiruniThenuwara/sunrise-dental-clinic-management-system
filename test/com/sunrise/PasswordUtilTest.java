package com.sunrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.sunrise.controller.PasswordUtil;

/**
 * Tests for the password hashing.
 */
public class PasswordUtilTest {

	@Test
	public void sameSaltAndPasswordMustGiveSameHash() {
		String hash1 = PasswordUtil.hash("admin123", "K7dQ2mZp");
		String hash2 = PasswordUtil.hash("admin123", "K7dQ2mZp");

		assertEquals(hash1, hash2);
	}

	@Test
	public void hashMustBe64CharactersLong() {
		// SHA-256 in hex is always 64 characters
		assertEquals(64, PasswordUtil.hash("admin123", "K7dQ2mZp").length());
	}

	@Test
	public void differentSaltMustGiveDifferentHash() {
		String hash1 = PasswordUtil.hash("admin123", "K7dQ2mZp");
		String hash2 = PasswordUtil.hash("admin123", "X3vB9tLr");

		assertNotEquals(hash1, hash2);
	}

	@Test
	public void passwordMustNotBeStoredAsPlainText() {
		String hash = PasswordUtil.hash("admin123", "K7dQ2mZp");

		assertFalse(hash.contains("admin123"));
	}

	@Test
	public void checkMustAcceptTheCorrectPassword() {
		String salt = PasswordUtil.generateSalt();
		String hash = PasswordUtil.hash("kamal123", salt);

		assertTrue(PasswordUtil.check("kamal123", salt, hash));
	}

	@Test
	public void checkMustRejectTheWrongPassword() {
		String salt = PasswordUtil.generateSalt();
		String hash = PasswordUtil.hash("kamal123", salt);

		assertFalse(PasswordUtil.check("kamal124", salt, hash));
	}

	@Test
	public void generateSaltMustGiveANewValueEveryTime() {
		assertNotEquals(PasswordUtil.generateSalt(), PasswordUtil.generateSalt());
	}
}
