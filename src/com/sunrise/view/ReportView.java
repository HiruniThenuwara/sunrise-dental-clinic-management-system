package com.sunrise.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import com.sunrise.client.RestClient;
import com.sunrise.controller.ErrorHandler;
import com.sunrise.controller.JsonUtil;

/**
 * The management reports of the clinic.
 *
 * Four reports help the manager to take decisions: what is happening today,
 * how busy each dentist is, how much money was collected, and which
 * treatments the patients take the most. Every report can be saved as a CSV
 * file and opened in Excel.
 */
public class ReportView extends JFrame {

	private static final long serialVersionUID = 1L;

	private static final String[] REPORT_NAMES = {
			"Daily appointment list",
			"Appointments by dentist",
			"Revenue summary",
			"Most popular treatments" };

	private final RestClient client = RestClient.getInstance();

	private JComboBox<String> cmbReport;
	private JSpinner spnFrom;
	private JSpinner spnTo;
	private JTable tblReport;
	private DefaultTableModel tableModel;

	public ReportView(JFrame parent) {
		setTitle("Reports");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(UITheme.appIcon());
		setSize(920, 620);

		buildScreen();
		UITheme.center(this);

		loadReport();
	}

	private void buildScreen() {
		JPanel main = new JPanel(new BorderLayout());
		main.setBackground(UITheme.BACKGROUND);

		main.add(UITheme.header("Clinic Reports",
				"Select the report and the dates, then press Show"), BorderLayout.NORTH);

		JPanel center = new JPanel(new BorderLayout(0, 12));
		center.setBackground(UITheme.BACKGROUND);
		center.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
		center.add(buildFilterPanel(), BorderLayout.NORTH);
		center.add(buildTablePanel(), BorderLayout.CENTER);

		main.add(center, BorderLayout.CENTER);
		setContentPane(main);
	}

	private JPanel buildFilterPanel() {
		JPanel card = UITheme.card();
		card.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 6));

		card.add(UITheme.label("Report"));
		cmbReport = new JComboBox<>(REPORT_NAMES);
		cmbReport.setFont(UITheme.NORMAL_FONT);
		card.add(cmbReport);

		card.add(UITheme.label("From"));
		spnFrom = dateSpinner();
		card.add(spnFrom);

		card.add(UITheme.label("To"));
		spnTo = dateSpinner();
		card.add(spnTo);

		JButton show = UITheme.primaryButton("Show");
		show.addActionListener(e -> loadReport());
		card.add(show);

		JButton export = UITheme.secondaryButton("Export to CSV");
		export.addActionListener(e -> exportCsv());
		card.add(export);

		return card;
	}

	private JSpinner dateSpinner() {
		JSpinner spinner = new JSpinner(new SpinnerDateModel());
		spinner.setEditor(new JSpinner.DateEditor(spinner, "yyyy-MM-dd"));
		spinner.setFont(UITheme.NORMAL_FONT);
		return spinner;
	}

	private JPanel buildTablePanel() {
		JPanel card = UITheme.card();
		card.setLayout(new BorderLayout());

		tableModel = new DefaultTableModel() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		tblReport = new JTable(tableModel);
		UITheme.styleTable(tblReport);
		tblReport.setAutoCreateRowSorter(true);

		card.add(new JScrollPane(tblReport), BorderLayout.CENTER);
		return card;
	}

	// ---------- actions ----------

	private void loadReport() {
		final int selected = cmbReport.getSelectedIndex();
		final String from = readDate(spnFrom);
		final String to = readDate(spnTo);

		new SwingWorker<List<Map<String, String>>, Void>() {

			@Override
			protected List<Map<String, String>> doInBackground() throws Exception {
				String path;

				switch (selected) {
					case 1:
						path = "/api/reports/dentists?date=" + from;
						break;
					case 2:
						path = "/api/reports/revenue?from=" + from + "&to=" + to;
						break;
					case 3:
						path = "/api/reports/treatments";
						break;
					default:
						path = "/api/reports/daily?date=" + from;
				}
				return JsonUtil.parseArray(client.get(path));
			}

			@Override
			protected void done() {
				try {
					showRows(get());
				} catch (Exception e) {
					Throwable cause = e.getCause() == null ? e : e.getCause();
					ErrorHandler.show(ReportView.this, new Exception(cause.getMessage()));
				}
			}
		}.execute();
	}

	/** Puts the rows of the answer into the table. */
	private void showRows(List<Map<String, String>> rows) {
		tableModel.setRowCount(0);
		tableModel.setColumnCount(0);

		if (rows.isEmpty()) {
			tableModel.addColumn("Result");
			tableModel.addRow(new Object[] { "There is no data for this report." });
			return;
		}

		for (String column : rows.get(0).keySet()) {
			tableModel.addColumn(columnTitle(column));
		}

		for (Map<String, String> row : rows) {
			tableModel.addRow(row.values().toArray());
		}
	}

	/** Changes appointmentNo into "Appointment No" for the table header. */
	private String columnTitle(String key) {
		StringBuilder title = new StringBuilder();
		title.append(Character.toUpperCase(key.charAt(0)));

		for (int i = 1; i < key.length(); i++) {
			char c = key.charAt(i);
			if (Character.isUpperCase(c)) {
				title.append(' ');
			}
			title.append(c);
		}
		return title.toString();
	}

	private void exportCsv() {
		if (tableModel.getRowCount() == 0) {
			ErrorHandler.showMessage(this, "There is nothing to export. Please show a report first.");
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setSelectedFile(new File(fileName()));

		if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		try (PrintWriter writer = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {

			for (int col = 0; col < tableModel.getColumnCount(); col++) {
				writer.print(tableModel.getColumnName(col));
				writer.print(col == tableModel.getColumnCount() - 1 ? "\n" : ",");
			}

			for (int row = 0; row < tableModel.getRowCount(); row++) {
				for (int col = 0; col < tableModel.getColumnCount(); col++) {
					Object value = tableModel.getValueAt(row, col);
					writer.print(value == null ? "" : value.toString().replace(",", " "));
					writer.print(col == tableModel.getColumnCount() - 1 ? "\n" : ",");
				}
			}

			ErrorHandler.showMessage(this, "The report is saved as\n"
					+ chooser.getSelectedFile().getAbsolutePath());

		} catch (Exception e) {
			ErrorHandler.show(this, new Exception("The report could not be saved. " + e.getMessage()));
		}
	}

	private String fileName() {
		String name = String.valueOf(cmbReport.getSelectedItem()).toLowerCase().replace(' ', '-');
		return name + "-" + LocalDate.now() + ".csv";
	}

	private String readDate(JSpinner spinner) {
		Date value = (Date) spinner.getValue();
		return value.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().toString();
	}
}
