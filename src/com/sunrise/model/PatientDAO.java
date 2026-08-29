package com.sunrise.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sunrise.controller.ClinicException;

/**
 * SQL of the patients table.
 * The contact number is used to find a patient who came to the clinic before,
 * so the same patient is not saved many times.
 */
public class PatientDAO {

	public int insert(Patient patient) throws ClinicException {
		String sql = "INSERT INTO patients (patient_name, address, contact_number, nic) VALUES (?, ?, ?, ?)";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, patient.getPatientName());
			ps.setString(2, patient.getAddress());
			ps.setString(3, patient.getContactNumber());
			ps.setString(4, patient.getNic());
			ps.executeUpdate();

			ResultSet keys = ps.getGeneratedKeys();
			int newId = 0;
			if (keys.next()) {
				newId = keys.getInt(1);
			}
			keys.close();
			ps.close();

			patient.setPatientId(newId);
			return newId;

		} catch (SQLException e) {
			throw new ClinicException("Could not save the patient details.", e);
		}
	}

	public Patient findByContact(String contactNumber) throws ClinicException {
		String sql = "SELECT patient_id, patient_name, address, contact_number, nic "
				+ "FROM patients WHERE contact_number = ? ORDER BY patient_id DESC LIMIT 1";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, contactNumber);

			ResultSet rs = ps.executeQuery();
			Patient patient = null;
			if (rs.next()) {
				patient = readPatient(rs);
			}
			rs.close();
			ps.close();
			return patient;

		} catch (SQLException e) {
			throw new ClinicException("Could not read the patient details.", e);
		}
	}

	public Patient findById(int patientId) throws ClinicException {
		String sql = "SELECT patient_id, patient_name, address, contact_number, nic "
				+ "FROM patients WHERE patient_id = ?";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, patientId);

			ResultSet rs = ps.executeQuery();
			Patient patient = null;
			if (rs.next()) {
				patient = readPatient(rs);
			}
			rs.close();
			ps.close();
			return patient;

		} catch (SQLException e) {
			throw new ClinicException("Could not read the patient details.", e);
		}
	}

	private Patient readPatient(ResultSet rs) throws SQLException {
		Patient patient = new Patient();
		patient.setPatientId(rs.getInt("patient_id"));
		patient.setPatientName(rs.getString("patient_name"));
		patient.setAddress(rs.getString("address"));
		patient.setContactNumber(rs.getString("contact_number"));
		patient.setNic(rs.getString("nic"));
		return patient;
	}
}
