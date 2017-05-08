package org.web.controllers;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.beans.Response;
import org.web.beans.SessionInfo;
import org.web.dao.DBManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AppointmentsController {
	
	@Autowired
	private DBManager dbmanager;

	@RequestMapping(value = "/getAppointments", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getAppointments(@RequestParam("netID") String netID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		List<Object> appointments = dbmanager.getAppointments(netID);

		r.setResult(appointments);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	@RequestMapping(value = "/getScheduledApps", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getScheduledApps(@RequestParam("netID") String netID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		List<Object> appointments = dbmanager.getScheduledApps(netID);

		r.setResult(appointments);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	@RequestMapping(value = "/getAdvisors", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getAdvisors(@RequestParam("branch") String branch){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		List<org.web.beans.User> advisors = dbmanager.getAdvisors(branch);

		r.setResult(advisors);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	
	@RequestMapping(value = "/deleteAppointments", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String deleteAppointments(@RequestParam("netID") String netID,
			@RequestParam("sessionID") Integer[] appointmentID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		List<Object> appointments = dbmanager.deleteAppointments(netID, appointmentID);
		r.setResult(appointments);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	
	
	
	@RequestMapping(value = "/createAppointment", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String createAppointment(@RequestParam("sessionID") int sessionID,
			@RequestParam("netID") String netID,
	        @RequestParam("date") Date date){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		r = dbmanager.createAppointment(sessionID, netID, date);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
	
	
	@RequestMapping(value = "/getSessionDates", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getSessionDates(@RequestParam("netID") String netID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		List<Object> sessions = dbmanager.getSessionDates(netID);

		r.setResult(sessions);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

}