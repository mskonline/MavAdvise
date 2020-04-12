package org.web.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.web.beans.Announcement;
import org.web.beans.Response;
import org.web.dao.DBManager;

/**
 * Controller for handling all the Announcements
 * 
 * @author gurleenkaur793
 */

@RestController
public class AnnouncementController {

	@Autowired
	private DBManager dbmanager;

	@PostMapping("/getAllAnnouncements")
	@ResponseBody
	public Response getAllAppointments(@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate, @RequestParam("branch") String branch,
			@RequestParam(value = "netId", required = false) String netId) {
		final Response response = new Response();
		final List<Object> announcements = dbmanager.getAllAnnouncements(startDate, endDate, branch, netId);

		response.setResult(announcements);
		return response;
	}

	@PostMapping("/createAnnouncement")
	@ResponseBody
	public Response register(HttpServletRequest request, @ModelAttribute Announcement announcement) {
		final Response response = new Response();
		final String msg = dbmanager.saveAnnouncement(announcement);

		if (msg == null) {
			response.setResult("Announcement Posted");
		} else {
			response.setMessage(msg);
		}

		return response;
	}

	@PostMapping("/deleteAnnouncement")
	@ResponseBody
	public Response deleteAnnouncement(@RequestParam("announcementID") int announcementId) {
		final Response response = new Response();
		final String msg = dbmanager.deleteAnnouncement(announcementId);

		if (msg == null) {
			response.setResult("Announcement Deleted");
		} else {
			response.setMessage(msg);
		}

		return response;
	}
}
