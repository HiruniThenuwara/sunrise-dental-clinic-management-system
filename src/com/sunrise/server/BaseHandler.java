package com.sunrise.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sunrise.controller.ClinicException;
import com.sunrise.controller.JsonUtil;
import com.sunrise.controller.SessionManager;
import com.sunrise.model.User;

/**
 * Common code of all the web service handlers.
 *
 * The handle() method catches the errors in one place, so every endpoint
 * answers with the same kind of JSON message and the correct HTTP status:
 * 200 ok, 201 created, 400 wrong data, 401 not logged in, 404 not found,
 * 405 wrong method, 500 server error.
 */
public abstract class BaseHandler implements HttpHandler {

	/** Every handler writes its own work here. */
	protected abstract void process(HttpExchange exchange) throws Exception;

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			process(exchange);

		} catch (ClinicException e) {
			// a normal business error, for example "time slot already booked"
			sendError(exchange, 400, e.getMessage());

		} catch (Exception e) {
			System.out.println("Server error: " + e.getMessage());
			sendError(exchange, 500, "The server could not complete the request.");

		} finally {
			exchange.close();
		}
	}

	protected String readBody(HttpExchange exchange) throws IOException {
		try (InputStream in = exchange.getRequestBody()) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	protected void sendJson(HttpExchange exchange, int status, String json) throws IOException {
		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
		exchange.sendResponseHeaders(status, bytes.length);

		try (OutputStream out = exchange.getResponseBody()) {
			out.write(bytes);
		}
	}

	protected void sendMessage(HttpExchange exchange, int status, String message) throws IOException {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("message", message);
		sendJson(exchange, status, JsonUtil.toJson(map));
	}

	protected void sendError(HttpExchange exchange, int status, String message) throws IOException {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("error", message);
		sendJson(exchange, status, JsonUtil.toJson(map));
	}

	/**
	 * Checks the session token that the client sends in the Authorization header.
	 * Returns null and answers 401 when the staff member is not logged in.
	 */
	protected User requireLogin(HttpExchange exchange) throws IOException {
		String token = exchange.getRequestHeaders().getFirst("Authorization");
		User user = SessionManager.getUser(token);

		if (user == null) {
			sendError(exchange, 401, "Please login again, your session is finished.");
			return null;
		}
		return user;
	}

	/** Gives the last part of the path, for example the appointment number. */
	protected String lastPathPart(HttpExchange exchange) {
		String path = exchange.getRequestURI().getPath();
		int index = path.lastIndexOf('/');
		return index < 0 ? "" : path.substring(index + 1);
	}

	/** Reads a value from the query string, for example ?date=2026-08-25 */
	protected String queryValue(HttpExchange exchange, String name) {
		String query = exchange.getRequestURI().getQuery();
		if (query == null) {
			return null;
		}

		for (String part : query.split("&")) {
			String[] pair = part.split("=", 2);
			if (pair.length == 2 && pair[0].equals(name)) {
				return java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
			}
		}
		return null;
	}
}
