package com.sunrise.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import com.sunrise.client.RestClient;
import com.sunrise.controller.AppointmentController;
import com.sunrise.controller.BillingController;
import com.sunrise.controller.ErrorHandler;

/**
 * Function 4 of the brief - calculate the bill and print the receipt.
 *
 * The receptionist types the appointment number, selects the patient type and
 * the payment method, and the server calculates the total with the billing
 * strategy that belongs to that patient type.
 */
public class BillingView extends JFrame {

	private static final long serialVersionUID = 1L;

	private final BillingController billingController = new BillingController();
	private final AppointmentController appointmentController = new AppointmentController();

	private JTextField txtAppointmentNo;
	private JComboBox<String> cmbPatientType;
	private JComboBox<String> cmbPayment;

	private JLabel lblPatient;
	private JLabel lblTreatment;
	private JLabel lblDentist;
	private JLabel lblTreatmentCost;
	private JLabel lblConsultationFee;
	private JLabel lblDiscount;
	private JLabel lblTax;
	private JLabel lblTotal;

	private JButton btnGenerate;
	private JButton btnPrint;

	private Map<String, String> currentBill;
	private Map<String, String> currentAppointment;

	public BillingView(JFrame parent) {
		setTitle("Calculate and Print Bill");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(UITheme.appIcon());
		setSize(700, 700);

		buildScreen();
		UITheme.center(this);
	}

	private void buildScreen() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(UITheme.BACKGROUND);

		main.add(UITheme.header("Calculate and Print Bill",
				"Enter the appointment number and press Calculate"), BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(0, 12));
		center.setBackground(UITheme.BACKGROUND);
		center.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
		center.add(buildTopPanel(), BorderLayout.NORTH);
		center.add(buildBillPanel(), BorderLayout.CENTER);

		main.add(center, BorderLayout.CENTER);
		main.add(buildButtons(), BorderLayout.SOUTH);

