package com.sunrise.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.sunrise.controller.ClinicException;

/**
 * DAO pattern - the interface of the appointment table.
 */
public interface IAppointmentDAO {

	/** Saves a new appointment and returns the appointment number. */
	String insert(Appointment appointment) throws ClinicException;

	/** Finds one appointment with the patient, dentist and treatment details. */
	Appointment findByNumber(String appointmentNo) throws ClinicException;

	/** All the appointments of one day. */
	List<Appointment> findByDate(LocalDate date) throws ClinicException;

	/** True when the dentist already has an appointment at that date and time. */
	boolean isSlotBooked(int dentistId, LocalDate date, LocalTime time) throws ClinicException;

	/** Changes the status to COMPLETED or CANCELLED. */
	void updateStatus(String appointmentNo, String status) throws ClinicException;
}
