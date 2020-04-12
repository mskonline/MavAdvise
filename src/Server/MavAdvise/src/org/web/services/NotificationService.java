package org.web.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web.app.AppConfig;
import org.web.beans.User;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Service class for sending notifications to mobile devices using Google's FireBase
 * 
 * @author mskonline
 */

@Service
public class NotificationService {
	private static OkHttpClient httpClient;
	private final String FIREBASE_ENDPOINT;
	private final String SERVER_KEY;
	private boolean isEnabled = true;

	final static Logger logger = Logger.getLogger(NotificationService.class);

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Autowired
	public NotificationService(AppConfig appConfig) {
		final String flag = appConfig.get("notifications.enabled");

		if (flag.equalsIgnoreCase("false")) {
			isEnabled = false;

			FIREBASE_ENDPOINT = null;
			SERVER_KEY = null;
			return;
		}

		if (httpClient == null) {
			httpClient = new OkHttpClient.Builder().build();
		}

		FIREBASE_ENDPOINT = appConfig.get("firebase.end-point");
		SERVER_KEY = appConfig.get("firebase.server-key");
	}

	/**
	 * Sends notification to the user
	 * 
	 * @param title The title of the message
	 * @param message The message
	 * @param messageEx The additional message data
	 * @param user The intended user
	 */
	public void sendNotification(final String title, final String message, final String messageEx, final User user) {
		if (!isEnabled) {
			logger.debug("Notifications disabled");
			return;
		}

		if (user == null)
			return;

		List<User> users = new ArrayList<User>();
		users.add(user);

		FireBaseNotifier fireBaseNotifier = new FireBaseNotifier(title, message, messageEx, users);

		final Thread thread = new Thread(fireBaseNotifier);
		thread.start();
	}

	/**
	 * Sends notification to a list of users
	 * 
	 * @param title The title of the message
	 * @param message The message
	 * @param messageEx The additional message data
	 * @param users List of all users
	 */
	public void sendNotification(final String title, final String message, final String messageEx,
			final List<User> users) {
		if (!isEnabled) {
			logger.debug("Notifications disabled");
			return;
		}

		if (users == null || users.size() == 0)
			return;

		FireBaseNotifier fireBaseNotifier = new FireBaseNotifier(title, message, messageEx, users);
		final Thread thread = new Thread(fireBaseNotifier);

		thread.start();
	}

	private class FireBaseNotifier implements Runnable {
		private String title, message, messageExtra;
		private List<User> users;

		public FireBaseNotifier(String title, String message, String messageExtra, List<User> users) {
			this.title = title;
			this.message = message;
			this.users = users;
			this.messageExtra = messageExtra;
		}

		@Override
		public void run() {
			if (users == null || users.size() == 0) {
				return;
			}

			final JSONObject jmessage = new JSONObject();

			if (users.size() == 1) {
				jmessage.put("to", users.get(0).getDeviceID());
			} else {
				JSONArray array = new JSONArray();

				for (User user : users) {
					array.put(user.getDeviceID());
				}

				jmessage.put("registration_ids", array);
			}

			jmessage.put("priority", "high");
			jmessage.put("time_to_live", 900);

			JSONObject data = new JSONObject();
			data.put("msg", message);

			if (StringUtils.isNotBlank(messageExtra)) {
				data.put("msgEx", messageExtra);
			} else {
				data.put("msgEx", "");
			}

			jmessage.put("data", data);

			final JSONObject notification = new JSONObject();
			notification.put("title", title);
			notification.put("body", message);
			notification.put("sound", "default");
			notification.put("click_action", ".MavAdvise.MavAdviseNotification");

			jmessage.put("notification", notification);

			RequestBody body = RequestBody.create(JSON, jmessage.toString());

			logger.debug("Firebase Request : " + jmessage.toString());

			final Request request = new Request.Builder().url(FIREBASE_ENDPOINT).post(body)
					.addHeader("Content-type", "application/json").addHeader("Authorization", SERVER_KEY).build();

			Response response = null;
			String responseMsg = null;

			try {
				response = httpClient.newCall(request).execute();
				responseMsg = response.body().string();

				logger.debug("Firebase Response : " + responseMsg);
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void test() {
		final User user = new User();
		user.setDeviceID(
				"dJQ3iY_rsO8:APA91bEG2bJdFW9vfF0tMkzX-A22LpchEJVaQEIXBQRmLPTSb-R-4PXnFMQw_kEJ89Q2mXZgvgy6JlEzFfJTqXr20MksCo-PmkAOs5pu6HMX9MUPYeZWwtcpBMaYjKnxC42Lnh09-jkJ");

		final User user2 = new User();
		user2.setDeviceID(
				"cYHoRWH5n6k:APA91bH6kmHqoEmY-GAowLYGkwu_eC7RXfgxZwjg0RXR6Z9HZ7ADvLZBpd3R4VSl0BeXABJPUnmSlbsAjGWZ6s_R5c1MJjX8u_930e92ruyRFDC-tRzpjdb0Lf4WnmB0sN61TAZL7iA2");

		final List<User> users = new ArrayList<User>();
		users.add(user);
		users.add(user2);

		// new NotificationService().sendNotification("Server Notification 3", "Hello
		// World", users);
	}
}
