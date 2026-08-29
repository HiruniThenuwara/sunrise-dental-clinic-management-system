package com.sunrise.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.sunrise.controller.AppointmentController;
import com.sunrise.controller.AppointmentObserver;
import com.sunrise.controller.AppointmentSubject;
import com.sunrise.controller.ClinicException;
import com.sunrise.controller.ErrorHandler;
import com.sunrise.controller.LoginController;

/**
 * The main menu (dashboard) with the six functions of the brief.
 *
 * This window is also an observer: when another window saves or cancels an
 * appointment, the counter of today is refreshed by itself.
 */
public class MainMenuView extends JFrame implements AppointmentObserver {

	private static final long serialVersionUID = 1L;

	private final AppointmentController appointmentController = new AppointmentController();
	private final LoginController loginController = new LoginController();

	private final String staffName;

	private JLabel lblTodayCount;

	public MainMenuView(String staffName) {
		this.staffName = staffName == null ? "Staff" : staffName;

		setTitle("Sunrise Dental Clinic - Main Menu");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setIconImage(UITheme.appIcon());
		setSize(820, 600);

		buildScreen();
		UITheme.center(this);

		// this window listens to the appointment changes (Observer pattern)
		AppointmentSubject.register(this);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				exitSystem();
			}
		});

		refreshCount();
	}

	private void buildScreen() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(UITheme.BACKGROUND);

		main.add(UITheme.header("Sunrise Dental Clinic", "Logged in as " + staffName),
				BorderLayout.NORTH);
		main.add(buildCenter(), BorderLayout.CENTER);
		main.add(buildFooter(), BorderLayout.SOUTH);

		setContentPane(main);
	}

	private JPanel buildCenter() {
		JPanel center = new JPanel();
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setBackground(UITheme.BACKGROUND);
		center.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

		center.add(buildTodayCard());
		center.add(javax.swing.Box.createVerticalStrut(16));
		center.add(buildMenuButtons());

		return center;
	}

	private JPanel buildTodayCard() {
		JPanel card = UITheme.card();
		card.setLayout(new BorderLayout());
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.setBackground(UITheme.CARD);
		left.add(UITheme.heading("Appointments today"));
		left.add(UITheme.hint("The number is updated when a new appointment is registered"));

		lblTodayCount = new JLabel("0");
		lblTodayCount.setFont(UITheme.TITLE_FONT.deriveFont(34f));
		lblTodayCount.setForeground(UITheme.ACCENT);

		card.add(left, BorderLayout.WEST);
		card.add(lblTodayCount, BorderLayout.EAST);
		return card;
	}

	private JPanel buildMenuButtons() {
		JPanel grid = new JPanel(new GridLayout(3, 2, 14, 14));
		grid.setBackground(UITheme.BACKGROUND);

		grid.add(menuButton("1.  Register New Appointment",
				"Enter the patient details and book a dentist", e -> openRegistration()));

		grid.add(menuButton("2.  Search Appointment",
				"Find an appointment with the appointment number", e -> openSearch()));

		grid.add(menuButton("3.  Calculate and Print Bill",
				"Create the bill and print the receipt", e -> openBilling()));

		grid.add(menuButton("4.  Reports",
				"Daily list, dentists, revenue and treatments", e -> openReports()));

		grid.add(menuButton("5.  Help",
				"Step by step instructions for new staff", e -> openHelp()));

		JButton exit = menuButton("6.  Exit System",
				"Close the session and the application", e -> exitSystem());
		exit.setBackground(new Color(252, 240, 240));
		grid.add(exit);

		return grid;
	}

	private JButton menuButton(String title, String description,
			java.awt.event.ActionListener action) {

		JButton button = new JButton("<html><div style='text-align:left;'>"
				+ "<span style='font-size:12pt;'><b>" + title + "</b></span><br>"
				+ "<span style='color:#6E7882;'>" + description + "</span></div></html>");

		button.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
		button.setFont(UITheme.NORMAL_FONT);
		button.setBackground(UITheme.CARD);
		button.setFocusPainted(false);
		button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(UITheme.BORDER, 1),
				BorderFactory.createEmptyBorder(14, 16, 14, 16)));
		button.addActionListener(action);
		return button;
	}

	private JPanel buildFooter() {
		JPanel footer = new JPanel(new BorderLayout());
		footer.setBackground(UITheme.BACKGROUND);
		footer.setBorder(BorderFactory.createEmptyBorder(0, 22, 14, 22));

		footer.add(UITheme.hint("Sunrise Dental Clinic, Colombo"), BorderLayout.WEST);

		JButton logout = UITheme.secondaryButton("Logout");
		logout.addActionListener(e -> logout());
		footer.add(logout, BorderLayout.EAST);

		return footer;
	}

	// ---------- actions ----------

	private void openRegistration() {
		new AppointmentRegistrationView(this).setVisible(true);
	}

	private void openSearch() {
		new SearchAppointmentView(this).setVisible(true);
	}

	private void openBilling() {
		new BillingView(this).setVisible(true);
	}

	private void openReports() {
		new ReportView(this).setVisible(true);
	}

	private void openHelp() {
		new HelpView(this).setVisible(true);
	}

	/** Function 6 of the brief - safe exit. */
	private void exitSystem() {
		if (ErrorHandler.confirm(this, "Do you want to close the system?")) {
			loginController.logout();
			AppointmentSubject.remove(this);
			dispose();
			System.exit(0);
		}
	}

	private void logout() {
		if (ErrorHandler.confirm(this, "Do you want to logout?")) {
			loginController.logout();
			AppointmentSubject.remove(this);

			LoginView login = new LoginView();
			login.setVisible(true);
			dispose();
		}
	}

	/** Reads the number of today from the web service. */
	public void refreshCount() {
		new javax.swing.SwingWorker<String, Void>() {

			@Override
			protected String doInBackground() throws Exception {
				return appointmentController.todayCount();
			}

			@Override
			protected void done() {
				try {
					lblTodayCount.setText(get());
				} catch (Exception e) {
					lblTodayCount.setText("-");
					Throwable cause = e.getCause() == null ? e : e.getCause();
					ErrorHandler.log(new ClinicException(cause.getMessage()));
				}
			}
		}.execute();
	}

	/** Observer pattern - another window changed an appointment. */
	@Override
	public void appointmentChanged(String appointmentNo, String action) {
		SwingUtilities.invokeLater(this::refreshCount);
	}
}
