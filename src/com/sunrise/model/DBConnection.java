package com.sunrise.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.sunrise.controller.AppConfig;
import com.sunrise.controller.ClinicException;

/**
 * Singleton design pattern.
 * Only one DBConnection object is created for the whole application,
 * so the system does not open a new MySQL connection for every query.
 */
public class DBConnection {

	// the only object of this class
	private static DBConnection instance;

	private Connection connection;

	// private constructor, so no other class can use "new DBConnection()"
	private DBConnection() throws ClinicException {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(AppConfig.getDbUrl(),
					AppConfig.getDbUser(), AppConfig.getDbPassword());
		} catch (ClassNotFoundException e) {
			throw new ClinicException("MySQL driver not found. "
					+ "Add lib/mysql-connector-j.jar to the build path.", e);
		} catch (SQLException e) {
			throw new ClinicException("Cannot connect to the database. "
					+ "Please check that MySQL is running and the settings in config/db.properties are correct.", e);
		}
	}

	/**
	 * Gives the single object. If the connection was closed it is opened again.
	 */
	public static synchronized DBConnection getInstance() throws ClinicException {
		try {
			if (instance == null || instance.connection == null || instance.connection.isClosed()) {
				instance = new DBConnection();
			}
		} catch (SQLException e) {
			throw new ClinicException("Database connection check failed.", e);
		}
		return instance;
	}

	public Connection getConnection() {
		return connection;
	}

	public void close() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Could not close the database connection: " + e.getMessage());
		}
		instance = null;
	}
}
