package com.sunrise.server;

import com.sunrise.controller.ClinicException;
import com.sunrise.controller.PasswordUtil;
import com.sunrise.model.IUserDAO;
import com.sunrise.model.User;
import com.sunrise.model.UserDAO;

/**
 * Login logic of the system.
 *
 * Only authorised staff can use the system. After 3 wrong passwords the
 * account is locked and the administrator has to open it again.
 */
public class AuthService {

	public static final int MAX_ATTEMPTS = 3;

	private IUserDAO userDAO;

	public AuthService() {
		this.userDAO = new UserDAO();
	}

	// this constructor is used by the unit tests with a fake DAO
	public AuthService(IUserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * Checks the username and the password.
	 *
	 * @return the logged in user
	 * @throws ClinicException if the login is not correct
	 */
	public User login(String username, String password) throws ClinicException {

		if (username == null || username.trim().isEmpty()) {
			throw new ClinicException("Please enter the username.");
		}

		if (password == null || password.isEmpty()) {
			throw new ClinicException("Please enter the password.");
		}

		User user = userDAO.findByUsername(username.trim());

		if (user == null) {
			throw new ClinicException("Invalid username or password.");
		}

		if (!user.isActive()) {
			throw new ClinicException("This account is locked. Please contact the administrator.");
		}

		if (!PasswordUtil.check(password, user.getSalt(), user.getPasswordHash())) {

			int attempts = user.getFailedAttempts() + 1;
			userDAO.updateFailedAttempts(user.getUserId(), attempts);

			if (attempts >= MAX_ATTEMPTS) {
				userDAO.lockAccount(user.getUserId());
				throw new ClinicException("Too many wrong passwords. "
						+ "This account is locked. Please contact the administrator.");
			}

			throw new ClinicException("Invalid username or password. "
					+ (MAX_ATTEMPTS - attempts) + " attempt(s) left.");
		}

		// correct password, start again from zero
		if (user.getFailedAttempts() > 0) {
			userDAO.updateFailedAttempts(user.getUserId(), 0);
		}

		return user;
	}
}
