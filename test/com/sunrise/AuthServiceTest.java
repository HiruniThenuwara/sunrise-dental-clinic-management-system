package com.sunrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sunrise.controller.ClinicException;
import com.sunrise.controller.PasswordUtil;
import com.sunrise.model.IUserDAO;
import com.sunrise.model.User;
import com.sunrise.server.AuthService;

/**
 * Tests for the login.
 *
 * A fake DAO is used instead of the real UserDAO, so the tests can run
 * without MySQL. This is possible because AuthService uses the IUserDAO
 * interface and not the UserDAO class directly.
 */
public class AuthServiceTest {

	private FakeUserDAO fakeDao;
	private AuthService authService;

	@BeforeEach
	public void setUp() {
		fakeDao = new FakeUserDAO();
		authService = new AuthService(fakeDao);
	}

	@Test
	public void correctUsernameAndPasswordMustLogin() throws ClinicException {
		User user = authService.login("kamal", "kamal123");

		assertNotNull(user);
		assertEquals("kamal", user.getUsername());
		assertEquals("RECEPTIONIST", user.getRole());
	}

	@Test
	public void wrongPasswordMustFail() {
		ClinicException error = assertThrows(ClinicException.class,
				() -> authService.login("kamal", "wrongpassword"));

		assertTrue(error.getMessage().contains("Invalid"));
	}

	@Test
	public void unknownUsernameMustFail() {
		assertThrows(ClinicException.class, () -> authService.login("nobody", "kamal123"));
	}

	@Test
	public void emptyUsernameMustFail() {
		assertThrows(ClinicException.class, () -> authService.login("", "kamal123"));
	}

	@Test
	public void emptyPasswordMustFail() {
		assertThrows(ClinicException.class, () -> authService.login("kamal", ""));
	}

	@Test
	public void failedAttemptsMustBeCounted() {
		try {
			authService.login("kamal", "wrong1");
		} catch (ClinicException e) {
			// expected
		}

		assertEquals(1, fakeDao.getUser().getFailedAttempts());
	}

	@Test
	public void accountMustLockAfterThreeWrongPasswords() {
		for (int i = 0; i < 3; i++) {
			try {
				authService.login("kamal", "wrong");
			} catch (ClinicException e) {
				// expected
			}
		}

		// the fourth try must say the account is locked
		ClinicException error = assertThrows(ClinicException.class,
				() -> authService.login("kamal", "kamal123"));

		assertTrue(error.getMessage().contains("locked"));
	}

	@Test
	public void successfulLoginMustResetTheFailedAttempts() throws ClinicException {
		try {
			authService.login("kamal", "wrong");
		} catch (ClinicException e) {
			// expected
		}

		authService.login("kamal", "kamal123");

		assertEquals(0, fakeDao.getUser().getFailedAttempts());
	}

	/**
	 * A simple fake DAO that keeps one user in memory instead of the database.
	 */
	private static class FakeUserDAO implements IUserDAO {

		private User user;

		FakeUserDAO() {
			String salt = "X3vB9tLr";

			user = new User();
			user.setUserId(2);
			user.setUsername("kamal");
			user.setSalt(salt);
			user.setPasswordHash(PasswordUtil.hash("kamal123", salt));
			user.setFullName("Kamal Silva");
			user.setRole("RECEPTIONIST");
			user.setActive(true);
			user.setFailedAttempts(0);
		}

		User getUser() {
			return user;
		}

		@Override
		public User findByUsername(String username) {
			if (user.getUsername().equals(username)) {
				return user;
			}
			return null;
		}

		@Override
		public void updateFailedAttempts(int userId, int attempts) {
			user.setFailedAttempts(attempts);
		}

		@Override
		public void lockAccount(int userId) {
			user.setActive(false);
		}
	}
}
