package org.web.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.web.beans.Response;
import org.web.beans.Session;
import org.web.beans.SessionInfo;
import org.web.beans.User;
import org.web.dao.DBManager;
import org.web.services.NotificationService;
import org.web.services.SessionsService;

/**
 * Controller for handling all the Sessions
 * 
 * @author mskonline
 */

@RestController
public class SessionsController {
	@Autowired
	private DBManager dbmanager;

	final static Logger logger = Logger.getLogger(SessionsController.class);

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SessionsService sessionsService;

	@PostMapping("/addSessions")
	@ResponseBody
	public Response createSession(HttpServletRequest request, @ModelAttribute SessionInfo sessionInfo) {
		final Response response = new Response();

		sessionsService.adjustSessionDates(sessionInfo);
		final Map<String, List<Object>> sessions = dbmanager.addSessions(sessionInfo);

		response.setResult(sessions);
		return response;
	}

	@PostMapping("/getSessions")
	@ResponseBody
	public Response getSession(@RequestParam("netID") String netID) {
		final Response response = new Response();
		final List<Object> sessions = dbmanager.getAllSessions(netID);
		
		response.setResult(sessions);
		return response;
	}

	@PostMapping("/getScheduledSessions")
	@ResponseBody
	public Response getScheduledSessions(@RequestParam("netID") String netID, @RequestParam("date") String date) {
		final Response response = new Response();
		final List<Object> sessions = dbmanager.getScheduledSessionsUpto(netID, date);
		
		response.setResult(sessions);
		return response;
	}

	@PostMapping("/getSessionAppointments")
	@ResponseBody
	public Response getSessionAppointments(@RequestParam("sessionID") Integer sessionID) {
		final Response response = new Response();
		final List<Object> allAppointments = dbmanager.getSessionAppointments(sessionID);
		
		response.setResult(allAppointments);
		return response;
	}

	@PostMapping("/deleteSessions")
	@ResponseBody
	public Response deleteSession(@RequestParam("netID") String netID, @RequestParam("sessionIDs") Integer[] sessionIDs) {
		final Response response = new Response();
		final List<Object> sessions = dbmanager.deleteSessions(netID, sessionIDs);
		
		response.setResult(sessions);
		return response;
	}

	@PostMapping("/startSession")
	@ResponseBody
	public Response startSession(@RequestParam("sessionID") Integer sessionID, @RequestParam("status") String status) {
		final Response response = new Response();
		final List<Object> allAppointments = dbmanager.getSessionAppointments(sessionID);

		if (status.equalsIgnoreCase("SCHEDULED")) {
			List<User> users = dbmanager.startSession(sessionID);
			Session session = dbmanager.getSession(sessionID);

			final String TITLE = "MavAdvise";
			final String MESSAGE = "Advising session has started";
			final String MESSAGEEX = sessionsService.getStartSessionMessage(session);

			notificationService.sendNotification(TITLE, MESSAGE, MESSAGEEX, users);

			// Notify the second appointment
			for (User user : users) {
				logger.debug("firstname  : " + user.getFirstName());
			}

			if (users.size() >= 2) {
				User secondApptUser = users.get(1);

				String MESSAGE_NEXT = "Your appointment is next";
				notificationService.sendNotification(TITLE, MESSAGE_NEXT, null, secondApptUser);
			}
		}

		response.setResult(allAppointments);
		return response;
	}

	@PostMapping(value = "/cancelSession")
	@ResponseBody
	public Response cancelSession(@RequestParam("sessionID") Integer sessionID,
			@RequestParam("cancelReason") String cancelReason) {
		final Response response = new Response();
		final List<User> users = dbmanager.cancelSession(sessionID, cancelReason);

		// Notify all the appointments
		Session session = dbmanager.getSession(sessionID);

		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d yyyy");
		String sDate = sdf.format(session.getDate());

		final String TITLE = "MavAdvise";
		final String MESSAGE = "Advising session on " + sDate + " was cancelled";

		notificationService.sendNotification(TITLE, MESSAGE, cancelReason, users);

		response.setResult("Session Cancelled");
		return response;
	}

