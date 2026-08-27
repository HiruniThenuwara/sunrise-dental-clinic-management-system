package com.sunrise.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Small JSON reader and writer written by hand, because the assessment does
 * not allow external libraries like Gson.
 *
 * The system only sends flat objects (name and value pairs) and lists of flat
 * objects, so this simple version is enough.
 */
public class JsonUtil {

	/** Makes a JSON object text from a map, for example {"status":"ok"} */
	public static String toJson(Map<String, String> map) {
		StringBuilder json = new StringBuilder("{");

		boolean first = true;
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (!first) {
				json.append(",");
			}
			json.append("\"").append(escape(entry.getKey())).append("\":");

			String value = entry.getValue();
			if (value == null) {
				json.append("null");
			} else {
				json.append("\"").append(escape(value)).append("\"");
			}
			first = false;
		}

		return json.append("}").toString();
	}

	/** Makes a JSON array text from a list of maps. */
	public static String toJsonArray(List<Map<String, String>> rows) {
		StringBuilder json = new StringBuilder("[");

		for (int i = 0; i < rows.size(); i++) {
			if (i > 0) {
				json.append(",");
			}
			json.append(toJson(rows.get(i)));
		}

		return json.append("]").toString();
	}

	/** Reads a flat JSON object text and returns the values in a map. */
	public static Map<String, String> parse(String json) {
		Map<String, String> map = new LinkedHashMap<>();
		if (json == null) {
			return map;
		}

		String text = json.trim();
		if (text.startsWith("{")) {
			text = text.substring(1);
		}
		if (text.endsWith("}")) {
			text = text.substring(0, text.length() - 1);
		}

		String key = null;
		StringBuilder current = new StringBuilder();
		boolean insideText = false;
		boolean escaped = false;
		boolean valuePart = false;

		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);

			if (escaped) {
				current.append(unescapeChar(c));
				escaped = false;
				continue;
			}

			if (c == '\\' && insideText) {
				escaped = true;
				continue;
			}

			if (c == '"') {
				insideText = !insideText;
				continue;
			}

			if (!insideText && c == ':' && !valuePart) {
				key = current.toString().trim();
				current.setLength(0);
				valuePart = true;
				continue;
			}

			if (!insideText && c == ',') {
				if (key != null) {
					map.put(key, cleanValue(current.toString()));
				}
				key = null;
				current.setLength(0);
				valuePart = false;
				continue;
			}

			current.append(c);
		}

		if (key != null) {
			map.put(key, cleanValue(current.toString()));
		}
		return map;
	}

	/** Reads a JSON array of flat objects. */
	public static List<Map<String, String>> parseArray(String json) {
		List<Map<String, String>> rows = new ArrayList<>();
		if (json == null) {
			return rows;
		}

		String text = json.trim();
		int start = text.indexOf('{');
		while (start >= 0) {
			int end = text.indexOf('}', start);
			if (end < 0) {
				break;
			}
			rows.add(parse(text.substring(start, end + 1)));
			start = text.indexOf('{', end);
		}
		return rows;
	}

	private static String cleanValue(String value) {
		String result = value.trim();
		if ("null".equals(result)) {
			return null;
		}
		return result;
	}

	private static String escape(String text) {
		return text.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r")
				.replace("\t", "\\t");
	}

	private static char unescapeChar(char c) {
		switch (c) {
			case 'n':
				return '\n';
			case 'r':
				return '\r';
			case 't':
				return '\t';
			default:
				return c;
		}
	}
}
