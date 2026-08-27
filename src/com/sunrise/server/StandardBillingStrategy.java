package com.sunrise.server;

/**
 * Normal patient: treatment cost + consultation fee, no discount.
 */
public class StandardBillingStrategy implements BillingStrategy {

	@Override
	public double calculate(double treatmentCost, double consultationFee) {
		return treatmentCost + consultationFee;
	}

	@Override
	public String getName() {
		return "Standard";
	}
}
