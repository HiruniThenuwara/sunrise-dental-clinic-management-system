package com.sunrise.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sunrise.controller.ClinicException;

/**
 * DAO pattern - all the SQL of the users table is written here.
 * PreparedStatement is used so nobody can do a SQL injection from the login form.
 */
public class UserDAO implements IUserDAO {

	@Override
	public User findByUsername(String username) throws ClinicException {
		String sql = "SELECT user_id, username, password_hash, salt, full_name, role, "
				+ "is_active, failed_attempts FROM users WHERE username = ?";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, username);

			ResultSet rs = ps.executeQuery();

			User user = null;
			if (rs.next()) {
				user = new User();
				user.setUserId(rs.getInt("user_id"));
				user.setUsername(rs.getString("username"));
				user.setPasswordHash(rs.getString("password_hash"));
				user.setSalt(rs.getString("salt"));
				user.setFullName(rs.getString("full_name"));
				user.setRole(rs.getString("role"));
				user.setActive(rs.getBoolean("is_active"));
				user.setFailedAttempts(rs.getInt("failed_attempts"));
			}

			rs.close();
			ps.close();
			return user;

		} catch (SQLException e) {
			throw new ClinicException("Could not read the user details from the database.", e);
		}
	}

	@Override
	public void updateFailedAttempts(int userId, int attempts) throws ClinicException {
		String sql = "UPDATE users SET failed_attempts = ? WHERE user_id = ?";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, attempts);
			ps.setInt(2, userId);
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			throw new ClinicException("Could not update the login attempts.", e);
		}
	}

	@Override
	public void lockAccount(int userId) throws ClinicException {
		String sql = "UPDATE users SET is_active = 0 WHERE user_id = ?";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, userId);
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			throw new ClinicException("Could not lock the user account.", e);
		}
	}
}
