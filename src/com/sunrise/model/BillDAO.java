package com.sunrise.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sunrise.controller.ClinicException;

/**
 * SQL of the bills table.
 */
public class BillDAO {

	public int insert(Bill bill) throws ClinicException {
		String sql = "INSERT INTO bills (appointment_id, treatment_cost, consultation_fee, discount, "
				+ "tax, total_amount, payment_method, billed_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, bill.getAppointmentId());
			ps.setDouble(2, bill.getTreatmentCost());
			ps.setDouble(3, bill.getConsultationFee());
			ps.setDouble(4, bill.getDiscount());
			ps.setDouble(5, bill.getTax());
			ps.setDouble(6, bill.getTotalAmount());
			ps.setString(7, bill.getPaymentMethod());
			ps.setInt(8, bill.getBilledBy());
			ps.executeUpdate();

			ResultSet keys = ps.getGeneratedKeys();
			int newId = 0;
			if (keys.next()) {
				newId = keys.getInt(1);
			}
			keys.close();
			ps.close();

			bill.setBillId(newId);
			return newId;

		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
				throw new ClinicException("A bill was already created for this appointment.", e);
			}
			throw new ClinicException("Could not save the bill.", e);
		}
	}

	public Bill findByAppointmentNo(String appointmentNo) throws ClinicException {
		String sql = "SELECT b.bill_id, b.appointment_id, a.appointment_no, b.treatment_cost, "
				+ "b.consultation_fee, b.discount, b.tax, b.total_amount, b.payment_method, "
				+ "b.billed_by, b.billed_at "
				+ "FROM bills b JOIN appointments a ON b.appointment_id = a.appointment_id "
				+ "WHERE a.appointment_no = ?";

		try {
			Connection con = DBConnection.getInstance().getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, appointmentNo);

			ResultSet rs = ps.executeQuery();
			Bill bill = null;
			if (rs.next()) {
				bill = new Bill();
				bill.setBillId(rs.getInt("bill_id"));
				bill.setAppointmentId(rs.getInt("appointment_id"));
				bill.setAppointmentNo(rs.getString("appointment_no"));
				bill.setTreatmentCost(rs.getDouble("treatment_cost"));
				bill.setConsultationFee(rs.getDouble("consultation_fee"));
				bill.setDiscount(rs.getDouble("discount"));
				bill.setTax(rs.getDouble("tax"));
				bill.setTotalAmount(rs.getDouble("total_amount"));
				bill.setPaymentMethod(rs.getString("payment_method"));
				bill.setBilledBy(rs.getInt("billed_by"));
				bill.setBilledAt(rs.getTimestamp("billed_at").toLocalDateTime());
			}
			rs.close();
			ps.close();
			return bill;

		} catch (SQLException e) {
			throw new ClinicException("Could not read the bill.", e);
		}
	}
}
