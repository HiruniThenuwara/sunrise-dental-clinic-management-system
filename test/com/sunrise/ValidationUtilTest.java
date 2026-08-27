package com.sunrise;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

import com.sunrise.controller.ValidationUtil;

/**
 * Tests for the input validation rules.
 * These tests were written before ValidationUtil was created.
 */
public class ValidationUtilTest {

	// ---------- patient name ----------

	@Test
	public void nameWithLettersAndSpacesIsValid() {
		assertTrue(ValidationUtil.isValidName("Sunil Bandara"));
	}

	@Test
	public void nameWithNumbersIsNotValid() {
		assertFalse(ValidationUtil.isValidName("Sunil 123"));
	}

	@Test
	public void emptyNameIsNotValid() {
		assertFalse(ValidationUtil.isValidName(""));
		assertFalse(ValidationUtil.isValidName("   "));
		assertFalse(ValidationUtil.isValidName(null));
	}

	@Test
	public void shortNameIsNotValid() {
		// the rule is minimum 3 letters
		assertFalse(ValidationUtil.isValidName("Su"));
		assertTrue(ValidationUtil.isValidName("Sun"));
	}

	// ---------- contact number ----------

	@Test
	public void tenDigitNumberStartingWithZeroIsValid() {
		assertTrue(ValidationUtil.isValidContact("0771234567"));
	}

	@Test
	public void contactNumberWithWrongLengthIsNotValid() {
		assertFalse(ValidationUtil.isValidContact("07712345"));
		assertFalse(ValidationUtil.isValidContact("07712345678"));
	}

	@Test
	public void contactNumberWithLettersIsNotValid() {
		assertFalse(ValidationUtil.isValidContact("077ABC4567"));
	}

	@Test
	public void contactNumberMustStartWithZero() {
		assertFalse(ValidationUtil.isValidContact("7712345678"));
	}

	// ---------- address ----------

	@Test
	public void normalAddressIsValid() {
		assertTrue(ValidationUtil.isValidAddress("No 45, Galle Road, Colombo 03"));
	}

	@Test
	public void emptyAddressIsNotValid() {
		assertFalse(ValidationUtil.isValidAddress(" "));
	}

	// ---------- appointment date ----------

	@Test
	public void todayAndFutureDateAreValid() {
		assertTrue(ValidationUtil.isFutureDate(LocalDate.now()));
		assertTrue(ValidationUtil.isFutureDate(LocalDate.now().plusDays(5)));
	}

	@Test
	public void pastDateIsNotValid() {
		assertFalse(ValidationUtil.isFutureDate(LocalDate.now().minusDays(1)));
		assertFalse(ValidationUtil.isFutureDate(null));
	}

	// ---------- appointment time ----------

	@Test
	public void timeInsideClinicHoursIsValid() {
		assertTrue(ValidationUtil.isClinicTime(LocalTime.of(8, 0)));
		assertTrue(ValidationUtil.isClinicTime(LocalTime.of(14, 30)));
		assertTrue(ValidationUtil.isClinicTime(LocalTime.of(19, 30)));
	}

	@Test
	public void timeOutsideClinicHoursIsNotValid() {
		assertFalse(ValidationUtil.isClinicTime(LocalTime.of(7, 59)));
		assertFalse(ValidationUtil.isClinicTime(LocalTime.of(20, 0)));
		assertFalse(ValidationUtil.isClinicTime(null));
	}

	// ---------- NIC (optional field) ----------

	@Test
	public void oldAndNewNicNumbersAreValid() {
		assertTrue(ValidationUtil.isValidNic("199012345678"));
		assertTrue(ValidationUtil.isValidNic("901234567V"));
	}

	@Test
	public void emptyNicIsAllowedBecauseTheFieldIsOptional() {
		assertTrue(ValidationUtil.isValidNic(""));
		assertTrue(ValidationUtil.isValidNic(null));
	}

	@Test
	public void wrongNicIsNotValid() {
		assertFalse(ValidationUtil.isValidNic("12345"));
	}
}
