package com.sunrise.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sunrise.client.RestClient;

/**
 * Controller of the appointment windows.
 *
 * It changes what the user typed into a JSON request, calls the web service
 * and gives the answer back to the window. When an appointment is saved or
 * cancelled it tells the observers, so the other open windows refresh.
 */
public class AppointmentController {

	private final RestClient client = RestClient.getInstance();

	/** Dentist list for the combo box. */
	public List<Map<String, String>> loadDentists() throws ClinicException {
		return JsonUtil.parseArray(client.get("/api/dentists"));
	}

	/** Treatment list with the prices for the combo box. */
	public List<Map<String, String>> loadTreatments() throws ClinicException {
		return JsonUtil.parseArray(client.get("/api/treatments"));
	}

	/**
	 * Function 2 of the brief - registers a new appointment.
	 *
	 * @return the appointment number that the system created
	 */
	public String register(String patientName, String address, String contactNumber,
			String dentistId, String treatmentId, String date, String time) throws ClinicException {

		Map<String, String> request = new LinkedHashMap<>();
		request.put("patientName", patientName.trim());
		request.put("address", address.trim());
		request.put("contactNumber", contactNumber.trim());
		request.put("dentistId", dentistId);
		request.put("treatmentId", treatmentId);
		request.put("appointmentDate", date);
		request.put("appointmentTime", time);

		String answer = client.post("/api/appointments", JsonUtil.toJson(request));
		String appointmentNo = JsonUtil.parse(answer).get("appointmentNo");

		AppointmentSubject.notifyObservers(appointmentNo, "SAVED");
		return appointmentNo;
	}

	/** Function 3 of the brief - searches one appointment by its number. */
	public Map<String, String> search(String appointmentNo) throws ClinicException {
		if (!ValidationUtil.isNotEmpty(appointmentNo)) {
			throw new ClinicException("Please enter the appointment number.");
		}
		return JsonUtil.parse(client.get("/api/appointments/" + appointmentNo.trim()));
	}

	/** All the appointments of one day, shown in the table. */
	public List<Map<String, String>> listByDate(String date) throws ClinicException {
		return JsonUtil.parseArray(client.get("/api/appointments?date=" + date));
	}

	public void cancel(String appointmentNo) throws ClinicException {
		client.put("/api/appointments/" + appointmentNo, "{\"status\":\"CANCELLED\"}");
		AppointmentSubject.notifyObservers(appointmentNo, "CANCELLED");
	}

	/** Number of appointments of today, shown on the dashboard. */
	public String todayCount() throws ClinicException {
		return JsonUtil.parse(client.get("/api/reports/today")).get("todayCount");
	}
}
