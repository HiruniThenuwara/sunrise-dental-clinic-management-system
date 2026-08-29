package com.sunrise.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import com.sunrise.controller.AppointmentController;
import com.sunrise.controller.AppointmentObserver;
import com.sunrise.controller.AppointmentSubject;
import com.sunrise.controller.ErrorHandler;

/**
 * Function 3 of the brief - search an appointment with the appointment number
 * and show all the patient and appointment information.
 *
 * The window also shows every appointment of a selected day in a table, and it
 * is an observer, so the table refreshes when another window saves or cancels
 * an appointment.
 */
public class SearchAppointmentView extends JFrame implements AppointmentObserver {

	private static final long serialVersionUID = 1L;

	private final AppointmentController controller = new AppointmentController();

	private JTextField txtAppointmentNo;
	private JSpinner spnDate;
	private JTable tblAppointments;
	private DefaultTableModel tableModel;

	private JLabel[] detailValues;
	private JButton btnCancelAppointment;
	private String selectedAppointmentNo;

	private static final String[] DETAIL_LABELS = {
			"Appointment number", "Patient name", "Address", "Contact number",
			"Dentist", "Treatment", "Date", "Time", "Status" };

	private static final String[] DETAIL_KEYS = {
			"appointmentNo", "patientName", "address", "contactNumber",
			"dentistName", "treatmentName", "appointmentDate", "appointmentTime", "status" };

