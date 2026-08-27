package com.sunrise.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Makes the unique appointment number of a visit.
 * Format: APT-yyyyMMdd-NNN , for example APT-20260825-001
 *
 * The number is created by the system and not typed by the user, so two
 * receptionists cannot give the same number to two patients.
 */
public class AppointmentNumberGenerator {

	private static final DateTimeFormatter DATE_PART = DateTimeFormatter.ofPattern("yyyyMMdd");

	/**
	 * @param date     the appointment date
	 * @param sequence how many appointments are already made for that date, plus one
	 */
	public static String generate(LocalDate date, int sequence) {
		return "APT-" + date.format(DATE_PART) + "-" + String.format("%03d", sequence);
	}
}
