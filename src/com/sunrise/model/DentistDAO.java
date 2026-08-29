package com.sunrise.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sunrise.controller.ClinicException;

/**
 * SQL of the dentists table. The consultation fee of the dentist is needed
 * for the bill, and the list is shown in the dentist combo box.
 */
public class DentistDAO {

	public List<Dentist> findAll() throws ClinicException {
		String sql = "SELECT dentist_id, dentist_name, specialization, consultation_fee, is_available "
				+ "FROM dentists WHERE is_available = 1 ORDER BY dentist_name";

		List<Dentist> list = new ArrayList<>();
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
			throw new ClinicException("Could not read the dentist list.", e);
		}
	}

	public Dentist findById(int dentistId) throws ClinicException {
		String sql = "SELECT dentist_id, dentist_name, specialization, consultation_fee, is_available "
				+ "FROM dentists WHERE dentist_id = ?";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, dentistId);

			ResultSet rs = ps.executeQuery();
			Dentist dentist = null;
			if (rs.next()) {
				dentist = read(rs);
			}
			rs.close();
			ps.close();
			return dentist;

		} catch (SQLException e) {
			throw new ClinicException("Could not read the dentist.", e);
		}
	}

	private Dentist read(ResultSet rs) throws SQLException {
		Dentist dentist = new Dentist(rs.getInt("dentist_id"), rs.getString("dentist_name"),
				rs.getString("specialization"), rs.getDouble("consultation_fee"));
		dentist.setAvailable(rs.getBoolean("is_available"));
		return dentist;
	}
}
