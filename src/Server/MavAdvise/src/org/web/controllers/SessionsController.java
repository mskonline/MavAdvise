package org.web.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.beans.Response;
import org.web.beans.Session;
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

		List<Object> sessions = dbmanager.getSessions(netID);
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
	public String startSession(@RequestParam("sessionID") Integer sessionID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		Session session = null;
		List<User> users = null;

		String title = "MavAdvise";
		String message = "";

		notificationService.sendNotification(title, message, users);
		r.setResult("{}");

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
			@RequestParam("appointmentID") Integer appointmentID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();


		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/setAppointmentAsNoShow", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String setAppointmentAsNoShow(@RequestParam("appointmentID") Integer appointmentID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();


		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
}
