package com.sunrise.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import com.sunrise.controller.AppConfig;
import com.sunrise.controller.ClinicException;
import com.sunrise.controller.JsonUtil;

/**
 * The client side of the distributed application.
 *
 * The Swing windows never touch the database. They call this class, and this
 * class sends the request to the web service with java.net.http.HttpClient.
 * The session token that the server gave at the login is added to every
 * request, so the server knows who is working.
 */
public class RestClient {

	private static RestClient instance;

	private final HttpClient http;
	private final String baseUrl;

	private String token;
	private int userId;
	private String fullName;
	private String role;

	private RestClient() {
		this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
		this.baseUrl = "http://" + AppConfig.getServerHost() + ":" + AppConfig.getServerPort();
	}

	public static synchronized RestClient getInstance() {
		if (instance == null) {
			instance = new RestClient();
		}
		return instance;
	}

	public String get(String path) throws ClinicException {
		return send(HttpRequest.newBuilder(URI.create(baseUrl + path)).GET());
	}

	public String post(String path, String json) throws ClinicException {
		return send(HttpRequest.newBuilder(URI.create(baseUrl + path))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json)));
	}

	public String put(String path, String json) throws ClinicException {
		return send(HttpRequest.newBuilder(URI.create(baseUrl + path))
				.header("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(json)));
	}

	private String send(HttpRequest.Builder builder) throws ClinicException {

		if (token != null) {
			builder.header("Authorization", token);
		}
		builder.timeout(Duration.ofSeconds(15));

		try {
			HttpResponse<String> response = http.send(builder.build(),
					HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 400) {
				throw new ClinicException(readErrorMessage(response.body(), response.statusCode()));
			}
			return response.body();

		} catch (ConnectException e) {
			throw new ClinicException("Cannot connect to the clinic server.\n"
					+ "Please ask the administrator to start the server program (ServerMain).", e);

		} catch (IOException e) {
			throw new ClinicException("The connection to the server was lost. Please try again.", e);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new ClinicException("The request was stopped.", e);
		}
	}

	/** Takes the message that the server sent inside the JSON answer. */
	private String readErrorMessage(String body, int status) {
		Map<String, String> map = JsonUtil.parse(body);
		String message = map.get("error");

		if (message == null || message.isEmpty()) {
			message = "The server answered with the error code " + status + ".";
		}
		if (status == 401) {
			message = message + "\nPlease login again.";
		}
		return message;
	}

	// ---- details of the staff member who is logged in ----

	public void setSession(String token, int userId, String fullName, String role) {
		this.token = token;
		this.userId = userId;
		this.fullName = fullName;
		this.role = role;
	}

	public void clearSession() {
		this.token = null;
		this.userId = 0;
		this.fullName = null;
		this.role = null;
	}

	public boolean isLoggedIn() {
		return token != null;
	}

	public int getUserId() {
		return userId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getRole() {
		return role;
	}
}
