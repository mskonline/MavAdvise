package org.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.beans.Announcement;
import org.web.beans.Response;
import org.web.dao.DBManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AnnouncementController {

	@Autowired
	private DBManager dbmanager;

	@RequestMapping(value = "/getAllAnnouncements", method = { RequestMethod.GET,
			RequestMethod.POST }, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String getAllAppointments(@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("branch") String branch,
			@RequestParam(value = "netId", required = false) String netId) {
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();
		List<Object> announcements = dbmanager.getAllAnnouncements(startDate, endDate, branch, netId);

		r.setResult(announcements);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/createAnnouncement", method = {
			RequestMethod.POST }, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String register(HttpServletRequest request, @ModelAttribute Announcement announ) {
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		String msg = dbmanager.saveAnnouncement(announ);

		if (msg == null)
			r.setResult("Announcement Posted");
		else
			r.setMessage(msg);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/deleteAnnouncement", method = {
			RequestMethod.POST }, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String deleteAnnouncement(@RequestParam("announcementID") int a_id) {
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		String msg = dbmanager.deleteAnnouncement(a_id);

		if (msg == null)
			r.setResult("Announcement Deleted");
		else
			r.setMessage(msg);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
}
