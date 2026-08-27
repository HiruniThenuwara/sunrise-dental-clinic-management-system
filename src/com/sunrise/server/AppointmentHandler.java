package com.sunrise.server;

import java.time.LocalDate;
import java.time.LocalTime;
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
 * Web service endpoints of the appointments:
 *   POST /api/appointments          - register a new appointment
 *   GET  /api/appointments/{number} - search one appointment
 *   GET  /api/appointments?date=    - all the appointments of one day
 *   PUT  /api/appointments/{number} - cancel or complete an appointment
 */
public class AppointmentHandler extends BaseHandler {

	private AppointmentService appointmentService = new AppointmentService();

	@Override
	protected void process(HttpExchange exchange) throws Exception {

		User user = requireLogin(exchange);
		if (user == null) {
			return; // the answer 401 is already sent
		}

		String method = exchange.getRequestMethod();

		if ("POST".equalsIgnoreCase(method)) {
			register(exchange, user);

		} else if ("GET".equalsIgnoreCase(method)) {
			String date = queryValue(exchange, "date");
			if (date != null) {
				listByDate(exchange, date);
			} else {
				searchOne(exchange);
			}

		} else if ("PUT".equalsIgnoreCase(method)) {
			changeStatus(exchange);

		} else {
			sendError(exchange, 405, "This method is not allowed for the appointments.");
		}
	}

	private void register(HttpExchange exchange, User user) throws Exception {
		Map<String, String> request = JsonUtil.parse(readBody(exchange));

		Appointment appointment = new Appointment();
		appointment.setPatientName(request.get("patientName"));
		appointment.setAddress(request.get("address"));
		appointment.setContactNumber(request.get("contactNumber"));
		appointment.setDentistId(toInt(request.get("dentistId")));
		appointment.setTreatmentId(toInt(request.get("treatmentId")));
		appointment.setCreatedBy(user.getUserId());

		try {
			appointment.setAppointmentDate(LocalDate.parse(request.get("appointmentDate")));
			appointment.setAppointmentTime(LocalTime.parse(request.get("appointmentTime")));
		} catch (Exception e) {
			throw new ClinicException("Please select a correct date and time.");
		}

		String appointmentNo = appointmentService.register(appointment);

		Map<String, String> response = new LinkedHashMap<>();
		response.put("appointmentNo", appointmentNo);
		response.put("message", "Appointment " + appointmentNo + " is saved.");

		sendJson(exchange, 201, JsonUtil.toJson(response));
	}

	private void searchOne(HttpExchange exchange) throws Exception {
		String appointmentNo = lastPathPart(exchange);

		Appointment appointment = appointmentService.searchByNumber(appointmentNo);
		sendJson(exchange, 200, JsonUtil.toJson(toMap(appointment)));
	}

	private void listByDate(HttpExchange exchange, String date) throws Exception {
		List<Appointment> appointments;
		try {
			appointments = appointmentService.findByDate(LocalDate.parse(date));
		} catch (java.time.format.DateTimeParseException e) {
			throw new ClinicException("The date must be in the format yyyy-mm-dd.");
		}

		List<Map<String, String>> rows = new ArrayList<>();
		for (Appointment appointment : appointments) {
			rows.add(toMap(appointment));
		}

		sendJson(exchange, 200, JsonUtil.toJsonArray(rows));
	}

	private void changeStatus(HttpExchange exchange) throws Exception {
		String appointmentNo = lastPathPart(exchange);
		Map<String, String> request = JsonUtil.parse(readBody(exchange));
		String status = request.get("status");

		if ("CANCELLED".equalsIgnoreCase(status)) {
			appointmentService.cancel(appointmentNo);
			sendMessage(exchange, 200, "Appointment " + appointmentNo + " is cancelled.");

		} else if ("COMPLETED".equalsIgnoreCase(status)) {
			appointmentService.complete(appointmentNo);
			sendMessage(exchange, 200, "Appointment " + appointmentNo + " is completed.");

		} else {
			throw new ClinicException("The status must be CANCELLED or COMPLETED.");
		}
	}

	/** Changes one appointment object into name and value pairs for the JSON answer. */
	private Map<String, String> toMap(Appointment a) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("appointmentNo", a.getAppointmentNo());
		map.put("patientName", a.getPatientName());
		map.put("address", a.getAddress());
		map.put("contactNumber", a.getContactNumber());
		map.put("dentistId", String.valueOf(a.getDentistId()));
		map.put("dentistName", a.getDentistName());
		map.put("treatmentId", String.valueOf(a.getTreatmentId()));
		map.put("treatmentName", a.getTreatmentName());
		map.put("treatmentCost", String.format("%.2f", a.getTreatmentCost()));
		map.put("consultationFee", String.format("%.2f", a.getConsultationFee()));
		map.put("appointmentDate", String.valueOf(a.getAppointmentDate()));
		map.put("appointmentTime", String.valueOf(a.getAppointmentTime()));
		map.put("status", a.getStatus());
		return map;
	}

	private int toInt(String value) {
		try {
			return Integer.parseInt(value.trim());
		} catch (Exception e) {
			return 0;
		}
	}
}