		setContentPane(main);
	}

	private JPanel buildTopPanel() {
		JPanel card = UITheme.card();
		card.setLayout(new GridLayout(3, 2, 8, 8));

		card.add(UITheme.label("Appointment number"));
		txtAppointmentNo = UITheme.textField(16);
		card.add(txtAppointmentNo);

		card.add(UITheme.label("Patient type"));
		cmbPatientType = new JComboBox<>(new String[] {
				"STANDARD - normal patient",
				"SENIOR - senior citizen discount",
				"EMERGENCY - extra charge" });
		cmbPatientType.setFont(UITheme.NORMAL_FONT);
		card.add(cmbPatientType);

		card.add(UITheme.label("Payment method"));
		cmbPayment = new JComboBox<>(new String[] { "CASH", "CARD" });
		cmbPayment.setFont(UITheme.NORMAL_FONT);
		card.add(cmbPayment);

		return card;
	}

	private JPanel buildBillPanel() {
		JPanel card = UITheme.card();
		card.setLayout(new BorderLayout(0, 10));

		card.add(UITheme.heading("Bill"), BorderLayout.NORTH);

		JPanel grid = new JPanel(new GridLayout(8, 2, 8, 8));
		grid.setBackground(UITheme.CARD);

		lblPatient = value("-");
		lblDentist = value("-");
		lblTreatment = value("-");
		lblTreatmentCost = value("0.00");
		lblConsultationFee = value("0.00");
		lblDiscount = value("0.00");
		lblTax = value("0.00");

		lblTotal = value("0.00");
		lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
		lblTotal.setForeground(UITheme.ACCENT);

		grid.add(UITheme.label("Patient"));
		grid.add(lblPatient);
		grid.add(UITheme.label("Dentist"));
		grid.add(lblDentist);
		grid.add(UITheme.label("Treatment"));
		grid.add(lblTreatment);
		grid.add(UITheme.label("Treatment charge (Rs.)"));
		grid.add(lblTreatmentCost);
		grid.add(UITheme.label("Consultation fee (Rs.)"));
		grid.add(lblConsultationFee);
		grid.add(UITheme.label("Discount (Rs.)"));
		grid.add(lblDiscount);
		grid.add(UITheme.label("Tax (Rs.)"));
		grid.add(lblTax);
		grid.add(UITheme.heading("TOTAL (Rs.)"));
		grid.add(lblTotal);

		card.add(grid, BorderLayout.CENTER);
		return card;
	}

	private JLabel value(String text) {
		JLabel label = UITheme.label(text);
		label.setFont(UITheme.NORMAL_FONT.deriveFont(Font.BOLD));
		return label;
	}

	private JPanel buildButtons() {
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
		buttons.setBackground(UITheme.BACKGROUND);

		JButton close = UITheme.secondaryButton("Close");
		close.addActionListener(e -> dispose());

		JButton load = UITheme.secondaryButton("Load Appointment");
		load.addActionListener(e -> loadAppointment());

		btnGenerate = UITheme.primaryButton("Calculate and Save Bill");
		btnGenerate.addActionListener(e -> generateBill());

		btnPrint = UITheme.primaryButton("Print Receipt");
		btnPrint.setEnabled(false);
		btnPrint.addActionListener(e -> printReceipt());

		buttons.add(close);
		buttons.add(load);
		buttons.add(btnGenerate);
		buttons.add(btnPrint);
		return buttons;
	}

	// ---------- actions ----------

	/** Shows the appointment details before the bill is created. */
	private void loadAppointment() {
		final String appointmentNo = txtAppointmentNo.getText().trim();

		new SwingWorker<Map<String, String>, Void>() {

			@Override
			protected Map<String, String> doInBackground() throws Exception {
				return appointmentController.search(appointmentNo);
			}

			@Override
			protected void done() {
				try {
					currentAppointment = get();

					lblPatient.setText(currentAppointment.get("patientName"));
					lblDentist.setText(currentAppointment.get("dentistName"));
					lblTreatment.setText(currentAppointment.get("treatmentName"));
					lblTreatmentCost.setText(currentAppointment.get("treatmentCost"));
					lblConsultationFee.setText(currentAppointment.get("consultationFee"));
					lblDiscount.setText("0.00");
					lblTax.setText("0.00");
					lblTotal.setText("0.00");

				} catch (Exception e) {
					Throwable cause = e.getCause() == null ? e : e.getCause();
					ErrorHandler.show(BillingView.this, new Exception(cause.getMessage()));
				}
			}
		}.execute();
	}

	private void generateBill() {
		final String appointmentNo = txtAppointmentNo.getText().trim();
		final String patientType = String.valueOf(cmbPatientType.getSelectedItem()).split(" ")[0];
		final String payment = String.valueOf(cmbPayment.getSelectedItem());

		btnGenerate.setEnabled(false);
		btnGenerate.setText("Calculating...");

		new SwingWorker<Map<String, String>, Void>() {

			@Override
			protected Map<String, String> doInBackground() throws Exception {
				if (currentAppointment == null) {
					currentAppointment = appointmentController.search(appointmentNo);
				}
				return billingController.generateBill(appointmentNo, patientType, payment);
			}

			@Override
			protected void done() {
				btnGenerate.setEnabled(true);
				btnGenerate.setText("Calculate and Save Bill");

				try {
					currentBill = get();
					showBill(currentBill);
					btnPrint.setEnabled(true);

					ErrorHandler.showMessage(BillingView.this,
							"The bill is saved.\nTotal amount: Rs. " + currentBill.get("totalAmount"));

				} catch (Exception e) {
					Throwable cause = e.getCause() == null ? e : e.getCause();
					ErrorHandler.show(BillingView.this, new Exception(cause.getMessage()));
				}
			}
		}.execute();
	}

	public void showBill(Map<String, String> bill) {
		lblTreatmentCost.setText(bill.get("treatmentCost"));
		lblConsultationFee.setText(bill.get("consultationFee"));
		lblDiscount.setText(bill.get("discount"));
		lblTax.setText(bill.get("tax"));
		lblTotal.setText(bill.get("totalAmount"));
	}

	private void printReceipt() {
		if (currentBill == null) {
			ErrorHandler.showMessage(this, "Please calculate the bill first.");
			return;
		}

		Map<String, String> appointment = currentAppointment == null
				? new LinkedHashMap<>() : currentAppointment;

		ReceiptPrinter.preview(this, currentBill, appointment, RestClient.getInstance().getFullName());
	}
}
