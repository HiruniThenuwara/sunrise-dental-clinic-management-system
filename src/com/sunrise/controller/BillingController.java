package com.sunrise.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sunrise.client.RestClient;

/**
 * Controller of the billing window.
 * Function 4 of the brief - calculate and print the bill.
 */
public class BillingController {

	private final RestClient client = RestClient.getInstance();

	/**
	 * Asks the web service to calculate and save the bill.
	 *
	 * @param patientType STANDARD, SENIOR or EMERGENCY. The server uses this
	 *                    to select the billing strategy.
	 */
	public Map<String, String> generateBill(String appointmentNo, String patientType,
			String paymentMethod) throws ClinicException {

		if (!ValidationUtil.isNotEmpty(appointmentNo)) {
			throw new ClinicException("Please enter the appointment number.");
		}

		Map<String, String> request = new LinkedHashMap<>();
		request.put("appointmentNo", appointmentNo.trim());
		request.put("patientType", patientType);
		request.put("paymentMethod", paymentMethod);

		String answer = client.post("/api/bills", JsonUtil.toJson(request));

		AppointmentSubject.notifyObservers(appointmentNo.trim(), "BILLED");
		return JsonUtil.parse(answer);
	}

	/** Reads a bill that was created before, to print the receipt again. */
	public Map<String, String> findBill(String appointmentNo) throws ClinicException {
		return JsonUtil.parse(client.get("/api/bills/" + appointmentNo.trim()));
	}
}
