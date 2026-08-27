package com.sunrise.server;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sunrise.controller.JsonUtil;
import com.sunrise.model.DAOFactory;
import com.sunrise.model.Dentist;
import com.sunrise.model.Treatment;
import com.sunrise.model.User;

/**
 * Gives the lists that fill the combo boxes of the registration window:
 *   GET /api/treatments - treatment types with the prices
 *   GET /api/dentists   - dentists with the consultation fee
 */
public class MasterDataHandler extends BaseHandler {

	@Override
	protected void process(HttpExchange exchange) throws Exception {

		User user = requireLogin(exchange);
		if (user == null) {
			return;
		}

		if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
			sendError(exchange, 405, "Only GET is allowed for this address.");
			return;
		}

		String path = exchange.getRequestURI().getPath();

		if (path.contains("treatments")) {
			sendTreatments(exchange);
		} else {
			sendDentists(exchange);
		}
	}

	private void sendTreatments(HttpExchange exchange) throws Exception {
		List<Treatment> treatments = DAOFactory.getTreatmentDAO().findAll();

		List<Map<String, String>> rows = new ArrayList<>();
		for (Treatment t : treatments) {
			Map<String, String> row = new LinkedHashMap<>();
			row.put("treatmentId", String.valueOf(t.getTreatmentId()));
			row.put("treatmentName", t.getTreatmentName());
			row.put("baseCost", String.format("%.2f", t.getBaseCost()));
			row.put("durationMinutes", String.valueOf(t.getDurationMinutes()));
			rows.add(row);
		}

		sendJson(exchange, 200, JsonUtil.toJsonArray(rows));
	}

	private void sendDentists(HttpExchange exchange) throws Exception {
		List<Dentist> dentists = DAOFactory.getDentistDAO().findAll();

		List<Map<String, String>> rows = new ArrayList<>();
		for (Dentist d : dentists) {
			Map<String, String> row = new LinkedHashMap<>();
			row.put("dentistId", String.valueOf(d.getDentistId()));
			row.put("dentistName", d.getDentistName());
			row.put("specialization", d.getSpecialization());
			row.put("consultationFee", String.format("%.2f", d.getConsultationFee()));
			rows.add(row);
		}

		sendJson(exchange, 200, JsonUtil.toJsonArray(rows));
	}
}
