package com.sunrise.model;

/**
 * A treatment type with its standard price, for example Root Canal Treatment.
 */
public class Treatment {

	private int treatmentId;
	private String treatmentName;
	private double baseCost;
	private int durationMinutes;

	public Treatment() {
	}

	public Treatment(int treatmentId, String treatmentName, double baseCost, int durationMinutes) {
		this.treatmentId = treatmentId;
		this.treatmentName = treatmentName;
		this.baseCost = baseCost;
		this.durationMinutes = durationMinutes;
	}

	public int getTreatmentId() {
		return treatmentId;
	}

	public void setTreatmentId(int treatmentId) {
		this.treatmentId = treatmentId;
	}

	public String getTreatmentName() {
		return treatmentName;
	}

	public void setTreatmentName(String treatmentName) {
		this.treatmentName = treatmentName;
	}

	public double getBaseCost() {
		return baseCost;
	}

	public void setBaseCost(double baseCost) {
		this.baseCost = baseCost;
	}

	public int getDurationMinutes() {
		return durationMinutes;
	}

	public void setDurationMinutes(int durationMinutes) {
		this.durationMinutes = durationMinutes;
	}

	// shown inside the treatment combo box
	@Override
	public String toString() {
		return treatmentName + " - Rs. " + baseCost;
	}
}
