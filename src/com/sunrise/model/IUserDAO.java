package com.sunrise.model;

import com.sunrise.controller.ClinicException;

/**
 * DAO pattern - the interface of the user table.
 *
 * AuthService works with this interface, so the database class can be
 * changed later (or replaced by a fake class in the unit tests) without
 * changing the login logic.
 */
public interface IUserDAO {

	/** Finds a staff member by the username, or null if there is no such user. */
	User findByUsername(String username) throws ClinicException;

	/** Saves how many times the user typed a wrong password. */
	void updateFailedAttempts(int userId, int attempts) throws ClinicException;

	/** Blocks the account after too many wrong passwords. */
	void lockAccount(int userId) throws ClinicException;
}
