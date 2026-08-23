package com.sunrise.controller;

/**
 * One exception class for the whole system.
 * DAO and service classes throw this instead of showing SQL errors to the user.
 */
public class ClinicException extends Exception {

	private static final long serialVersionUID = 1L;

	public ClinicException(String message) {
		super(message);
	}

	public ClinicException(String message, Throwable cause) {
		super(message, cause);
	}
}
