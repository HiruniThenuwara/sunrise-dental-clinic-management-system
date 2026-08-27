package com.sunrise.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.sunrise.controller.ClinicException;

/**
 * SQL of the management reports. The rows are returned as String arrays
 * because a report is only shown in a table and is not saved back.
 */
public class ReportDAO {

	/** Money collected on each day between two dates. */
	public List<String[]> revenueSummary(LocalDate from, LocalDate to) throws ClinicException {
		String sql = "SELECT a.appointment_date, COUNT(b.bill_id) AS bill_count, "
				+ "SUM(b.total_amount) AS total "
				+ "FROM bills b JOIN appointments a ON b.appointment_id = a.appointment_id "
				+ "WHERE a.appointment_date BETWEEN ? AND ? "
				+ "GROUP BY a.appointment_date ORDER BY a.appointment_date";

		List<String[]> rows = new ArrayList<>();
		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setDate(1, java.sql.Date.valueOf(from));
			ps.setDate(2, java.sql.Date.valueOf(to));

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rows.add(new String[] { rs.getString("appointment_date"), rs.getString("bill_count"),
						String.format("%.2f", rs.getDouble("total")) });
			}
			rs.close();
			ps.close();
			return rows;

		} catch (SQLException e) {
			throw new ClinicException("Could not create the revenue report.", e);
		}
	}

	/** How many appointments each dentist has on a given day. */
	public List<String[]> appointmentsByDentist(LocalDate date) throws ClinicException {
		String sql = "SELECT d.dentist_name, d.specialization, COUNT(a.appointment_id) AS total "
				+ "FROM dentists d LEFT JOIN appointments a "
				+ "ON d.dentist_id = a.dentist_id AND a.appointment_date = ? AND a.status <> 'CANCELLED' "
				+ "GROUP BY d.dentist_id, d.dentist_name, d.specialization ORDER BY total DESC";

		List<String[]> rows = new ArrayList<>();
		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setDate(1, java.sql.Date.valueOf(date));

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				rows.add(new String[] { rs.getString("dentist_name"), rs.getString("specialization"),
						rs.getString("total") });
			}
			rs.close();
			ps.close();
			return rows;

		} catch (SQLException e) {
			throw new ClinicException("Could not create the dentist report.", e);
		}
	}

	/** Which treatments the patients take the most. */
	public List<String[]> popularTreatments() throws ClinicException {
		String sql = "SELECT t.treatment_name, COUNT(a.appointment_id) AS total, "
				+ "SUM(t.base_cost) AS income "
				+ "FROM treatments t LEFT JOIN appointments a ON t.treatment_id = a.treatment_id "
				+ "GROUP BY t.treatment_id, t.treatment_name ORDER BY total DESC";

		List<String[]> rows = new ArrayList<>();
		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				double income = rs.getDouble("income");
				rows.add(new String[] { rs.getString("treatment_name"), rs.getString("total"),
						String.format("%.2f", income) });
			}
			rs.close();
			ps.close();
			return rows;

		} catch (SQLException e) {
			throw new ClinicException("Could not create the treatment report.", e);
		}
	}

	/** Number of appointments of one day, shown on the dashboard. */
	public int countByDate(LocalDate date) throws ClinicException {
		String sql = "SELECT COUNT(*) FROM appointments WHERE appointment_date = ? AND status <> 'CANCELLED'";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setDate(1, java.sql.Date.valueOf(date));

			ResultSet rs = ps.executeQuery();
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
			ps.close();
			return count;

		} catch (SQLException e) {
			throw new ClinicException("Could not count the appointments.", e);
		}
	}
}