	@PostMapping(value = "/advanceSession")
	@ResponseBody
	public Response advanceSession(@RequestParam("sessionID") Integer sessionID,
			@RequestParam("nextAppointmentID") Integer nextAppointmentID,
			@RequestParam("prevAppointmentID") Integer prevAppointmentID, @RequestParam("noShow") String noShow) {
		logger.debug("prevAppointmentID : " + prevAppointmentID + " nextAppointmentID : " + nextAppointmentID);

		final Response response = new Response();
		String msg = "Session: Next appointment notified";

		if (nextAppointmentID != 0) {
			// Get user of appointmentID
			User user = dbmanager.getUserForAppointment(nextAppointmentID);

			// Notify the user
			final String TITLE = "MavAdvise";
			final String MESSAGE = "Your appointment is next";

			notificationService.sendNotification(TITLE, MESSAGE, null, user);
		}

		// Mark prevAppointmentID as Done or No Show
		if (noShow.equalsIgnoreCase("N")) {
			dbmanager.markAppointmentAsDone(prevAppointmentID);
			msg = "DONE";
		} else {
			dbmanager.markAppointmentAsNoShow(prevAppointmentID);

			final String TITLE = "MavAdvise";
			final String MESSAGE = "Your appointment was marked as NO SHOW";

			User u = dbmanager.getUserForAppointment(prevAppointmentID);
			notificationService.sendNotification(TITLE, MESSAGE, null, u);

			msg = "NO SHOW";
		}

		if (nextAppointmentID == -1) {
			// Mark session as done
			msg = dbmanager.markSessionAsDone(sessionID);
		}

		response.setResult(msg);
		return response;
	}

	@PostMapping(value = "/cancelSessionAppointment")
	@ResponseBody
	public Response cancelSessionAppointment(@RequestParam("netID") String netID,
			@RequestParam("appointmentID") Integer[] appointmentID, @RequestParam("sessionID") Integer sessionID,
			@RequestParam("cancelReason") String cancelReason) {
		final Response response = new Response();
		final List<Object> appointments = dbmanager.cancelAppointments(netID, appointmentID);
		
		response.setResult(appointments);

		if (StringUtils.isNotBlank(cancelReason)) {
			final String TITLE = "MavAdvise";
			final String MESSAGE = "Your appointment was Cancelled";

			User u = dbmanager.getUserForAppointment(appointmentID[0]);
			Session session = dbmanager.getSession(sessionID);

			String messageBody = sessionsService.getCancelAppointmentMessage(session, cancelReason);
			notificationService.sendNotification(TITLE, MESSAGE, messageBody, u);
		}

		return response;
	}

	@GetMapping("/sendNotification")
	@ResponseBody
	public Response sendNotification(@RequestParam("netID") String netID) {
		final Response response = new Response();
		final User user = dbmanager.getUser(netID);

		if (user != null) {
			System.out.println("Device ID : " + user.getDeviceID());

			User user1 = new User();
			user.setDeviceID(
					"dJQ3iY_rsO8:APA91bEG2bJdFW9vfF0tMkzX-A22LpchEJVaQEIXBQRmLPTSb-R-4PXnFMQw_kEJ89Q2mXZgvgy6JlEzFfJTqXr20MksCo-PmkAOs5pu6HMX9MUPYeZWwtcpBMaYjKnxC42Lnh09-jkJ");

			User user2 = new User();
			user2.setDeviceID(
					"cYHoRWH5n6k:APA91bH6kmHqoEmY-GAowLYGkwu_eC7RXfgxZwjg0RXR6Z9HZ7ADvLZBpd3R4VSl0BeXABJPUnmSlbsAjGWZ6s_R5c1MJjX8u_930e92ruyRFDC-tRzpjdb0Lf4WnmB0sN61TAZL7iA2");

			List<User> users = new ArrayList<User>();
			users.add(user1);
			users.add(user2);

			notificationService.sendNotification("Demo", "Demo Message", null, users);
		} else
			System.out.println("No such user " + netID);

		return response;
	}
}
