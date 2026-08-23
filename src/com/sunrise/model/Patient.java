package com.sunrise.model;

/**
 * Patient details collected when a new patient comes to the clinic.
 */
public class Patient {

	private int patientId;
	private String patientName;
	private String address;
	private String contactNumber;
	private String nic;

	public Patient() {
	}

	public Patient(String patientName, String address, String contactNumber) {
		this.patientName = patientName;
		this.address = address;
		this.contactNumber = contactNumber;
	}

	public int getPatientId() {
		return patientId;
	}

	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	public String getPatientName() {
		return patientName;
	}

	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	@Override
	public String toString() {
		return patientName + " - " + contactNumber;
	}
}
