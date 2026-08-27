package com.sunrise.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.regex.Pattern;

/**
 * All the input validation rules of the system are in this one class,
 * so the windows and the web service check the data in the same way.
 */
public class ValidationUtil {

	// clinic working hours
	public static final LocalTime OPEN_TIME = LocalTime.of(8, 0);
	public static final LocalTime CLOSE_TIME = LocalTime.of(20, 0);

	private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z. ]{3,50}$");
	private static final Pattern CONTACT_PATTERN = Pattern.compile("^0\\d{9}$");
	private static final Pattern NIC_PATTERN = Pattern.compile("^(\\d{9}[vVxX]|\\d{12})$");

	public static boolean isNotEmpty(String value) {
		return value != null && !value.trim().isEmpty();
	}

	/** Patient name: letters and spaces only, 3 to 50 characters. */
	public static boolean isValidName(String name) {
		if (!isNotEmpty(name)) {
			return false;
		}
		return NAME_PATTERN.matcher(name.trim()).matches();
	}

	/** Address: not empty and not longer than 150 characters. */
	public static boolean isValidAddress(String address) {
		return isNotEmpty(address) && address.trim().length() <= 150;
	}

	/** Sri Lankan mobile or land number: 10 digits starting with 0. */
	public static boolean isValidContact(String contact) {
		if (!isNotEmpty(contact)) {
			return false;
		}
		return CONTACT_PATTERN.matcher(contact.trim()).matches();
	}

	/** NIC is optional, but if it is entered it must be the old or the new format. */
	public static boolean isValidNic(String nic) {
		if (!isNotEmpty(nic)) {
			return true;
		}
		return NIC_PATTERN.matcher(nic.trim()).matches();
	}

	/** The appointment cannot be made for a day that is already finished. */
	public static boolean isFutureDate(LocalDate date) {
		if (date == null) {
			return false;
		}
		return !date.isBefore(LocalDate.now());
	}

	/** The clinic works from 08:00 to 20:00. */
	public static boolean isClinicTime(LocalTime time) {
		if (time == null) {
			return false;
		}
		return !time.isBefore(OPEN_TIME) && time.isBefore(CLOSE_TIME);
	}
}
