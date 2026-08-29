package com.sunrise.controller;

import java.awt.Component;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import javax.swing.JOptionPane;

/**
 * All the errors of the system are shown and written from this one class.
 *
 * The staff member sees a short message that is easy to understand, and the
 * technical details are written in the log file for the administrator. The
 * user never sees a java stack trace on the screen.
 */
public class ErrorHandler {

	private static final String LOG_FILE = "logs/clinic.log";

	/** Shows the message of a business error, for example a wrong time slot. */
	public static void show(Component parent, Exception e) {
		log(e);
		JOptionPane.showMessageDialog(parent, e.getMessage(), "Sunrise Dental Clinic",
				JOptionPane.WARNING_MESSAGE);
	}

	/** Shows a simple message without an exception. */
	public static void showMessage(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Sunrise Dental Clinic",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/** Question with Yes and No, used before deleting or exiting. */
	public static boolean confirm(Component parent, String question) {
		int answer = JOptionPane.showConfirmDialog(parent, question, "Sunrise Dental Clinic",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		return answer == JOptionPane.YES_OPTION;
	}

	/** Writes the error into logs/clinic.log with the date and time. */
	public static void log(Exception e) {
		try {
			Files.createDirectories(Paths.get("logs"));

			try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
				writer.println(LocalDateTime.now() + " - " + e.getMessage());

				if (e.getCause() != null) {
					writer.println("    reason: " + e.getCause());
				}
			}
		} catch (IOException ioError) {
			// if even the log file cannot be written, show it on the console
			System.out.println("Could not write the log file: " + ioError.getMessage());
		}
	}
}
