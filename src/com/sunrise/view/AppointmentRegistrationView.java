package com.sunrise.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingWorker;

import com.sunrise.controller.AppointmentController;
import com.sunrise.controller.ErrorHandler;
import com.sunrise.controller.FormValidator;

/**
 * Function 2 of the brief - register a new appointment.
 *
 * The window collects the patient and appointment details, checks every field
 * and sends them to the web service through AppointmentController.
 */
public class AppointmentRegistrationView extends JFrame {

	private static final long serialVersionUID = 1L;

	private final AppointmentController controller = new AppointmentController();
	private final MainMenuView parent;

	private JTextField txtPatientName;
	private JTextField txtAddress;
	private JTextField txtContact;
	private JComboBox<ComboItem> cmbDentist;
	private JComboBox<ComboItem> cmbTreatment;
	private JSpinner spnDate;
	private JComboBox<String> cmbTime;
	private JLabel lblFee;
	private JButton btnSave;

	public AppointmentRegistrationView(MainMenuView parent) {
		this.parent = parent;

		setTitle("Register New Appointment");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(UITheme.appIcon());
		setSize(660, 720);

		buildScreen();
		UITheme.center(this);

		loadComboBoxes();
	}

	private void buildScreen() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(UITheme.BACKGROUND);

		main.add(UITheme.header("Register New Appointment",
				"All the fields with * must be filled"), BorderLayout.NORTH);
		main.add(buildForm(), BorderLayout.CENTER);
		main.add(buildButtons(), BorderLayout.SOUTH);