	public SearchAppointmentView(JFrame parent) {
		setTitle("Search Appointment");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(UITheme.appIcon());
		setSize(900, 680);

		buildScreen();
		UITheme.center(this);

		AppointmentSubject.register(this);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				AppointmentSubject.remove(SearchAppointmentView.this);
			}
		});

		loadDay();
	}

	private void buildScreen() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(UITheme.BACKGROUND);

		main.add(UITheme.header("Search Appointment",
				"Search with the appointment number, or see all the appointments of one day"),
				BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(0, 12));
		center.setBackground(UITheme.BACKGROUND);
		center.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

		center.add(buildSearchPanel(), BorderLayout.NORTH);
		center.add(buildDetailPanel(), BorderLayout.CENTER);
		center.add(buildTablePanel(), BorderLayout.SOUTH);

		main.add(center, BorderLayout.CENTER);
		setContentPane(main);
	}

	private JPanel buildSearchPanel() {
		JPanel panel = UITheme.card();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

		panel.add(UITheme.label("Appointment number"));

		txtAppointmentNo = UITheme.textField(18);
		panel.add(txtAppointmentNo);

		JButton search = UITheme.primaryButton("Search");
		search.addActionListener(e -> searchOne());
		panel.add(search);

		panel.add(UITheme.hint("example: APT-20260825-001"));
		return panel;
	}

	private JPanel buildDetailPanel() {
		JPanel card = UITheme.card();
		card.setLayout(new BorderLayout(0, 10));

		card.add(UITheme.heading("Appointment details"), BorderLayout.NORTH);

		JPanel grid = new JPanel(new GridLayout(DETAIL_LABELS.length, 2, 8, 6));
		grid.setBackground(UITheme.CARD);

		detailValues = new JLabel[DETAIL_LABELS.length];
		for (int i = 0; i < DETAIL_LABELS.length; i++) {
			grid.add(UITheme.label(DETAIL_LABELS[i]));

			detailValues[i] = UITheme.label("-");
			detailValues[i].setFont(UITheme.NORMAL_FONT.deriveFont(java.awt.Font.BOLD));
			grid.add(detailValues[i]);
		}
		card.add(grid, BorderLayout.CENTER);

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
		buttons.setBackground(UITheme.CARD);

		btnCancelAppointment = UITheme.dangerButton("Cancel this appointment");
		btnCancelAppointment.setEnabled(false);
		btnCancelAppointment.addActionListener(e -> cancelAppointment());
		buttons.add(btnCancelAppointment);

		card.add(buttons, BorderLayout.SOUTH);
		return card;
	}

	private JPanel buildTablePanel() {
		JPanel card = UITheme.card();
		card.setLayout(new BorderLayout(0, 8));

		JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
		top.setBackground(UITheme.CARD);
		top.add(UITheme.heading("Appointments of the day"));

		spnDate = new JSpinner(new SpinnerDateModel());
		spnDate.setEditor(new JSpinner.DateEditor(spnDate, "yyyy-MM-dd"));
		spnDate.setFont(UITheme.NORMAL_FONT);
		top.add(spnDate);

		JButton load = UITheme.secondaryButton("Show");
		load.addActionListener(e -> loadDay());
		top.add(load);

		card.add(top, BorderLayout.NORTH);

		tableModel = new DefaultTableModel(
				new String[] { "Appointment No", "Time", "Patient", "Contact", "Dentist", "Treatment", "Status" }, 0) {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // the table is only for reading
			}
		};

		tblAppointments = new JTable(tableModel);
		UITheme.styleTable(tblAppointments);
		tblAppointments.setAutoCreateRowSorter(true);

		// clicking a row shows the details above
		tblAppointments.getSelectionModel().addListSelectionListener(e -> {
			int row = tblAppointments.getSelectedRow();
			if (row >= 0 && !e.getValueIsAdjusting()) {
				txtAppointmentNo.setText(String.valueOf(tblAppointments.getValueAt(row, 0)));
				searchOne();
			}
		});

		JScrollPane scroll = new JScrollPane(tblAppointments);
		scroll.setPreferredSize(new java.awt.Dimension(840, 200));
		card.add(scroll, BorderLayout.CENTER);

		return card;
	}

	// ---------- actions ----------

	private void searchOne() {
		final String appointmentNo = txtAppointmentNo.getText().trim();

		new SwingWorker<Map<String, String>, Void>() {

			@Override
			protected Map<String, String> doInBackground() throws Exception {
				return controller.search(appointmentNo);
			}

			@Override
			protected void done() {
				try {
					showDetails(get());
				} catch (Exception e) {
					clearDetails();
					Throwable cause = e.getCause() == null ? e : e.getCause();
					ErrorHandler.show(SearchAppointmentView.this, new Exception(cause.getMessage()));
				}
			}
		}.execute();
	}

	private void loadDay() {
		Date value = (Date) spnDate.getValue();
		final LocalDate date = value.toInstant()
				.atZone(java.time.ZoneId.systemDefault()).toLocalDate();

		new SwingWorker<List<Map<String, String>>, Void>() {

			@Override
			protected List<Map<String, String>> doInBackground() throws Exception {
				return controller.listByDate(date.toString());
			}

			@Override
			protected void done() {
				try {
					tableModel.setRowCount(0);

					for (Map<String, String> row : get()) {
						tableModel.addRow(new Object[] {
								row.get("appointmentNo"), row.get("appointmentTime"),
								row.get("patientName"), row.get("contactNumber"),
								row.get("dentistName"), row.get("treatmentName"), row.get("status") });
					}

				} catch (Exception e) {
					Throwable cause = e.getCause() == null ? e : e.getCause();
					ErrorHandler.show(SearchAppointmentView.this, new Exception(cause.getMessage()));
				}
			}
		}.execute();
	}

	private void cancelAppointment() {
		if (selectedAppointmentNo == null) {
			return;
		}

		if (!ErrorHandler.confirm(this, "Do you want to cancel appointment "
				+ selectedAppointmentNo + " ?")) {
			return;
		}

		try {
			controller.cancel(selectedAppointmentNo);
			ErrorHandler.showMessage(this, "The appointment " + selectedAppointmentNo + " is cancelled.");
			searchOne();
			loadDay();

		} catch (Exception e) {
			ErrorHandler.show(this, e);
		}
	}

	public void showDetails(Map<String, String> appointment) {
		for (int i = 0; i < DETAIL_KEYS.length; i++) {
			String value = appointment.get(DETAIL_KEYS[i]);
			detailValues[i].setText(value == null ? "-" : value);
		}

		selectedAppointmentNo = appointment.get("appointmentNo");
		btnCancelAppointment.setEnabled(!"CANCELLED".equals(appointment.get("status")));
	}

	private void clearDetails() {
		for (JLabel value : detailValues) {
			value.setText("-");
		}
		selectedAppointmentNo = null;
		btnCancelAppointment.setEnabled(false);
	}

	/** Observer pattern - refresh the table when an appointment changes. */
	@Override
	public void appointmentChanged(String appointmentNo, String action) {
		SwingUtilities.invokeLater(this::loadDay);
	}
}
