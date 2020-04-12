package org.web.app;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * A Handler Interceptor
 * 
 * We will use this to Intercept all the requests to the application.
 * This is a basic authorization of all the requests
 * 
 * @author mskonline
 */

public class AppSessionInterceptor extends HandlerInterceptorAdapter {

	final static Logger logger = Logger.getLogger(AppSessionInterceptor.class);
	private List<String> excludeURLs;

	public AppSessionInterceptor() {
		excludeURLs = new ArrayList<String>();
		excludeURLs.add("login");
		excludeURLs.add("register");
		excludeURLs.add("ping");
		excludeURLs.add("getUser");
		excludeURLs.add("updatePassword");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		/*String requestMethod = ((HandlerMethod) handler).getMethod().getName();

		logger.error("Request Method : " + requestMethod);

		if (!excludeURLs.contains(requestMethod)) {
			HttpSession session = request.getSession(false);
			if (session == null || session.getAttribute("hasAccess") == null) {
				logger.error("Access denied for this request : " + request.getRequestURL());
				return false;
			}
		}
*/
		return true;
	}
}
