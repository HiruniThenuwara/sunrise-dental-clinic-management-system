package com.sunrise.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Reads the settings from config/db.properties.
 * Nothing like the database password is written inside the java code.
 */
public class AppConfig {

	private static final String CONFIG_FILE = "config/db.properties";

	private static Properties properties;

	// load the file only once
	private static Properties getProperties() {
		if (properties == null) {
			properties = new Properties();
			try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
				properties.load(in);
			} catch (IOException e) {
				throw new RuntimeException("Cannot read " + CONFIG_FILE
						+ ". Please check the file exists in the project folder.", e);
			}
		}
		return properties;
	}

	public static String get(String key) {
		return getProperties().getProperty(key);
	}

	public static String getDbUrl() {
		return get("db.url");
	}

	public static String getDbUser() {
		return get("db.user");
	}

	public static String getDbPassword() {
		return get("db.password");
	}

	public static int getServerPort() {
		return Integer.parseInt(get("server.port"));
	}

	public static String getServerHost() {
		return get("server.host");
	}

	public static double getTaxRate() {
		return Double.parseDouble(get("clinic.taxRate"));
	}

	public static double getSeniorDiscount() {
		return Double.parseDouble(get("clinic.seniorDiscount"));
	}

	public static double getEmergencyCharge() {
		return Double.parseDouble(get("clinic.emergencyCharge"));
	}

	public static int getSessionTimeout() {
		return Integer.parseInt(get("session.timeout"));
	}
}
