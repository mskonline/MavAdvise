package org.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.beans.Response;
import org.web.beans.User;
import org.web.dao.DBManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SessionController{

	@Autowired
	private DBManager dbmanager;

	@RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String login(HttpServletRequest request, @RequestParam("netID") String netID,
			@RequestParam("password") String password){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		User user = dbmanager.getUser(netID);
		if(user != null){
			boolean status;
			status = user.authenticate(password);

			if(status == true){
				HttpSession session = request.getSession();
				session.setAttribute("hasAccess", "true");
				r.setResult(user);
			} else {
				r.setType("failed");
				r.setMessage("Invalid password");
			}
		}
		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	//TODO - remove GET
	@RequestMapping(value = "/register", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String register(HttpServletRequest request, @ModelAttribute User user){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		String msg = dbmanager.saveUser(user);

		if(msg == null)
			r.setResult("User registered");
		else
			// failure. Send message to App
			r.setMessage(msg);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	@ResponseBody
	public void logout(HttpServletRequest request, @RequestParam("netID") String netID){

	}
}
