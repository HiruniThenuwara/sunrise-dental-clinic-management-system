package com.sunrise.model;

import java.time.LocalDateTime;

/**
 * The bill of one appointment.
 * total = treatment cost + consultation fee - discount + tax
 */
public class Bill {

	private int billId;
	private int appointmentId;
	private String appointmentNo; // used when printing the receipt
	private double treatmentCost;
	private double consultationFee;
	private double discount;
	private double tax;
	private double totalAmount;
	private String paymentMethod; // CASH or CARD
	private int billedBy;
	private LocalDateTime billedAt;

	public Bill() {
		this.paymentMethod = "CASH";
	}

	public int getBillId() {
		return billId;
	}

	public void setBillId(int billId) {
		this.billId = billId;
	}

	public int getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(int appointmentId) {
		this.appointmentId = appointmentId;
	}

	public String getAppointmentNo() {
		return appointmentNo;
	}

	public void setAppointmentNo(String appointmentNo) {
		this.appointmentNo = appointmentNo;
	}

	public double getTreatmentCost() {
		return treatmentCost;
	}

	public void setTreatmentCost(double treatmentCost) {
		this.treatmentCost = treatmentCost;
	}

	public double getConsultationFee() {
		return consultationFee;
	}

	public void setConsultationFee(double consultationFee) {
		this.consultationFee = consultationFee;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double tax) {
		this.tax = tax;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public int getBilledBy() {
		return billedBy;
	}

	public void setBilledBy(int billedBy) {
		this.billedBy = billedBy;
	}

	public LocalDateTime getBilledAt() {
		return billedAt;
	}

	public void setBilledAt(LocalDateTime billedAt) {
		this.billedAt = billedAt;
	}

	@Override
	public String toString() {
		return "Bill of " + appointmentNo + " : Rs. " + totalAmount;
	}
}
