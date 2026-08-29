package com.sunrise.controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Observer design pattern - the subject.
 *
 * When a receptionist saves or cancels an appointment, every open window that
 * registered here is told about it. So the dashboard counter and the
 * appointment table refresh by themselves and the staff does not have to
 * press a refresh button.
 */
public class AppointmentSubject {

	private static final List<AppointmentObserver> observers = new ArrayList<>();

	public static void register(AppointmentObserver observer) {
		if (observer != null && !observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public static void remove(AppointmentObserver observer) {
		observers.remove(observer);
	}

	public static void notifyObservers(String appointmentNo, String action) {
		// a copy is used, because a window can close itself while it is told
		for (AppointmentObserver observer : new ArrayList<>(observers)) {
			try {
				observer.appointmentChanged(appointmentNo, action);
			} catch (Exception e) {
				ErrorHandler.log(e);
			}
		}
	}

	public static int observerCount() {
		return observers.size();
	}
}
