package org.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
public class UsersController {

	final static Logger logger = Logger.getLogger(UsersController.class);

	@Autowired
	private DBManager dbmanager;

	@RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String login(HttpServletRequest request,
			@RequestParam("netID") String netID,
			@RequestParam("password") String password,
			@RequestParam("deviceID") String deviceID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		User user = dbmanager.getUser(netID);
		if(user != null){
			boolean status;
			status = user.authenticate(password);

			if(status == true){
				HttpSession session = request.getSession();
				session.setAttribute("hasAccess", "true");

				if(StringUtils.isNotBlank(deviceID)){
					logger.debug("New DeviceID : " + deviceID);
					dbmanager.updateUserDeviceID(user.getNetID(), deviceID);
				}

				user.setDeviceID(null);
				user.setPassword(null);
				user.setSecurityQuestionID(-1);
				user.setSecurityAnswer(null);
				r.setResult(user);
			} else {
				r.setMessage("Invalid password");
			}
		} else {
			r.setMessage("NetID doesn't exists");
		}

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/register", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String register(HttpServletRequest request, @ModelAttribute User user){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		String msg = dbmanager.saveUser(user);

		if(msg == null)
			r.setResult("User registered");
		else
			r.setMessage(msg);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/updateDeviceID", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String updateDeviceID(@RequestParam("newDeviceID") String newDeviceID,
			@RequestParam("oldDeviceID") String oldDeviceID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		dbmanager.updateDeviceID(newDeviceID, oldDeviceID);

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/updatePassword", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String updatePassword(@RequestParam("netID") String netId,
			@RequestParam("password") String password){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		dbmanager.updatePassword(netId, password);
		r.setResult("Successful");

		try {
			r.setMessage("Done!!");
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/changePassword", method = {RequestMethod.POST}, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String changePassword(@RequestParam("netID") String netId,
			@RequestParam("oldPassword") String oldPwd,
		@RequestParam("newPassword") String newPwd){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();

		String msg = dbmanager.changePassword(netId, oldPwd,newPwd);
		r.setResult("Successfull");

		try {
			r.setMessage("Done!!");
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/logout", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String logout(HttpSession session, @RequestParam("netID") String netID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();
		session.removeAttribute("hasAccess");

		session.invalidate();
		session = null;

		try {
			r.setMessage("Successfully logged out");
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}

	@RequestMapping(value = "/getUser", method = {RequestMethod.POST} , produces = "application/json; charset=utf-8")
	@ResponseBody
	public String returnUser(@RequestParam("netID") String netID){
		Response r = new Response();
		ObjectMapper mapper = new ObjectMapper();
		User user = dbmanager.getUser(netID);

		if(user != null){
			user.setDeviceID(null);
			r.setResult(user);
		}else{
			r.setMessage("NetID does not exists");
		}

		try {
			return mapper.writeValueAsString(r);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "{}";
		}
	}
}
