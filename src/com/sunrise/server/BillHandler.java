package com.sunrise.server;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sunrise.controller.JsonUtil;
import com.sunrise.model.Bill;
import com.sunrise.model.User;

/**
 * Web service endpoints of the billing:
 *   POST /api/bills          - calculate and save the bill
 *   GET  /api/bills/{number} - read the bill again for the receipt
 */
public class BillHandler extends BaseHandler {

	@Override
	protected void process(HttpExchange exchange) throws Exception {

		User user = requireLogin(exchange);
		if (user == null) {
			return;
		}

		String method = exchange.getRequestMethod();

		if ("POST".equalsIgnoreCase(method)) {
			createBill(exchange, user);

		} else if ("GET".equalsIgnoreCase(method)) {
			readBill(exchange);

		} else {
			sendError(exchange, 405, "This method is not allowed for the bills.");
		}
	}

	private void createBill(HttpExchange exchange, User user) throws Exception {
		Map<String, String> request = JsonUtil.parse(readBody(exchange));

		String appointmentNo = request.get("appointmentNo");
		String patientType = request.get("patientType");   // STANDARD, SENIOR or EMERGENCY
		String paymentMethod = request.get("paymentMethod");

		BillingService billingService = new BillingService();
		billingService.setStrategy(selectStrategy(patientType));

		Bill bill = billingService.generateBill(appointmentNo, user.getUserId(), paymentMethod);

		Map<String, String> response = toMap(bill);
		response.put("billingType", billingService.getStrategy().getName());

		sendJson(exchange, 201, JsonUtil.toJson(response));
	}

	private void readBill(HttpExchange exchange) throws Exception {
		String appointmentNo = lastPathPart(exchange);

		Bill bill = new BillingService().findBill(appointmentNo);
		sendJson(exchange, 200, JsonUtil.toJson(toMap(bill)));
	}

	/**
	 * Selects the billing rule. This is the place where the Strategy pattern
	 * is used: the handler only chooses the object, the calculation itself is
	 * inside the strategy class.
	 */
	private BillingStrategy selectStrategy(String patientType) {
		if ("SENIOR".equalsIgnoreCase(patientType)) {
			return new SeniorCitizenDiscountStrategy();
		}
		if ("EMERGENCY".equalsIgnoreCase(patientType)) {
			return new EmergencyBillingStrategy();
		}
		return new StandardBillingStrategy();
	}

	private Map<String, String> toMap(Bill bill) {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("billId", String.valueOf(bill.getBillId()));
		map.put("appointmentNo", bill.getAppointmentNo());
		map.put("treatmentCost", String.format("%.2f", bill.getTreatmentCost()));
		map.put("consultationFee", String.format("%.2f", bill.getConsultationFee()));
		map.put("discount", String.format("%.2f", bill.getDiscount()));
		map.put("tax", String.format("%.2f", bill.getTax()));
		map.put("totalAmount", String.format("%.2f", bill.getTotalAmount()));
		map.put("paymentMethod", bill.getPaymentMethod());
		return map;
	}
}
