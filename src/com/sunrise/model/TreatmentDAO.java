package com.sunrise.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sunrise.controller.ClinicException;

/**
 * SQL of the treatments table. The price list is loaded from here into the
 * treatment combo box of the registration window.
 */
public class TreatmentDAO {

	public List<Treatment> findAll() throws ClinicException {
		String sql = "SELECT treatment_id, treatment_name, base_cost, duration_minutes "
				+ "FROM treatments ORDER BY treatment_name";

		List<Treatment> list = new ArrayList<>();
		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				list.add(read(rs));
			}
			rs.close();
			ps.close();
			return list;

		} catch (SQLException e) {
			throw new ClinicException("Could not read the treatment list.", e);
		}
	}

	public Treatment findById(int treatmentId) throws ClinicException {
		String sql = "SELECT treatment_id, treatment_name, base_cost, duration_minutes "
				+ "FROM treatments WHERE treatment_id = ?";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, treatmentId);

			ResultSet rs = ps.executeQuery();
			Treatment treatment = null;
			if (rs.next()) {
				treatment = read(rs);
			}
			rs.close();
			ps.close();
			return treatment;

		} catch (SQLException e) {
			throw new ClinicException("Could not read the treatment.", e);
		}
	}

	private Treatment read(ResultSet rs) throws SQLException {
		return new Treatment(rs.getInt("treatment_id"), rs.getString("treatment_name"),
				rs.getDouble("base_cost"), rs.getInt("duration_minutes"));
	}
}