		setContentPane(main);
	}

	private JPanel buildForm() {
		JPanel card = UITheme.card();
		card.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(7, 6, 7, 6);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;

		card.add(UITheme.heading("Patient details"), c);

		c.gridy++;
		card.add(UITheme.label("Patient name *"), c);
		c.gridx = 1;
		txtPatientName = UITheme.textField(22);
		card.add(txtPatientName, c);

		c.gridx = 0;
		c.gridy++;
		card.add(UITheme.label("Address *"), c);
		c.gridx = 1;
		txtAddress = UITheme.textField(22);
		card.add(txtAddress, c);

		c.gridx = 0;
		c.gridy++;
		card.add(UITheme.label("Contact number *"), c);
		c.gridx = 1;
		txtContact = UITheme.textField(22);
		card.add(txtContact, c);

		c.gridx = 1;
		c.gridy++;
		card.add(UITheme.hint("10 digits starting with 0, for example 0771234567"), c);

		c.gridx = 0;
		c.gridy++;
		card.add(UITheme.heading("Appointment details"), c);

		c.gridy++;
		card.add(UITheme.label("Dentist *"), c);
		c.gridx = 1;
		cmbDentist = new JComboBox<>();
		cmbDentist.setFont(UITheme.NORMAL_FONT);
		cmbDentist.addActionListener(e -> showFee());
		card.add(cmbDentist, c);

		c.gridx = 0;
		c.gridy++;
		card.add(UITheme.label("Treatment type *"), c);
		c.gridx = 1;
		cmbTreatment = new JComboBox<>();
		cmbTreatment.setFont(UITheme.NORMAL_FONT);
		cmbTreatment.addActionListener(e -> showFee());
		card.add(cmbTreatment, c);

		c.gridx = 0;
		c.gridy++;
		card.add(UITheme.label("Appointment date *"), c);
		c.gridx = 1;
		spnDate = new JSpinner(new SpinnerDateModel());
		spnDate.setEditor(new JSpinner.DateEditor(spnDate, "yyyy-MM-dd"));
		spnDate.setFont(UITheme.NORMAL_FONT);
		card.add(spnDate, c);

		c.gridx = 0;
		c.gridy++;
		card.add(UITheme.label("Appointment time *"), c);
		c.gridx = 1;
		cmbTime = new JComboBox<>(timeSlots());
		cmbTime.setFont(UITheme.NORMAL_FONT);
		card.add(cmbTime, c);

		c.gridx = 1;
		c.gridy++;
		card.add(UITheme.hint("The clinic is open from 08:00 to 20:00"), c);

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;
		lblFee = UITheme.label(" ");
		lblFee.setForeground(UITheme.ACCENT);
		lblFee.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));
		card.add(lblFee, c);

		JPanel holder = new JPanel(new GridBagLayout());
		holder.setBackground(UITheme.BACKGROUND);
		holder.setBorder(BorderFactory.createEmptyBorder(14, 14, 6, 14));
		holder.add(card);
		return holder;
	}

	private JPanel buildButtons() {
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
		buttons.setBackground(UITheme.BACKGROUND);

		JButton close = UITheme.secondaryButton("Close");
		close.addActionListener(e -> dispose());

		JButton clear = UITheme.secondaryButton("Clear");
		clear.addActionListener(e -> clearForm());

		btnSave = UITheme.primaryButton("Save Appointment");
		btnSave.addActionListener(e -> save());

		buttons.add(close);
		buttons.add(clear);
		buttons.add(btnSave);
		return buttons;
	}

	/** The clinic works in slots of 30 minutes. */
	private String[] timeSlots() {
		List<String> slots = new ArrayList<>();
		LocalTime time = LocalTime.of(8, 0);

		while (time.isBefore(LocalTime.of(20, 0))) {
			slots.add(time.toString());
			time = time.plusMinutes(30);
		}
		return slots.toArray(new String[0]);
	}

	/** Loads the dentists and the treatments from the web service. */
	private void loadComboBoxes() {
		new SwingWorker<Void, Void>() {

			private List<Map<String, String>> dentists;
			private List<Map<String, String>> treatments;

			@Override
			protected Void doInBackground() throws Exception {
				dentists = controller.loadDentists();
				treatments = controller.loadTreatments();
				return null;
			}

			@Override
			protected void done() {
				try {
					get();

					for (Map<String, String> d : dentists) {
						cmbDentist.addItem(new ComboItem(d.get("dentistId"),
								d.get("dentistName") + " (" + d.get("specialization") + ")",
								d.get("consultationFee")));
					}
					for (Map<String, String> t : treatments) {
						cmbTreatment.addItem(new ComboItem(t.get("treatmentId"),
								t.get("treatmentName"), t.get("baseCost")));
					}
					showFee();

				} catch (Exception e) {
					Throwable cause = e.getCause() == null ? e : e.getCause();
					ErrorHandler.show(AppointmentRegistrationView.this, new Exception(cause.getMessage()));
				}
			}
		}.execute();
	}

	/** Shows the price of the selected treatment and dentist. */
	private void showFee() {
		ComboItem dentist = (ComboItem) cmbDentist.getSelectedItem();
		ComboItem treatment = (ComboItem) cmbTreatment.getSelectedItem();

		if (dentist == null || treatment == null) {
			lblFee.setText(" ");
			return;
		}

		double fee = toDouble(dentist.extra);
		double cost = toDouble(treatment.extra);

		lblFee.setText(String.format("Estimated charge:  treatment Rs. %.2f  +  consultation Rs. %.2f  =  Rs. %.2f",
				cost, fee, cost + fee));
	}

	/** Checks the fields and sends the appointment to the server. */
	private void save() {

		FormValidator validator = new FormValidator()
				.checkName(txtPatientName, "Enter a valid patient name (letters only, at least 3 letters)")
				.checkAddress(txtAddress, "Enter the patient address")
				.checkContact(txtContact, "The contact number must have 10 digits and start with 0");

		ComboItem dentist = (ComboItem) cmbDentist.getSelectedItem();
		ComboItem treatment = (ComboItem) cmbTreatment.getSelectedItem();

		validator.check(dentist != null, cmbDentist, "Select a dentist");
		validator.check(treatment != null, cmbTreatment, "Select a treatment type");

		LocalDate date = selectedDate();
		validator.check(!date.isBefore(LocalDate.now()), spnDate, "The date cannot be in the past");

		if (!validator.isValid()) {
			ErrorHandler.showMessage(this, validator.getMessage());
			return;
		}

		btnSave.setEnabled(false);
		btnSave.setText("Saving...");

		final String time = (String) cmbTime.getSelectedItem();

		new SwingWorker<String, Void>() {

			@Override
			protected String doInBackground() throws Exception {
				return controller.register(txtPatientName.getText(), txtAddress.getText(),
						txtContact.getText(), dentist.id, treatment.id, date.toString(), time);
			}

			@Override
			protected void done() {
				btnSave.setEnabled(true);
				btnSave.setText("Save Appointment");

				try {
					String appointmentNo = get();

					ErrorHandler.showMessage(AppointmentRegistrationView.this,
							"The appointment is saved.\n\n"
							+ "Appointment number:  " + appointmentNo + "\n"
							+ "Patient:  " + txtPatientName.getText() + "\n"
							+ "Date and time:  " + date + " at " + time + "\n\n"
							+ "Please give the appointment number to the patient.");

					clearForm();
					if (parent != null) {
						parent.refreshCount();
					}

				} catch (Exception e) {
					Throwable cause = e.getCause() == null ? e : e.getCause();
					ErrorHandler.show(AppointmentRegistrationView.this, new Exception(cause.getMessage()));
				}
			}
		}.execute();
	}

	private LocalDate selectedDate() {
		Date value = (Date) spnDate.getValue();
		return value.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
	}

	public void clearForm() {
		txtPatientName.setText("");
		txtAddress.setText("");
		txtContact.setText("");
		spnDate.setValue(new Date());
		cmbTime.setSelectedIndex(0);
		txtPatientName.requestFocus();
	}

	private double toDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return 0;
		}
	}

	/** One line of a combo box: it shows the name but keeps the id. */
	private static class ComboItem {
		private final String id;
		private final String text;
		private final String extra;

		ComboItem(String id, String text, String extra) {
			this.id = id;
			this.text = text;
			this.extra = extra;
		}

		@Override
		public String toString() {
			return text;
		}
	}
}
