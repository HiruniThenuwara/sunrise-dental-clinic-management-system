package com.sunrise.server;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sunrise.controller.ClinicException;
import com.sunrise.controller.JsonUtil;
import com.sunrise.controller.SessionManager;
import com.sunrise.model.User;

/**
 * Web service endpoints of the login:
 *   POST /api/login   - checks the username and password, gives a token
 *   POST /api/logout  - closes the session
 */
public class AuthHandler extends BaseHandler {

	private AuthService authService = new AuthService();

	@Override
	protected void process(HttpExchange exchange) throws Exception {

		String path = exchange.getRequestURI().getPath();

		if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
			sendError(exchange, 405, "Only POST is allowed for this address.");
			return;
		}

		if (path.endsWith("/logout")) {
			logout(exchange);
		} else {
			login(exchange);
		}
	}

	private void login(HttpExchange exchange) throws Exception {
		Map<String, String> request = JsonUtil.parse(readBody(exchange));

		User user;
		try {
			user = authService.login(request.get("username"), request.get("password"));
		} catch (ClinicException e) {
			// a failed login is answered with 401 and not with 400
			sendError(exchange, 401, e.getMessage());
			return;
		}

		String token = SessionManager.createToken(user);

		Map<String, String> response = new LinkedHashMap<>();
		response.put("token", token);
		response.put("userId", String.valueOf(user.getUserId()));
		response.put("username", user.getUsername());
		response.put("fullName", user.getFullName());
		response.put("role", user.getRole());

		sendJson(exchange, 200, JsonUtil.toJson(response));
	}

	private void logout(HttpExchange exchange) throws Exception {
		String token = exchange.getRequestHeaders().getFirst("Authorization");
		SessionManager.remove(token);

		sendMessage(exchange, 200, "You are logged out.");
	}
}
