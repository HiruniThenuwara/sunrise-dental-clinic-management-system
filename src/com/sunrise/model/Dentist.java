package com.sunrise.model;

/**
 * A dentist working in the clinic. The consultation fee is added to every bill.
 */
public class Dentist {

	private int dentistId;
	private String dentistName;
	private String specialization;
	private double consultationFee;
	private boolean available;

	public Dentist() {
	}

	public Dentist(int dentistId, String dentistName, String specialization, double consultationFee) {
		this.dentistId = dentistId;
		this.dentistName = dentistName;
		this.specialization = specialization;
		this.consultationFee = consultationFee;
		this.available = true;
	}

	public int getDentistId() {
		return dentistId;
	}

	public void setDentistId(int dentistId) {
		this.dentistId = dentistId;
	}

	public String getDentistName() {
		return dentistName;
	}

	public void setDentistName(String dentistName) {
		this.dentistName = dentistName;
	}

	public String getSpecialization() {
		return specialization;
	}

	public void setSpecialization(String specialization) {
		this.specialization = specialization;
	}

	public double getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(double consultationFee) {
		this.consultationFee = consultationFee;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	// shown inside the dentist combo box of the registration window
	@Override
	public String toString() {
		return dentistName + " (" + specialization + ")";
	}
}
