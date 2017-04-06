package org.web.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.beans.Response;
import org.web.beans.Session;
import org.web.dao.DBManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SessionsController{
	@Autowired
	private DBManager dbmanager;

	@RequestMapping(value = "/addSessions", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String createSession(HttpServletRequest request, @ModelAttribute Session session){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		r.setResult(session);
		dbmanager.addSessions(session);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
}
