package com.sunrise.server;

import java.time.LocalDate;
import java.util.List;

import com.sunrise.controller.ClinicException;
import com.sunrise.model.Appointment;
import com.sunrise.model.DAOFactory;
import com.sunrise.model.IAppointmentDAO;
import com.sunrise.model.ReportDAO;

/**
 * The four management reports of the clinic.
 * These reports help the manager to take decisions, for example which
 * treatment brings the most income.
 */
public class ReportService {

	private ReportDAO reportDAO;
	private IAppointmentDAO appointmentDAO;

	public ReportService() {
		this.reportDAO = DAOFactory.getReportDAO();
		this.appointmentDAO = DAOFactory.getAppointmentDAO();
	}

	public ReportService(ReportDAO reportDAO, IAppointmentDAO appointmentDAO) {
		this.reportDAO = reportDAO;
		this.appointmentDAO = appointmentDAO;
	}

	/** Report 1: every appointment of one day. */
	public List<Appointment> dailyAppointments(LocalDate date) throws ClinicException {
		return appointmentDAO.findByDate(date);
	}

	/** Report 2: how many appointments each dentist has on that day. */
	public List<String[]> appointmentsByDentist(LocalDate date) throws ClinicException {
		return reportDAO.appointmentsByDentist(date);
	}

	/** Report 3: money collected between two dates. */
	public List<String[]> revenueSummary(LocalDate from, LocalDate to) throws ClinicException {
		if (from == null || to == null) {
			throw new ClinicException("Please select the from date and the to date.");
		}
		if (to.isBefore(from)) {
			throw new ClinicException("The to date cannot be before the from date.");
		}
		return reportDAO.revenueSummary(from, to);
	}

	/** Report 4: the treatments the patients take the most. */
	public List<String[]> popularTreatments() throws ClinicException {
		return reportDAO.popularTreatments();
	}

	/** Number shown on the dashboard of the main menu. */
	public int todayCount() throws ClinicException {
		return reportDAO.countByDate(LocalDate.now());
	}
}
