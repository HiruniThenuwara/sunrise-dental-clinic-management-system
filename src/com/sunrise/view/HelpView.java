package com.sunrise.view;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

/**
 * Function 5 of the brief - the help section.
 *
 * One tab for every function of the system, with short steps written in
 * simple English for a new staff member of the clinic.
 */
public class HelpView extends JFrame {

	private static final long serialVersionUID = 1L;

	public HelpView(JFrame parent) {
		setTitle("Help - How to use the system");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(UITheme.appIcon());
		setSize(720, 620);

		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(UITheme.BACKGROUND);
		main.add(UITheme.header("Help", "Step by step instructions for the clinic staff"),
				BorderLayout.NORTH);

		JTabbedPane tabs = new JTabbedPane();
		tabs.setFont(UITheme.NORMAL_FONT);

		tabs.addTab("1. Login", helpPanel(LOGIN_HELP));
		tabs.addTab("2. New appointment", helpPanel(REGISTER_HELP));
		tabs.addTab("3. Search", helpPanel(SEARCH_HELP));
		tabs.addTab("4. Billing", helpPanel(BILLING_HELP));
		tabs.addTab("5. Reports", helpPanel(REPORT_HELP));
		tabs.addTab("6. Problems", helpPanel(PROBLEM_HELP));

		main.add(tabs, BorderLayout.CENTER);
		setContentPane(main);

		UITheme.center(this);
	}

	private JScrollPane helpPanel(String text) {
		JTextArea area = new JTextArea(text);
		area.setFont(UITheme.NORMAL_FONT);
		area.setEditable(false);
		area.setLineWrap(true);
		area.setWrapStyleWord(true);
		area.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
		area.setBackground(UITheme.CARD);
		return new JScrollPane(area);
	}

	private static final String LOGIN_HELP =
			"HOW TO LOGIN\n\n"
			+ "1. Ask the administrator for your username and password.\n"
			+ "2. Type your username in the first box.\n"
			+ "3. Type your password in the second box.\n"
			+ "   You can tick 'Show password' if you want to check what you typed.\n"
			+ "4. Press the Login button, or press the Enter key.\n\n"
			+ "IMPORTANT\n"
			+ "- After three wrong passwords the account is locked for safety.\n"
			+ "  Please ask the administrator to open it again.\n"
			+ "- Never give your password to another person. The system writes\n"
			+ "  your name on every appointment and every bill that you create.\n"
			+ "- Press Logout when you leave the counter.";

	private static final String REGISTER_HELP =
			"HOW TO REGISTER A NEW APPOINTMENT\n\n"
			+ "1. In the main menu press '1. Register New Appointment'.\n"
			+ "2. Type the patient name (letters only).\n"
			+ "3. Type the address of the patient.\n"
			+ "4. Type the contact number: 10 digits starting with 0,\n"
			+ "   for example 0771234567.\n"
			+ "5. Select the dentist from the list.\n"
			+ "6. Select the treatment type. The price is shown at the bottom.\n"
			+ "7. Select the date and the time of the visit.\n"
			+ "8. Press 'Save Appointment'.\n\n"
			+ "The system gives an appointment number, for example\n"
			+ "APT-20260825-001. Please write this number on the patient card,\n"
			+ "because the number is needed for the search and for the bill.\n\n"
			+ "IF THE SYSTEM SAYS 'ALREADY BOOKED'\n"
			+ "That dentist has another patient at the same time. Select another\n"
			+ "time or another dentist. This message protects the clinic from\n"
			+ "double booking.";

	private static final String SEARCH_HELP =
			"HOW TO FIND AN APPOINTMENT\n\n"
			+ "1. In the main menu press '2. Search Appointment'.\n"
			+ "2. Type the appointment number and press Search.\n"
			+ "3. All the details of the patient and the appointment are shown.\n\n"
			+ "IF THE PATIENT FORGOT THE NUMBER\n"
			+ "1. Select the date at the bottom of the window and press Show.\n"
			+ "2. All the appointments of that day are shown in the table.\n"
			+ "3. Click the row of the patient and the details appear above.\n\n"
			+ "TO CANCEL AN APPOINTMENT\n"
			+ "Find the appointment first, then press 'Cancel this appointment'\n"
			+ "and answer Yes. The appointment is not deleted, the status is\n"
			+ "changed to CANCELLED, so the clinic keeps the history.";

	private static final String BILLING_HELP =
			"HOW TO MAKE THE BILL AND PRINT THE RECEIPT\n\n"
			+ "1. In the main menu press '3. Calculate and Print Bill'.\n"
			+ "2. Type the appointment number and press 'Load Appointment'\n"
			+ "   to check that it is the correct patient.\n"
			+ "3. Select the patient type:\n"
			+ "   - STANDARD  : normal patient\n"
			+ "   - SENIOR    : senior citizen, a discount is given\n"
			+ "   - EMERGENCY : patient without an appointment, extra charge\n"
			+ "4. Select CASH or CARD.\n"
			+ "5. Press 'Calculate and Save Bill'. The total is shown in green.\n"
			+ "6. Press 'Print Receipt'. Check the preview and press Print.\n\n"
			+ "HOW THE TOTAL IS CALCULATED\n"
			+ "treatment charge + consultation fee - discount + tax = total\n\n"
			+ "One appointment can have only one bill. If the bill was made\n"
			+ "already, the system will say it.";

	private static final String REPORT_HELP =
			"HOW TO USE THE REPORTS\n\n"
			+ "1. In the main menu press '4. Reports'.\n"
			+ "2. Select the report from the list:\n\n"
			+ "   - Daily appointment list : every patient of one day\n"
			+ "   - Appointments by dentist: how busy each dentist is\n"
			+ "   - Revenue summary        : money collected between two dates\n"
			+ "   - Most popular treatments: which treatment is taken the most\n\n"
			+ "3. Select the dates and press Show.\n"
			+ "4. Click a column header to sort the table.\n"
			+ "5. Press 'Export to CSV' to save the report and open it in Excel.";

	private static final String PROBLEM_HELP =
			"COMMON PROBLEMS AND WHAT TO DO\n\n"
			+ "'Cannot connect to the clinic server'\n"
			+ "The server program is not running. Ask the administrator to start\n"
			+ "ServerMain on the clinic computer, then try again.\n\n"
			+ "'Please login again, your session is finished'\n"
			+ "The system logs out after 30 minutes without work. Login again.\n\n"
			+ "'This account is locked'\n"
			+ "Three wrong passwords were entered. The administrator can open it.\n\n"
			+ "'This dentist is already booked'\n"
			+ "Select another time or another dentist.\n\n"
			+ "'The contact number must have 10 digits'\n"
			+ "Type the number without spaces and without the country code,\n"
			+ "for example 0712345678.\n\n"
			+ "If a problem stays, tell the administrator. The system writes\n"
			+ "every error in the file logs/clinic.log.";
}
