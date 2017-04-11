package org.web.controllers;

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

	@RequestMapping(value = "/getAppointments", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String getSession(@RequestParam("netID") String netID){
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


//	private boolean checkSessionTimeCollisions(SessionInfo newSessionInfo){
//		boolean collisionFound = false;
//
//		List<SessionInfo> sList = dbmanager.getSessionInfoList(newSessionInfo.getNetID());
//
//		if(sList.isEmpty())
//			return false;
//
//		// Check each sessionInfo
//		for(SessionInfo aSessionInfo : sList){
//
//			if(isNewSessionInBetweenAnother(newSessionInfo, aSessionInfo)){
//
//				int aFrequecny = Integer.parseInt(aSessionInfo.getFrequency(), 2);
//				int newFrequency = Integer.parseInt(newSessionInfo.getFrequency(), 2);
//
//				// Check collision in the days of week
//				if((aFrequecny & newFrequency) != 0){
//					Time endTime = aSessionInfo.getEndTime();
//					Time startTime = newSessionInfo.getStartTime();
//
//					if(startTime.getTime() < endTime.getTime()){
//						collisionFound = true;
//						break;
//					}
//				}
//			}
//		}
//
//		return collisionFound;
//	}
//
//	private void adjustSessionDates(SessionInfo sessionInfo){
//		int[] dayOfWeek = new int[8];
//		java.sql.Date d;
//
//		for(int i = 0; i < sessionInfo.getFrequency().length(); ++i)
//			dayOfWeek[i + 1] = Character.getNumericValue(sessionInfo.getFrequency().charAt(i));
//
//		DateTime start = DateTime.parse(sessionInfo.getStartDate().toString());
//		DateTime end = DateTime.parse(sessionInfo.getEndDate().toString());
//		DateTime tmp = start;
//
//		// Adjust start date
//		while(!tmp.isAfter(end)) {
//			if(dayOfWeek[tmp.getDayOfWeek()] == 1){
//				start = tmp;
//				break;
//			}
//
//			tmp = tmp.plusDays(1);
//		}
//
//		tmp = end;
//
//		// Adjust end date
//		while(!tmp.isBefore(start)) {
//			if(dayOfWeek[tmp.getDayOfWeek()] == 1){
//				end = tmp;
//				break;
//			}
//
//			tmp = tmp.minusDays(1);
//		}
//
//		d = new java.sql.Date(start.getMillis());
//		sessionInfo.setStartDate(d);
//
//		d = new java.sql.Date(end.getMillis());
//		sessionInfo.setEndDate(d);
//	}
//
//	private boolean isNewSessionInBetweenAnother(SessionInfo newSessionInfo, SessionInfo aSessionInfo){
//		DateTime newstartDate = DateTime.parse(newSessionInfo.getStartDate().toString());
//		DateTime newendDate = DateTime.parse(newSessionInfo.getEndDate().toString());
//
//		DateTime startDate = DateTime.parse(aSessionInfo.getStartDate().toString());
//		DateTime endDate = DateTime.parse(aSessionInfo.getEndDate().toString());
//
//		if(newstartDate.isEqual(startDate))
//			return true;
//
//		if(newstartDate.isAfter(startDate) && newstartDate.isBefore(endDate))
//			return true;
//
//		if(newendDate.equals(endDate))
//			return true;
//
//		if(newendDate.isBefore(endDate) && newendDate.isAfter(startDate))
//			return true;
//
//		return false;
//	}

}
