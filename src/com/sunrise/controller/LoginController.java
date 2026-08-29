package com.sunrise.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sunrise.client.RestClient;

/**
 * Controller of the login window (the C of MVC).
 *
 * The window only collects the username and the password. This class checks
 * them, calls the web service and keeps the session details.
 */
public class LoginController {

	private final RestClient client = RestClient.getInstance();

	/**
	 * Sends the login request to the web service.
	 *
	 * @return the full name of the staff member who logged in
	 */
	public String login(String username, char[] password) throws ClinicException {

		String plainPassword = new String(password);

		if (!ValidationUtil.isNotEmpty(username)) {
			throw new ClinicException("Please enter your username.");
		}
		if (!ValidationUtil.isNotEmpty(plainPassword)) {
			throw new ClinicException("Please enter your password.");
		}

		Map<String, String> request = new LinkedHashMap<>();
		request.put("username", username.trim());
		request.put("password", plainPassword);

		String answer = client.post("/api/login", JsonUtil.toJson(request));
		Map<String, String> body = JsonUtil.parse(answer);

		client.setSession(body.get("token"), toInt(body.get("userId")),
				body.get("fullName"), body.get("role"));

		// the password is not kept in the memory of the window
		java.util.Arrays.fill(password, ' ');

		return body.get("fullName");
	}

	public void logout() {
		try {
			client.post("/api/logout", "{}");
		} catch (ClinicException e) {
			// the server may be off already, the local session is closed anyway
			ErrorHandler.log(e);
		}
		client.clearSession();
	}

	private int toInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return 0;
		}
	}
}
