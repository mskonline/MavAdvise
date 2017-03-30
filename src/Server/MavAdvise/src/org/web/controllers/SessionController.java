package org.web.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web.beans.User;
import org.web.dao.DBManager;
import org.web.dao.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SessionController{

	@Autowired
	private DBManager dbmanager;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public String login(HttpServletRequest request, @RequestParam("netID") String netID,
			@RequestParam("password") String password){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		User user = dbmanager.getUser(netID);
		r.setMessage("SUCCESS");
		r.setResult(user);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	@ResponseBody
	public String register(HttpServletRequest request, @ModelAttribute User user){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		boolean status = dbmanager.saveUser(user);

		if(status)
			r.setType("SUCCESS");
		else
			r.setType("FAILED");

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
