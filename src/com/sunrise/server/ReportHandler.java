package com.sunrise.server;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sunrise.controller.ClinicException;
import com.sunrise.controller.JsonUtil;
import com.sunrise.model.Appointment;
import com.sunrise.model.User;

/**
 * Web service endpoints of the reports:
 *   GET /api/reports/daily?date=
 *   GET /api/reports/dentists?date=
 *   GET /api/reports/revenue?from=&to=
 *   GET /api/reports/treatments
 *   GET /api/reports/today
 */
public class ReportHandler extends BaseHandler {

	private ReportService reportService = new ReportService();

	@Override
	protected void process(HttpExchange exchange) throws Exception {

		User user = requireLogin(exchange);
		if (user == null) {
			return;
		}

		if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			sendError(exchange, 405, "The reports can only be read with GET.");
			return;
		}

		String reportName = lastPathPart(exchange);

		switch (reportName) {
			case "daily":
				dailyReport(exchange);
				break;
			case "dentists":
				sendRows(exchange, reportService.appointmentsByDentist(readDate(exchange, "date")),
						"dentistName", "specialization", "appointments");
				break;
			case "revenue":
				revenueReport(exchange);
				break;
			case "treatments":
				sendRows(exchange, reportService.popularTreatments(),
						"treatmentName", "appointments", "income");
				break;
			case "today":
				Map<String, String> count = new LinkedHashMap<>();
				count.put("todayCount", String.valueOf(reportService.todayCount()));
				sendJson(exchange, 200, JsonUtil.toJson(count));
				break;
			default:
				sendError(exchange, 404, "There is no report with the name " + reportName + ".");
		}
	}

	private void dailyReport(HttpExchange exchange) throws Exception {
		List<Appointment> appointments = reportService.dailyAppointments(readDate(exchange, "date"));

		List<Map<String, String>> rows = new ArrayList<>();
		for (Appointment a : appointments) {
			Map<String, String> row = new LinkedHashMap<>();
			row.put("appointmentNo", a.getAppointmentNo());
			row.put("time", String.valueOf(a.getAppointmentTime()));
			row.put("patientName", a.getPatientName());
			row.put("contactNumber", a.getContactNumber());
			row.put("dentistName", a.getDentistName());
			row.put("treatmentName", a.getTreatmentName());
			row.put("status", a.getStatus());
			rows.add(row);
		}

		sendJson(exchange, 200, JsonUtil.toJsonArray(rows));
	}

	private void revenueReport(HttpExchange exchange) throws Exception {
		LocalDate from = readDate(exchange, "from");
		LocalDate to = readDate(exchange, "to");

		sendRows(exchange, reportService.revenueSummary(from, to), "date", "bills", "total");
	}

	/** Sends a report table where every row has three values. */
	private void sendRows(HttpExchange exchange, List<String[]> data, String col1, String col2, String col3)
			throws Exception {

		List<Map<String, String>> rows = new ArrayList<>();
		for (String[] values : data) {
			Map<String, String> row = new LinkedHashMap<>();
			row.put(col1, values[0]);
			row.put(col2, values[1]);
			row.put(col3, values[2]);
			rows.add(row);
		}

		sendJson(exchange, 200, JsonUtil.toJsonArray(rows));
	}

	private LocalDate readDate(HttpExchange exchange, String name) throws ClinicException {
		String value = queryValue(exchange, name);
		if (value == null) {
			return LocalDate.now();
		}
		try {
			return LocalDate.parse(value);
		} catch (Exception e) {
			throw new ClinicException("The date " + value + " is not in the format yyyy-mm-dd.");
		}
	}
}
