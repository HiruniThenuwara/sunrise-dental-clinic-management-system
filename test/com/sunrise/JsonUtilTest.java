package com.sunrise;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.sunrise.controller.JsonUtil;

/**
 * Tests of the hand written JSON reader and writer.
 */
public class JsonUtilTest {

	@Test
	public void mapIsWrittenAsJsonObject() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("appointmentNo", "APT-20260825-001");
		map.put("status", "BOOKED");

		assertEquals("{\"appointmentNo\":\"APT-20260825-001\",\"status\":\"BOOKED\"}",
				JsonUtil.toJson(map));
	}

	@Test
	public void jsonObjectIsReadBackIntoAMap() {
		Map<String, String> map = JsonUtil.parse(
				"{\"username\":\"kamal\",\"password\":\"kamal123\"}");

		assertEquals("kamal", map.get("username"));
		assertEquals("kamal123", map.get("password"));
	}

	@Test
	public void writingAndReadingGivesTheSameValues() {
		Map<String, String> original = new LinkedHashMap<>();
		original.put("patientName", "Sunil Bandara");
		original.put("address", "No 45, Galle Road, Colombo 03");
		original.put("contactNumber", "0771234567");

		Map<String, String> result = JsonUtil.parse(JsonUtil.toJson(original));

		assertEquals(original, result);
	}

	@Test
	public void quotesInsideTheTextAreEscaped() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("note", "Patient said \"it hurts\"");

		String json = JsonUtil.toJson(map);

		assertTrue(json.contains("\\\""));
		assertEquals("Patient said \"it hurts\"", JsonUtil.parse(json).get("note"));
	}

	@Test
	public void listOfMapsIsWrittenAsJsonArray() {
		Map<String, String> first = new LinkedHashMap<>();
		first.put("treatmentName", "Tooth Filling");
		Map<String, String> second = new LinkedHashMap<>();
		second.put("treatmentName", "Dental X-Ray");

		String json = JsonUtil.toJsonArray(List.of(first, second));

		assertEquals("[{\"treatmentName\":\"Tooth Filling\"},{\"treatmentName\":\"Dental X-Ray\"}]", json);
	}

	@Test
	public void jsonArrayIsReadBackIntoAList() {
		List<Map<String, String>> rows = JsonUtil.parseArray(
				"[{\"dentistId\":\"1\",\"dentistName\":\"Dr. Saman Perera\"},"
				+ "{\"dentistId\":\"2\",\"dentistName\":\"Dr. Anusha Fernando\"}]");

		assertEquals(2, rows.size());
		assertEquals("Dr. Anusha Fernando", rows.get(1).get("dentistName"));
	}

	@Test
	public void emptyTextGivesAnEmptyMap() {
		assertTrue(JsonUtil.parse("{}").isEmpty());
		assertTrue(JsonUtil.parse(null).isEmpty());
	}
}
