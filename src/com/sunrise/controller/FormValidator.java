package com.sunrise.controller;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

/**
 * Checks the fields of a window and marks the wrong field with a red border.
 *
 * The same checking code was written in every window at the beginning. It was
 * moved into this one class so the windows stay short and every window shows
 * the same messages.
 */
public class FormValidator {

	private static final Color ERROR_COLOR = new Color(200, 60, 60);

	private final StringBuilder errors = new StringBuilder();

	/** Checks one field and keeps the message when it is wrong. */
	public FormValidator check(boolean valid, JComponent field, String message) {
		if (valid) {
			clearMark(field);
		} else {
			mark(field);
			errors.append("- ").append(message).append("\n");
		}
		return this;
	}

	public FormValidator checkName(JTextField field, String message) {
		return check(ValidationUtil.isValidName(field.getText()), field, message);
	}

	public FormValidator checkAddress(JTextField field, String message) {
		return check(ValidationUtil.isValidAddress(field.getText()), field, message);
	}

	public FormValidator checkContact(JTextField field, String message) {
		return check(ValidationUtil.isValidContact(field.getText()), field, message);
	}

	public FormValidator checkNotEmpty(JTextField field, String message) {
		return check(ValidationUtil.isNotEmpty(field.getText()), field, message);
	}

	public boolean isValid() {
		return errors.length() == 0;
	}

	/** All the messages together, shown in one dialog box. */
	public String getMessage() {
		return "Please correct the following:\n\n" + errors.toString();
	}

	private void mark(JComponent field) {
		field.putClientProperty("normalBorder", field.getBorder());
		field.setBorder(new LineBorder(ERROR_COLOR, 2));
	}

	private void clearMark(JComponent field) {
		Object border = field.getClientProperty("normalBorder");
		if (border instanceof Border) {
			field.setBorder((Border) border);
		}
	}
}
