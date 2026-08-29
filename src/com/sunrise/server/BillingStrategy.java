package com.sunrise.server;

/**
 * Strategy design pattern.
 *
 * Each class that implements this interface knows one way of calculating the
 * amount of a bill. BillingService keeps one of them and can change it at run
 * time, so a new price rule does not change the service class.
 */
public interface BillingStrategy {

	/**
	 * @param treatmentCost   price of the treatment
	 * @param consultationFee fee of the dentist
	 * @return the amount before the tax is added
	 */
	double calculate(double treatmentCost, double consultationFee);

	/** Name shown on the receipt, for example "Senior Citizen Discount". */
	String getName();
}
