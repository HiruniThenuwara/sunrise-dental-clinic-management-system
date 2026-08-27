package com.sunrise.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.sunrise.model.User;

/**
 * Keeps the staff sessions of the web service.
 *
 * After a successful login the server creates a token and sends it to the
 * client. The client must send that token with every next request, so a
 * person who does not log in cannot use the appointment or billing service.
 * A session is closed automatically after the timeout in db.properties.
 */
public class SessionManager {

	private static final Map<String, Session> sessions = new ConcurrentHashMap<>();

	public static String createToken(User user) {
		String token = UUID.randomUUID().toString();
		sessions.put(token, new Session(user, LocalDateTime.now()));
		return token;
	}

	public static User getUser(String token) {
		if (token == null) {
			return null;
		}

		Session session = sessions.get(token);
		if (session == null) {
			return null;
		}

		if (isExpired(session)) {
			sessions.remove(token);
			return null;
		}

		// the session lives as long as the staff member is working
		session.lastUsed = LocalDateTime.now();
		return session.user;
	}

	public static boolean isValid(String token) {
		return getUser(token) != null;
	}

	public static void remove(String token) {
		if (token != null) {
			sessions.remove(token);
		}
	}

	public static int activeSessions() {
		return sessions.size();
	}

	private static boolean isExpired(Session session) {
		int timeoutMinutes = AppConfig.getSessionTimeout();
		return session.lastUsed.plusMinutes(timeoutMinutes).isBefore(LocalDateTime.now());
	}

	/** One logged in staff member. */
	private static class Session {
		private final User user;
		private LocalDateTime lastUsed;

		Session(User user, LocalDateTime lastUsed) {
			this.user = user;
			this.lastUsed = lastUsed;
		}
	}
}
