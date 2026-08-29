package com.sunrise.server;

import com.sunrise.controller.AppConfig;

/**
 * Patients over 60 years get a discount on the whole amount.
 * The discount rate is kept in config/db.properties, so the clinic manager
 * can change it without changing the java code.
 */
public class SeniorCitizenDiscountStrategy implements BillingStrategy {

	private double discountRate;

	public SeniorCitizenDiscountStrategy() {
		this(AppConfig.getSeniorDiscount());
	}

	// the unit tests use this constructor with a fixed rate
	public SeniorCitizenDiscountStrategy(double discountRate) {
		this.discountRate = discountRate;
	}

	@Override
	public double calculate(double treatmentCost, double consultationFee) {
		double amount = treatmentCost + consultationFee;
		return amount - (amount * discountRate);
	}

	@Override
	public String getName() {
		return "Senior Citizen Discount " + (int) (discountRate * 100) + "%";
	}
}
