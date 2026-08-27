package com.sunrise.model;

/**
 * Factory design pattern.
 *
 * The service classes ask this factory for a DAO object instead of writing
 * "new UserDAO()". The services then depend on the interfaces only, so the
 * data access classes can be changed without touching the business logic.
 */
public class DAOFactory {

	public static IUserDAO getUserDAO() {
		return new UserDAO();
	}

	public static IAppointmentDAO getAppointmentDAO() {
		return new AppointmentDAO();
	}

	public static PatientDAO getPatientDAO() {
		return new PatientDAO();
	}

	public static DentistDAO getDentistDAO() {
		return new DentistDAO();
	}

	public static TreatmentDAO getTreatmentDAO() {
		return new TreatmentDAO();
	}

	public static BillDAO getBillDAO() {
		return new BillDAO();
	}

	public static ReportDAO getReportDAO() {
		return new ReportDAO();
	}
}
