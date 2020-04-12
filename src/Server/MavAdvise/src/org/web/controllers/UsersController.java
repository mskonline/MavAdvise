package org.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.web.beans.Response;
import org.web.beans.User;
import org.web.dao.DBManager;

/**
 * Controller for handling all the Users
 * 
 * @author mskonline
 */

@RestController
public class UsersController {
	final static Logger logger = Logger.getLogger(UsersController.class);

	@Autowired
	private DBManager dbmanager;

	@PostMapping("/login")
	@ResponseBody
	public Response login(HttpServletRequest request, @RequestParam("netID") String netID,
			@RequestParam("password") String password, @RequestParam("deviceID") String deviceID) {
		final Response response = new Response();

		final User user = dbmanager.getUser(netID);

		if (user != null) {
			boolean status;
			status = user.authenticate(password);

			if (status) {
				HttpSession session = request.getSession();
				session.setAttribute("hasAccess", "true");

				if (StringUtils.isNotBlank(deviceID)) {
					logger.debug("New DeviceID : " + deviceID);
					dbmanager.updateUserDeviceID(user.getNetID(), deviceID);
				}

				user.setDeviceID(null);
				user.setPassword(null);
				user.setSecurityQuestionID(-1);
				user.setSecurityAnswer(null);
				response.setResult(user);
			} else {
				response.setMessage("Invalid password");
			}
		} else {
			response.setMessage("NetID doesn't exists");
		}

		return response;
	}

	@PostMapping("/register")
	@ResponseBody
	public Response register(HttpServletRequest request, @ModelAttribute User user) {
		final Response response = new Response();
		final String msg = dbmanager.saveUser(user);

		if (msg == null) {
			response.setResult("User registered");
		} else {
			response.setMessage(msg);
		}

		return response;
	}

	@PostMapping("/logout")
	@ResponseBody
	public Response logout(HttpSession session, @RequestParam("netID") String netID) {
		final Response response = new Response();

		session.removeAttribute("hasAccess");
		session.invalidate();
		session = null;

		response.setMessage("Successfully logged out");
		return response;
	}

	@PostMapping("/updateDeviceID")
	@ResponseBody
	public Response updateDeviceID(@RequestParam("newDeviceID") String newDeviceID,
			@RequestParam("oldDeviceID") String oldDeviceID) {
		final Response response = new Response();
		dbmanager.updateDeviceID(newDeviceID, oldDeviceID);

		return response;
	}

	@PostMapping("/updatePassword")
	@ResponseBody
	public Response updatePassword(@RequestParam("netID") String netId, @RequestParam("password") String password) {
		final Response response = new Response();

		dbmanager.updatePassword(netId, password);
		response.setResult("Successful");

		return response;
	}

	@PostMapping("/changePassword")
	@ResponseBody
	public Response changePassword(@RequestParam("netID") String netId, @RequestParam("oldPassword") String oldPwd,
			@RequestParam("newPassword") String newPwd) {
		final Response response = new Response();

		final String message = dbmanager.changePassword(netId, oldPwd, newPwd);

		response.setResult("Successfull");
		return response;
	}

	@PostMapping("/getUser")
	@ResponseBody
	public Response returnUser(@RequestParam("netID") String netID) {
		final Response response = new Response();
		final User user = dbmanager.getUser(netID);

		if (user != null) {
			user.setDeviceID(null);
			response.setResult(user);
		} else {
			response.setMessage("NetID does not exists");
		}

		return response;
	}
}
