package com.sunrise.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;
import com.sunrise.controller.AppConfig;
import com.sunrise.model.DBConnection;

/**
 * Starts the web service of the clinic.
 *
 * This class makes the system a distributed application: the Swing client is
 * a different program and talks to this server over HTTP with JSON. The
 * HttpServer class comes with the JDK, so no web framework is needed.
 *
 * Run this class first, and after that run ClientMain.
 */
public class ServerMain {

	public static void main(String[] args) {

		int port = AppConfig.getServerPort();

		try {
			// check the database before the server starts,
			// so the staff sees the problem immediately and not at the first login
			DBConnection.getInstance();
			System.out.println("Database connection is ready.");

			HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
			createContexts(server);

			// a small thread pool, a clinic reception has only a few computers
			server.setExecutor(Executors.newFixedThreadPool(10));
			server.start();

			System.out.println("Sunrise Dental Clinic web service is running on http://localhost:" + port);
			System.out.println("Press Ctrl + C to stop the server.");

		} catch (IOException e) {
			System.out.println("The server could not start on port " + port + ": " + e.getMessage());
		} catch (Exception e) {
			System.out.println("The server could not start: " + e.getMessage());
		}
	}

	/** Connects every address of the web service to its handler class. */
	private static void createContexts(HttpServer server) {
		server.createContext("/api/login", new AuthHandler());
		server.createContext("/api/logout", new AuthHandler());
		server.createContext("/api/appointments", new AppointmentHandler());
		server.createContext("/api/bills", new BillHandler());
		server.createContext("/api/reports", new ReportHandler());
		server.createContext("/api/treatments", new MasterDataHandler());
		server.createContext("/api/dentists", new MasterDataHandler());
	}
}
