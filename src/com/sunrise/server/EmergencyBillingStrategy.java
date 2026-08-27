package com.sunrise.server;

import com.sunrise.controller.AppConfig;

/**
 * Emergency patient who comes without an appointment.
 * An extra charge is added because the dentist has to see the patient
 * between the booked appointments.
 */
public class EmergencyBillingStrategy implements BillingStrategy {

	private double emergencyCharge;

	public EmergencyBillingStrategy() {
		this(AppConfig.getEmergencyCharge());
	}

	public EmergencyBillingStrategy(double emergencyCharge) {
		this.emergencyCharge = emergencyCharge;
	}

	@Override
	public double calculate(double treatmentCost, double consultationFee) {
		return treatmentCost + consultationFee + emergencyCharge;
	}

	@Override
	public String getName() {
		return "Emergency (extra Rs. " + emergencyCharge + ")";
	}
}
