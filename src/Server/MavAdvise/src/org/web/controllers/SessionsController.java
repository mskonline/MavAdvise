package org.web.controllers;

import java.util.List;
import java.util.Map;

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
public class SessionsController{
	@Autowired
	private DBManager dbmanager;

	@RequestMapping(value = "/addSessions", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String createSession(HttpServletRequest request, @ModelAttribute SessionInfo sessionInfo){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		adjustSessionDates(sessionInfo);
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

	@RequestMapping(value = "/getSessionAppointments", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getSessionAppointments(@RequestParam("sessionID") Integer sessionID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

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

	private void adjustSessionDates(SessionInfo sessionInfo){

		if(sessionInfo.getStartDate().equals(sessionInfo.getEndDate()))
			return;

		/**
		 *   M T W T F S S
		 * 0 0 0 0 0 0 0 0
		 */
		int[] dayOfWeek = new int[8];
		java.sql.Date d;

		/**
		 * Frequency
		 * M T W T F S S
		 * 0 0 0 0 0 0 0
		 */
		for(int i = 0; i < sessionInfo.getFrequency().length(); ++i)
			dayOfWeek[i + 1] = Character.getNumericValue(sessionInfo.getFrequency().charAt(i));

		DateTime start = DateTime.parse(sessionInfo.getStartDate().toString());
		DateTime end = DateTime.parse(sessionInfo.getEndDate().toString());
		DateTime tmp = start;

		// Adjust start date
		while(!tmp.isAfter(end)) {
			if(dayOfWeek[tmp.getDayOfWeek()] == 1){
				start = tmp;
				break;
			}

			tmp = tmp.plusDays(1);
		}

		tmp = end;

		// Adjust end date
		while(!tmp.isBefore(start)) {
			if(dayOfWeek[tmp.getDayOfWeek()] == 1){
				end = tmp;
				break;
			}

			tmp = tmp.minusDays(1);
		}

		d = new java.sql.Date(start.getMillis());
		sessionInfo.setStartDate(d);

		d = new java.sql.Date(end.getMillis());
		sessionInfo.setEndDate(d);
	}
}
