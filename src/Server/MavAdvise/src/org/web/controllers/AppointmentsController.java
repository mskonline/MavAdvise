package org.web.controllers;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.web.beans.Response;
import org.web.dao.DBManager;

/**
 * Controller for handling all the Appointments
 * 
 * @author remeshsv
 */

@RestController
public class AppointmentsController {

	@Autowired
	private DBManager dbmanager;

	@PostMapping("/getAppointments")
	@ResponseBody
	public Response getAppointments(@RequestParam("netID") String netID) {
		final Response response = new Response();
		final List<Object> appointments = dbmanager.getAppointments(netID);

		response.setResult(appointments);
		return response;
	}

	@PostMapping("/getScheduledAppointments")
	@ResponseBody
	public Response getScheduledApps(@RequestParam("netID") String netID) {
		final Response response = new Response();
		final List<Object> appointments = dbmanager.getScheduledApps(netID);

		response.setResult(appointments);
		return response;
	}

	@PostMapping("/getAdvisors")
	@ResponseBody
	public Response getAdvisors(@RequestParam("branch") String branch) {
		final Response response = new Response();
		final List<org.web.beans.User> advisors = dbmanager.getAdvisors(branch);

		response.setResult(advisors);
		return response;
	}

	@PostMapping("/cancelAppointments")
	@ResponseBody
	public Response cancelAppointments(@RequestParam("netID") String netID,
			@RequestParam("sessionID") Integer[] appointmentID) {
		final Response response = new Response();
		final List<Object> appointments = dbmanager.cancelAppointments(netID, appointmentID);

		response.setResult(appointments);
		return response;
	}

	@PostMapping("/createAppointment")
	@ResponseBody
	public Response createAppointment(@RequestParam("sessionID") int sessionID, @RequestParam("netID") String netID,
			@RequestParam("date") Date date, @RequestParam("appointmentReason") String appointmentReason) {
		final Response response = dbmanager.createAppointment(sessionID, netID, date, appointmentReason);
		return response;
	}

	@PostMapping("/getSessionDates")
	@ResponseBody
	public Response getSessionDates(@RequestParam("netID") String netID) {
		final Response response = new Response();
		final List<Object> sessions = dbmanager.getSessionDates(netID);

		response.setResult(sessions);
		return response;
	}
}