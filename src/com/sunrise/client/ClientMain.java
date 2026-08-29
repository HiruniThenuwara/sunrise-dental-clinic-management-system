package com.sunrise.client;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.sunrise.controller.ErrorHandler;
import com.sunrise.view.LoginView;

/**
 * Start of the desktop application.
 *
 * Run ServerMain first, and after that run this class.
 */
public class ClientMain {

	public static void main(String[] args) {

		// use the normal windows look, so the program looks like other
		// programs on the computer of the receptionist
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			ErrorHandler.log(e);
		}

		SwingUtilities.invokeLater(() -> {
			try {
				new LoginView().setVisible(true);

			} catch (Exception e) {
				ErrorHandler.log(e);
				JOptionPane.showMessageDialog(null,
						"The program could not start.\n" + e.getMessage()
						+ "\n\nPlease check that the file config/db.properties exists.",
						"Sunrise Dental Clinic", JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}
		});
	}
}
