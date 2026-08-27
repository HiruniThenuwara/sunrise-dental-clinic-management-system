package com.sunrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;
import com.sunrise.controller.JsonUtil;
import com.sunrise.model.DBConnection;
import com.sunrise.server.AppointmentHandler;
import com.sunrise.server.AuthHandler;
import com.sunrise.server.BillHandler;
import com.sunrise.server.MasterDataHandler;
import com.sunrise.server.ReportHandler;

/**
 * Integration tests of the web service.
 *
 * The server is started on the test port 8899 inside the test, and the
 * requests are sent with HttpClient, the same way the Swing client does it.
 *
 * The tests that need real data are skipped when MySQL is not running, so
 * the automatic build on GitHub does not fail.
 */
public class WebServiceTest {

	private static final int TEST_PORT = 8899;
	private static final String BASE_URL = "http://localhost:" + TEST_PORT;

	private static HttpServer server;
	private static HttpClient client;
	private static boolean databaseAvailable;

	@BeforeAll
	public static void startServer() throws Exception {
		server = HttpServer.create(new InetSocketAddress(TEST_PORT), 0);
		server.createContext("/api/login", new AuthHandler());
		server.createContext("/api/logout", new AuthHandler());
		server.createContext("/api/appointments", new AppointmentHandler());
		server.createContext("/api/bills", new BillHandler());
		server.createContext("/api/reports", new ReportHandler());
		server.createContext("/api/treatments", new MasterDataHandler());
		server.setExecutor(Executors.newFixedThreadPool(4));
		server.start();

		client = HttpClient.newHttpClient();

		try {
			DBConnection.getInstance();
			databaseAvailable = true;
		} catch (Exception e) {
			databaseAvailable = false;
			System.out.println("MySQL is not running, the database tests are skipped.");
		}
	}

	@AfterAll
	public static void stopServer() {
		if (server != null) {
			server.stop(0);
		}
	}

	// ---------- tests that do not need the database ----------

	@Test
	public void appointmentsCannotBeUsedWithoutLogin() throws Exception {
		HttpResponse<String> response = get("/api/appointments/APT-20260825-001", null);

		assertEquals(401, response.statusCode());
		assertTrue(response.body().contains("login"));
	}

	@Test
	public void reportsCannotBeUsedWithoutLogin() throws Exception {
		assertEquals(401, get("/api/reports/daily", null).statusCode());
	}

	@Test
	public void billsCannotBeUsedWithoutLogin() throws Exception {
		assertEquals(401, get("/api/bills/APT-20260825-001", null).statusCode());
	}

	@Test
	public void loginWithEmptyUsernameGives401() throws Exception {
		HttpResponse<String> response = post("/api/login", "{\"username\":\"\",\"password\":\"x\"}", null);

		assertEquals(401, response.statusCode());
	}

	@Test
	public void wrongMethodGives405() throws Exception {
		HttpResponse<String> response = get("/api/login", null);

		assertEquals(405, response.statusCode());
	}

	// ---------- tests that need MySQL with the seed data ----------

	@Test
	public void staffCanLoginAndGetAToken() throws Exception {
		Assumptions.assumeTrue(databaseAvailable, "MySQL is not running");

		HttpResponse<String> response = post("/api/login",
				"{\"username\":\"kamal\",\"password\":\"kamal123\"}", null);

		assertEquals(200, response.statusCode());

		Map<String, String> body = JsonUtil.parse(response.body());
		assertNotNull(body.get("token"));
		assertEquals("RECEPTIONIST", body.get("role"));
	}

	@Test
	public void loginWithWrongPasswordGives401() throws Exception {
		Assumptions.assumeTrue(databaseAvailable, "MySQL is not running");

		HttpResponse<String> response = post("/api/login",
				"{\"username\":\"kamal\",\"password\":\"wrongpassword\"}", null);

		assertEquals(401, response.statusCode());
	}

	@Test
	public void treatmentListIsReturnedAfterLogin() throws Exception {
		Assumptions.assumeTrue(databaseAvailable, "MySQL is not running");

		String token = login("admin", "admin123");
		HttpResponse<String> response = get("/api/treatments", token);

		assertEquals(200, response.statusCode());
		assertTrue(response.body().contains("treatmentName"));
	}

	@Test
	public void searchingAnUnknownAppointmentGives400() throws Exception {
		Assumptions.assumeTrue(databaseAvailable, "MySQL is not running");

		String token = login("admin", "admin123");
		HttpResponse<String> response = get("/api/appointments/APT-19990101-999", token);

		assertEquals(400, response.statusCode());
		assertTrue(response.body().contains("not found"));
	}

	// ---------- small helper methods ----------

	private String login(String username, String password) throws Exception {
		HttpResponse<String> response = post("/api/login",
				"{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}", null);
		return JsonUtil.parse(response.body()).get("token");
	}

	private HttpResponse<String> get(String path, String token) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(BASE_URL + path)).GET();
		if (token != null) {
			builder.header("Authorization", token);
		}
		return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
	}

	private HttpResponse<String> post(String path, String body, String token) throws Exception {
		HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(BASE_URL + path))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body));
		if (token != null) {
			builder.header("Authorization", token);
		}
		return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
	}
}
