package com.sunrise.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.sunrise.controller.ErrorHandler;

/**
 * Function 4 of the brief - print the patient bill / receipt.
 *
 * The receipt is built as simple text and shown in a preview window. The
 * Print button uses the java print service, so the receipt can be printed on
 * the clinic printer or saved as a PDF file.
 */
public class ReceiptPrinter {

	private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	/** Shows the receipt with a Print button. */
	public static void preview(JFrame parent, Map<String, String> bill,
			Map<String, String> appointment, String staffName) {

		String text = buildReceiptText(bill, appointment, staffName);

		JTextArea area = new JTextArea(text);
		area.setFont(UITheme.MONO_FONT);
		area.setEditable(false);
		area.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

		JDialog dialog = new JDialog(parent, "Patient Receipt", true);
		dialog.setSize(460, 620);
		dialog.setLayout(new BorderLayout());
		dialog.add(new JScrollPane(area), BorderLayout.CENTER);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
		buttons.setBackground(UITheme.BACKGROUND);

		JButton close = UITheme.secondaryButton("Close");
		close.addActionListener(e -> dialog.dispose());

		JButton print = UITheme.primaryButton("Print Receipt");
		print.addActionListener(e -> print(area, parent));

		buttons.add(close);
		buttons.add(print);
		dialog.add(buttons, BorderLayout.SOUTH);

		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}

	private static void print(JTextArea area, JFrame parent) {
		try {
			boolean printed = area.print();

			if (printed) {
				ErrorHandler.showMessage(parent, "The receipt was sent to the printer.");
			}
		} catch (Exception e) {
			ErrorHandler.show(parent, new Exception(
					"The receipt could not be printed. Please check the printer.\n" + e.getMessage()));
		}
	}

	/** Builds the text of the receipt with the clinic name and the totals. */
	public static String buildReceiptText(Map<String, String> bill,
			Map<String, String> appointment, String staffName) {

		StringBuilder r = new StringBuilder();

		r.append(line());
		r.append(center("SUNRISE DENTAL CLINIC"));
		r.append(center("No 120, Galle Road, Colombo 03"));
		r.append(center("Tel: 011 2 345 678"));
		r.append(line());
		r.append(center("PATIENT BILL / RECEIPT"));
		r.append(line());
		r.append("\n");

		r.append(pair("Receipt No", "BILL-" + value(bill.get("billId"))));
		r.append(pair("Appointment No", value(bill.get("appointmentNo"))));
		r.append(pair("Date", LocalDateTime.now().format(STAMP)));
		r.append(pair("Cashier", staffName == null ? "-" : staffName));
		r.append("\n");

		if (appointment != null) {
			r.append(pair("Patient", value(appointment.get("patientName"))));
			r.append(pair("Contact", value(appointment.get("contactNumber"))));
			r.append(pair("Dentist", value(appointment.get("dentistName"))));
			r.append(pair("Treatment", value(appointment.get("treatmentName"))));
			r.append(pair("Visit date", value(appointment.get("appointmentDate")) + " "
					+ value(appointment.get("appointmentTime"))));
			r.append("\n");
		}

		r.append(line());
		r.append(money("Treatment charge", bill.get("treatmentCost")));
		r.append(money("Consultation fee", bill.get("consultationFee")));
		r.append(money("Discount", "-" + value(bill.get("discount"))));
		r.append(money("Tax", bill.get("tax")));
		r.append(line());
		r.append(money("TOTAL (LKR)", bill.get("totalAmount")));
		r.append(line());
		r.append("\n");

		r.append(pair("Payment method", value(bill.get("paymentMethod"))));
		if (bill.get("billingType") != null) {
			r.append(pair("Billing type", bill.get("billingType")));
		}

		r.append("\n");
		r.append(center("Thank you and get well soon"));
		r.append(center("Please keep this receipt"));
		r.append(line());

		return r.toString();
	}

	private static String line() {
		return "========================================\n";
	}

	private static String center(String text) {
		int width = 40;
		int space = Math.max(0, (width - text.length()) / 2);
		return " ".repeat(space) + text + "\n";
	}

	private static String pair(String name, String value) {
		return String.format("%-18s: %s%n", name, value);
	}

	private static String money(String name, String amount) {
		return String.format("%-24s %14s%n", name, value(amount));
	}

	private static String value(String text) {
		return text == null ? "-" : text;
	}
}
