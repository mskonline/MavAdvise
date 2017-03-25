package org.web.app;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class AppSessionInterceptor extends HandlerInterceptorAdapter {

	final static Logger logger = Logger.getLogger(AppSessionInterceptor.class);
	private List<String> excludeURLs;

	public AppSessionInterceptor() {
		excludeURLs = new ArrayList<String>();
		excludeURLs.add("login");
		excludeURLs.add("registerUser");
		excludeURLs.add("ping");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String requestMethod = ((HandlerMethod) handler).getMethod().getName();

		if (!excludeURLs.contains(requestMethod)) {
			HttpSession session = request.getSession(false);
			if (session == null || session.getAttribute("hasAccess") == null) {
				logger.info("Access denied for this request : " + request.getRequestURL());
				return false;
			}
		}

		return true;
	}
}
