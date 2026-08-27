package com.sunrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sunrise.controller.ClinicException;
import com.sunrise.model.Appointment;
import com.sunrise.model.Bill;
import com.sunrise.model.BillDAO;
import com.sunrise.model.IAppointmentDAO;
import com.sunrise.server.BillingService;
import com.sunrise.server.EmergencyBillingStrategy;
import com.sunrise.server.SeniorCitizenDiscountStrategy;
import com.sunrise.server.StandardBillingStrategy;

/**
 * Tests of the bill calculation and of the three billing strategies.
 * Fake DAO classes are used, so no database is needed.
 *
 * Test data: Scaling and Cleaning = 3500.00, consultation fee = 1500.00,
 * tax = 2%.
 */
public class BillingServiceTest {

	private static final double TREATMENT_COST = 3500.00;
	private static final double CONSULTATION_FEE = 1500.00;
	private static final double TAX_RATE = 0.02;

	private BillingService billingService;

	@BeforeEach
	public void setUp() {
		billingService = new BillingService(new FakeAppointmentDAO(), new FakeBillDAO(), TAX_RATE);
	}

	// ---------- the three strategies ----------

	@Test
	public void standardStrategyAddsTreatmentAndConsultation() {
		StandardBillingStrategy strategy = new StandardBillingStrategy();

		assertEquals(5000.00, strategy.calculate(TREATMENT_COST, CONSULTATION_FEE), 0.001);
	}

	@Test
	public void seniorCitizenStrategyGivesTenPercentDiscount() {
		SeniorCitizenDiscountStrategy strategy = new SeniorCitizenDiscountStrategy(0.10);

		// 5000 - 10% = 4500
		assertEquals(4500.00, strategy.calculate(TREATMENT_COST, CONSULTATION_FEE), 0.001);
	}

	@Test
	public void emergencyStrategyAddsTheExtraCharge() {
		EmergencyBillingStrategy strategy = new EmergencyBillingStrategy(1500.00);

		assertEquals(6500.00, strategy.calculate(TREATMENT_COST, CONSULTATION_FEE), 0.001);
	}

	// ---------- the whole bill ----------

	@Test
	public void standardBillTotalIsCorrect() throws ClinicException {
		Bill bill = billingService.generateBill("APT-20260825-001", 2, "CASH");

		// 3500 + 1500 = 5000, tax 2% = 100, total = 5100
		assertEquals(3500.00, bill.getTreatmentCost(), 0.001);
		assertEquals(1500.00, bill.getConsultationFee(), 0.001);
		assertEquals(0.00, bill.getDiscount(), 0.001);
		assertEquals(100.00, bill.getTax(), 0.001);
		assertEquals(5100.00, bill.getTotalAmount(), 0.001);
	}

	@Test
	public void seniorCitizenBillHasDiscountAndSmallerTotal() throws ClinicException {
		billingService.setStrategy(new SeniorCitizenDiscountStrategy(0.10));

		Bill bill = billingService.generateBill("APT-20260825-001", 2, "CASH");

		// 5000 - 500 = 4500, tax 2% = 90, total = 4590
		assertEquals(500.00, bill.getDiscount(), 0.001);
		assertEquals(90.00, bill.getTax(), 0.001);
		assertEquals(4590.00, bill.getTotalAmount(), 0.001);
	}

	@Test
	public void emergencyBillIsBiggerThanTheStandardBill() throws ClinicException {
		billingService.setStrategy(new EmergencyBillingStrategy(1500.00));

		Bill bill = billingService.generateBill("APT-20260825-001", 2, "CASH");

		// 5000 + 1500 = 6500, tax 2% = 130, total = 6630
		assertEquals(0.00, bill.getDiscount(), 0.001);
		assertEquals(6630.00, bill.getTotalAmount(), 0.001);
	}

	@Test
	public void billIsSavedWithTheAppointmentNumber() throws ClinicException {
		Bill bill = billingService.generateBill("APT-20260825-001", 2, "CARD");

		assertEquals("APT-20260825-001", bill.getAppointmentNo());
		assertEquals("CARD", bill.getPaymentMethod());
	}

	@Test
	public void wrongAppointmentNumberMustFail() {
		ClinicException error = assertThrows(ClinicException.class,
				() -> billingService.generateBill("APT-20260825-999", 2, "CASH"));

		assertTrue(error.getMessage().contains("not found"));
	}

	@Test
	public void cancelledAppointmentCannotBeBilled() {
		ClinicException error = assertThrows(ClinicException.class,
				() -> billingService.generateBill("APT-20260825-002", 2, "CASH"));

		assertTrue(error.getMessage().contains("cancelled"));
	}

	// ---------- fake DAO classes ----------

	private static class FakeAppointmentDAO implements IAppointmentDAO {

		@Override
		public String insert(Appointment appointment) {
			return appointment.getAppointmentNo();
		}

		@Override
		public Appointment findByNumber(String appointmentNo) {
			if ("APT-20260825-001".equals(appointmentNo) || "APT-20260825-002".equals(appointmentNo)) {
				Appointment a = new Appointment();
				a.setAppointmentId(1);
				a.setAppointmentNo(appointmentNo);
				a.setPatientName("Sunil Bandara");
				a.setTreatmentName("Scaling and Cleaning");
				a.setTreatmentCost(TREATMENT_COST);
				a.setDentistName("Dr. Saman Perera");
				a.setConsultationFee(CONSULTATION_FEE);
				a.setAppointmentDate(LocalDate.now());
				a.setAppointmentTime(LocalTime.of(9, 0));
				a.setStatus("APT-20260825-002".equals(appointmentNo) ? "CANCELLED" : "BOOKED");
				return a;
			}
			return null;
		}

		@Override
		public List<Appointment> findByDate(LocalDate date) {
			return new ArrayList<>();
		}

		@Override
		public boolean isSlotBooked(int dentistId, LocalDate date, LocalTime time) {
			return false;
		}

		@Override
		public void updateStatus(String appointmentNo, String status) {
			// nothing to do in the fake class
		}
	}

	private static class FakeBillDAO extends BillDAO {

		@Override
		public int insert(Bill bill) {
			bill.setBillId(1);
			return 1;
		}
	}
}
