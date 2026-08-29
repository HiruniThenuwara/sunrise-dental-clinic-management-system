package com.sunrise.server;

import java.time.LocalDate;
import java.util.List;

import com.sunrise.controller.AppointmentNumberGenerator;
import com.sunrise.controller.ClinicException;
import com.sunrise.controller.ValidationUtil;
import com.sunrise.model.Appointment;
import com.sunrise.model.DAOFactory;
import com.sunrise.model.IAppointmentDAO;

/**
 * Business rules of the appointments.
 *
 * The most important rule is the double booking check: one dentist cannot
 * have two appointments at the same date and time.
 */
public class AppointmentService {

	private IAppointmentDAO appointmentDAO;

	public AppointmentService() {
		this.appointmentDAO = DAOFactory.getAppointmentDAO();
	}

	// used by the unit tests with a fake DAO
	public AppointmentService(IAppointmentDAO appointmentDAO) {
		this.appointmentDAO = appointmentDAO;
	}

	/**
	 * Saves a new appointment and returns the new appointment number.
	 */
	public String register(Appointment appointment) throws ClinicException {

		validate(appointment);
		checkDoubleBooking(appointment);

		// the number is APT-date-001, 002, 003 ... for each day
		int sequence = appointmentDAO.findByDate(appointment.getAppointmentDate()).size() + 1;
		appointment.setAppointmentNo(
				AppointmentNumberGenerator.generate(appointment.getAppointmentDate(), sequence));
		appointment.setStatus("BOOKED");

		return appointmentDAO.insert(appointment);
	}

	public Appointment searchByNumber(String appointmentNo) throws ClinicException {
		if (!ValidationUtil.isNotEmpty(appointmentNo)) {
			throw new ClinicException("Please enter the appointment number.");
		}

		Appointment appointment = appointmentDAO.findByNumber(appointmentNo.trim());
		if (appointment == null) {
			throw new ClinicException("Appointment number " + appointmentNo + " was not found.");
		}
		return appointment;
	}

	public List<Appointment> findByDate(LocalDate date) throws ClinicException {
		return appointmentDAO.findByDate(date);
	}

	public void cancel(String appointmentNo) throws ClinicException {
		Appointment appointment = searchByNumber(appointmentNo);

		if ("CANCELLED".equals(appointment.getStatus())) {
			throw new ClinicException("This appointment is already cancelled.");
		}
		// the row is never deleted, only the status is changed,
		// because the clinic needs the history for the reports
		appointmentDAO.updateStatus(appointmentNo.trim(), "CANCELLED");
	}

	public void complete(String appointmentNo) throws ClinicException {
		searchByNumber(appointmentNo);
		appointmentDAO.updateStatus(appointmentNo.trim(), "COMPLETED");
	}

	private void validate(Appointment a) throws ClinicException {

		if (!ValidationUtil.isValidName(a.getPatientName())) {
			throw new ClinicException("Please enter a valid patient name (letters only, 3 to 50 characters).");
		}
		if (!ValidationUtil.isValidAddress(a.getAddress())) {
			throw new ClinicException("Please enter the patient address.");
		}
		if (!ValidationUtil.isValidContact(a.getContactNumber())) {
			throw new ClinicException("The contact number must have 10 digits and start with 0.");
		}
		if (a.getDentistId() <= 0) {
			throw new ClinicException("Please select a dentist.");
		}
		if (a.getTreatmentId() <= 0) {
			throw new ClinicException("Please select a treatment type.");
		}
		if (!ValidationUtil.isFutureDate(a.getAppointmentDate())) {
			throw new ClinicException("The appointment date cannot be in the past.");
		}
		if (!ValidationUtil.isClinicTime(a.getAppointmentTime())) {
			throw new ClinicException("The clinic is open from 08:00 to 20:00 only.");
		}
	}

	private void checkDoubleBooking(Appointment a) throws ClinicException {
		boolean booked = appointmentDAO.isSlotBooked(a.getDentistId(), a.getAppointmentDate(),
				a.getAppointmentTime());

		if (booked) {
			throw new ClinicException("This dentist is already booked at " + a.getAppointmentTime()
					+ " on " + a.getAppointmentDate() + ". Please select another time.");
		}
	}
}
