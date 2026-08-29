package com.sunrise.controller;

/**
 * Observer design pattern.
 *
 * A window that must know when an appointment is added, cancelled or billed
 * implements this interface and registers itself in AppointmentSubject.
 */
public interface AppointmentObserver {

	/**
	 * @param appointmentNo the appointment that changed
	 * @param action        SAVED, CANCELLED or BILLED
	 */
	void appointmentChanged(String appointmentNo, String action);
}
