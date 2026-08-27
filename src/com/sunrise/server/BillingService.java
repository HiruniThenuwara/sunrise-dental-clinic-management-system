package com.sunrise.server;

import com.sunrise.controller.AppConfig;
import com.sunrise.controller.ClinicException;
import com.sunrise.model.Appointment;
import com.sunrise.model.Bill;
import com.sunrise.model.BillDAO;
import com.sunrise.model.DAOFactory;
import com.sunrise.model.IAppointmentDAO;

/**
 * Creates the bill of an appointment.
 *
 * total = treatment cost + consultation fee - discount + tax
 *
 * The part before the tax is calculated by a BillingStrategy object, so this
 * class does not know the price rules (Strategy design pattern).
 */
public class BillingService {

	private IAppointmentDAO appointmentDAO;
	private BillDAO billDAO;
	private BillingStrategy strategy;
	private double taxRate;

	public BillingService() {
		this.appointmentDAO = DAOFactory.getAppointmentDAO();
		this.billDAO = DAOFactory.getBillDAO();
		this.strategy = new StandardBillingStrategy();
		this.taxRate = AppConfig.getTaxRate();
	}

	// used by the unit tests
	public BillingService(IAppointmentDAO appointmentDAO, BillDAO billDAO, double taxRate) {
		this.appointmentDAO = appointmentDAO;
		this.billDAO = billDAO;
		this.strategy = new StandardBillingStrategy();
		this.taxRate = taxRate;
	}

	public void setStrategy(BillingStrategy strategy) {
		this.strategy = strategy;
	}

	public BillingStrategy getStrategy() {
		return strategy;
	}

	/**
	 * Calculates and saves the bill of one appointment.
	 */
	public Bill generateBill(String appointmentNo, int billedBy, String paymentMethod)
			throws ClinicException {

		Appointment appointment = appointmentDAO.findByNumber(appointmentNo);
		if (appointment == null) {
			throw new ClinicException("Appointment number " + appointmentNo + " was not found.");
		}
		if ("CANCELLED".equals(appointment.getStatus())) {
			throw new ClinicException("This appointment is cancelled, so a bill cannot be created.");
		}

		double treatmentCost = appointment.getTreatmentCost();
		double consultationFee = appointment.getConsultationFee();

		double amount = strategy.calculate(treatmentCost, consultationFee);
		double discount = 0;
		if (amount < treatmentCost + consultationFee) {
			discount = round(treatmentCost + consultationFee - amount);
		}

		double tax = round(amount * taxRate);
		double total = round(amount + tax);

		Bill bill = new Bill();
		bill.setAppointmentId(appointment.getAppointmentId());
		bill.setAppointmentNo(appointment.getAppointmentNo());
		bill.setTreatmentCost(treatmentCost);
		bill.setConsultationFee(consultationFee);
		bill.setDiscount(discount);
		bill.setTax(tax);
		bill.setTotalAmount(total);
		bill.setBilledBy(billedBy);
		if (paymentMethod != null && !paymentMethod.isEmpty()) {
			bill.setPaymentMethod(paymentMethod);
		}

		billDAO.insert(bill);

		// after the bill the visit is finished
		appointmentDAO.updateStatus(appointmentNo, "COMPLETED");

		return bill;
	}

	public Bill findBill(String appointmentNo) throws ClinicException {
		Bill bill = billDAO.findByAppointmentNo(appointmentNo);
		if (bill == null) {
			throw new ClinicException("No bill was found for " + appointmentNo + ".");
		}
		return bill;
	}

	// money is kept with two decimal places
	private double round(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
}
