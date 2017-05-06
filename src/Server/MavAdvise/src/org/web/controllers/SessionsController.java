package org.web.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.beans.Response;
import org.web.beans.SessionInfo;
import org.web.beans.User;
import org.web.dao.DBManager;
import org.web.services.NotificationService;
import org.web.services.SessionsService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SessionsController{
	@Autowired
	private DBManager dbmanager;

	final static Logger logger = Logger.getLogger(SessionsController.class);

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SessionsService sessionsService;

	@RequestMapping(value = "/addSessions", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String createSession(HttpServletRequest request, @ModelAttribute SessionInfo sessionInfo){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		sessionsService.adjustSessionDates(sessionInfo);
		Map<String, List<Object>> sessions = dbmanager.addSessions(sessionInfo);

		r.setResult(sessions);
		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/getSessions", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getSession(@RequestParam("netID") String netID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		List<Object> sessions = dbmanager.getAllSessions(netID);
		r.setResult(sessions);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/getScheduledSessions", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getScheduledSessions(@RequestParam("netID") String netID,
			@RequestParam("date") String date){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		List<Object> sessions = dbmanager.getScheduledSessionsUpto(netID, date);
		r.setResult(sessions);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/getSessionAppointments", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getSessionAppointments(@RequestParam("sessionID") Integer sessionID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		List<Object> allAppointments = dbmanager.getSessionAppointments(sessionID);
		r.setResult(allAppointments);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/deleteSessions", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String deleteSession(@RequestParam("netID") String netID,
			@RequestParam("sessionIDs") Integer[] sessionIDs){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		List<Object> sessions = dbmanager.deleteSessions(netID, sessionIDs);
		r.setResult(sessions);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/startSession", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String startSession(
			@RequestParam("sessionID") Integer sessionID,
			@RequestParam("status") String status){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		if(status.equalsIgnoreCase("SCHEDULED")){
			List<User> users = dbmanager.startSession(sessionID);

			final String TITLE = "MavAdvise";
			final String MESSAGE = "Advising session has started";

			notificationService.sendNotification(TITLE, MESSAGE, users);
		}

		List<Object> allAppointments = dbmanager.getSessionAppointments(sessionID);
		r.setResult(allAppointments);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/cancelSession", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String cancelSession(@RequestParam("sessionID") Integer sessionID,
			@RequestParam("cancelReason") String cancelReason){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		String msg = dbmanager.cancelSession(sessionID, cancelReason);

		if(msg == null)
			r.setResult("Session Cancelled");
		else
			r.setMessage(msg);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/advanceSession", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String advanceSession(@RequestParam("sessionID") Integer sessionID,
			@RequestParam("nextAppointmentID") Integer nextAppointmentID,
			@RequestParam("prevAppointmentID") Integer prevAppointmentID,
			@RequestParam("noShow") String noShow){

		logger.debug("prevAppointmentID : " + prevAppointmentID + " nextAppointmentID : " + nextAppointmentID );

		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		String msg = "Session: Next appointment notified";

		if(nextAppointmentID != 0){
			// Get user of appointmentID
			User user = dbmanager.getUserForAppointment(nextAppointmentID);

			// Notify the user
			final String TITLE = "MavAdvise";
			final String MESSAGE = "Your appointment is next";

			notificationService.sendNotification(TITLE, MESSAGE, user);
		}

		// Mark prevAppointmentID as Done or No Show
		if(noShow.equalsIgnoreCase("N")){
			dbmanager.markAppointmentAsDone(prevAppointmentID);
			msg = "DONE";
		}
		else{
			dbmanager.markAppointmentAsNoShow(prevAppointmentID);

			final String TITLE = "MavAdvise";
			final String MESSAGE = "Your appointment was marked as NO SHOW";

			User u = dbmanager.getUserForAppointment(prevAppointmentID);
			notificationService.sendNotification(TITLE, MESSAGE, u);

			msg = "NO SHOW";
		}


		if(nextAppointmentID == -1){
			// Mark session as done
			msg = dbmanager.markSessionAsDone(sessionID);
		}

		r.setResult(msg);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/sendNotification", method = {RequestMethod.GET}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String sendNotification(@RequestParam("netID") String netID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		User user = dbmanager.getUser(netID);

		if(user != null){
			System.out.println("Device ID : " + user.getDeviceID());

			User user1 = new User();
			user.setDeviceID("dJQ3iY_rsO8:APA91bEG2bJdFW9vfF0tMkzX-A22LpchEJVaQEIXBQRmLPTSb-R-4PXnFMQw_kEJ89Q2mXZgvgy6JlEzFfJTqXr20MksCo-PmkAOs5pu6HMX9MUPYeZWwtcpBMaYjKnxC42Lnh09-jkJ");

			User user2 = new User();
			user2.setDeviceID("cYHoRWH5n6k:APA91bH6kmHqoEmY-GAowLYGkwu_eC7RXfgxZwjg0RXR6Z9HZ7ADvLZBpd3R4VSl0BeXABJPUnmSlbsAjGWZ6s_R5c1MJjX8u_930e92ruyRFDC-tRzpjdb0Lf4WnmB0sN61TAZL7iA2");

			List<User> users = new ArrayList<User>();
			users.add(user1);
			users.add(user2);

			notificationService.sendNotification("Demo", "Demo Message", users);
		}else
			System.out.println("No such user " + netID);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
}
