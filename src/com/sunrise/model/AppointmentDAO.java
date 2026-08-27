package com.sunrise.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.sunrise.controller.ClinicException;

/**
 * SQL of the appointments table.
 *
 * The select statements use a join, so one query gives the appointment with
 * the patient name, the dentist name, the treatment name and the two prices.
 */
public class AppointmentDAO implements IAppointmentDAO {

	private PatientDAO patientDAO = new PatientDAO();

	private static final String SELECT_JOIN =
			"SELECT a.appointment_id, a.appointment_no, a.patient_id, a.dentist_id, a.treatment_id, "
			+ "a.appointment_date, a.appointment_time, a.status, a.created_by, "
			+ "p.patient_name, p.address, p.contact_number, "
			+ "d.dentist_name, d.consultation_fee, "
			+ "t.treatment_name, t.base_cost "
			+ "FROM appointments a "
			+ "JOIN patients p   ON a.patient_id = p.patient_id "
			+ "JOIN dentists d   ON a.dentist_id = d.dentist_id "
			+ "JOIN treatments t ON a.treatment_id = t.treatment_id ";

	@Override
	public String insert(Appointment appointment) throws ClinicException {

		// the same patient can come again, so first look for the contact number
		Patient patient = patientDAO.findByContact(appointment.getContactNumber());
		if (patient == null) {
			patient = new Patient(appointment.getPatientName(), appointment.getAddress(),
					appointment.getContactNumber());
			patientDAO.insert(patient);
		}
		appointment.setPatientId(patient.getPatientId());

		String sql = "INSERT INTO appointments (appointment_no, patient_id, dentist_id, treatment_id, "
				+ "appointment_date, appointment_time, status, created_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, appointment.getAppointmentNo());
			ps.setInt(2, appointment.getPatientId());
			ps.setInt(3, appointment.getDentistId());
			ps.setInt(4, appointment.getTreatmentId());
			ps.setDate(5, java.sql.Date.valueOf(appointment.getAppointmentDate()));
			ps.setTime(6, java.sql.Time.valueOf(appointment.getAppointmentTime()));
			ps.setString(7, appointment.getStatus());
			ps.setInt(8, appointment.getCreatedBy());
			ps.executeUpdate();

			ResultSet keys = ps.getGeneratedKeys();
			if (keys.next()) {
				appointment.setAppointmentId(keys.getInt(1));
			}
			keys.close();
			ps.close();

			return appointment.getAppointmentNo();

		} catch (SQLException e) {
			// error 1062 means the UNIQUE key stopped a double booking
			if (e.getErrorCode() == 1062) {
				throw new ClinicException("This dentist already has an appointment at that date and time.", e);
			}
			throw new ClinicException("Could not save the appointment.", e);
		}
	}

	@Override
	public Appointment findByNumber(String appointmentNo) throws ClinicException {
		String sql = SELECT_JOIN + "WHERE a.appointment_no = ?";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, appointmentNo);

			ResultSet rs = ps.executeQuery();
			Appointment appointment = null;
			if (rs.next()) {
				appointment = readAppointment(rs);
			}
			rs.close();
			ps.close();
			return appointment;

		} catch (SQLException e) {
			throw new ClinicException("Could not read the appointment.", e);
		}
	}

	@Override
	public List<Appointment> findByDate(LocalDate date) throws ClinicException {
		String sql = SELECT_JOIN + "WHERE a.appointment_date = ? ORDER BY a.appointment_time";

		List<Appointment> list = new ArrayList<>();
		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setDate(1, java.sql.Date.valueOf(date));

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				list.add(readAppointment(rs));
			}
			rs.close();
			ps.close();
			return list;

		} catch (SQLException e) {
			throw new ClinicException("Could not read the appointment list.", e);
		}
	}

	@Override
	public boolean isSlotBooked(int dentistId, LocalDate date, LocalTime time) throws ClinicException {
		String sql = "SELECT COUNT(*) FROM appointments WHERE dentist_id = ? AND appointment_date = ? "
				+ "AND appointment_time = ? AND status <> 'CANCELLED'";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setInt(1, dentistId);
			ps.setDate(2, java.sql.Date.valueOf(date));
			ps.setTime(3, java.sql.Time.valueOf(time));

			ResultSet rs = ps.executeQuery();
			boolean booked = false;
			if (rs.next()) {
				booked = rs.getInt(1) > 0;
			}
			rs.close();
			ps.close();
			return booked;

		} catch (SQLException e) {
			throw new ClinicException("Could not check the dentist availability.", e);
		}
	}

	@Override
	public void updateStatus(String appointmentNo, String status) throws ClinicException {
		String sql = "UPDATE appointments SET status = ? WHERE appointment_no = ?";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, status);
			ps.setString(2, appointmentNo);
			ps.executeUpdate();
			ps.close();

		} catch (SQLException e) {
			throw new ClinicException("Could not update the appointment.", e);
		}
	}

	private Appointment readAppointment(ResultSet rs) throws SQLException {
		Appointment a = new Appointment();
		a.setAppointmentId(rs.getInt("appointment_id"));
		a.setAppointmentNo(rs.getString("appointment_no"));
		a.setPatientId(rs.getInt("patient_id"));
		a.setDentistId(rs.getInt("dentist_id"));
		a.setTreatmentId(rs.getInt("treatment_id"));
		a.setAppointmentDate(rs.getDate("appointment_date").toLocalDate());
		a.setAppointmentTime(rs.getTime("appointment_time").toLocalTime());
		a.setStatus(rs.getString("status"));
		a.setCreatedBy(rs.getInt("created_by"));

		a.setPatientName(rs.getString("patient_name"));
		a.setAddress(rs.getString("address"));
		a.setContactNumber(rs.getString("contact_number"));
		a.setDentistName(rs.getString("dentist_name"));
		a.setConsultationFee(rs.getDouble("consultation_fee"));
		a.setTreatmentName(rs.getString("treatment_name"));
		a.setTreatmentCost(rs.getDouble("base_cost"));
		return a;
	}
}
