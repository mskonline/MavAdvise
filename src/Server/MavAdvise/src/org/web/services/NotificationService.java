package org.web.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

@Service
public class NotificationService {

	private static OkHttpClient httpClient;
	private final String FIREBASE_ENDPOINT;
	private final String SERVER_KEY;

	public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	@Autowired
	public NotificationService(AppConfig appConfig){
		if(httpClient == null){
			httpClient = new OkHttpClient.Builder().build();
		}

		FIREBASE_ENDPOINT = appConfig.get("firebase.end-point");
		SERVER_KEY = appConfig.get("firebase.server-key");
	}

	public void sendNotification(String title, String message, User user){
		if(user == null)
			return;

		List<User> users = new ArrayList<User>();
		users.add(user);

		FireBaseNotifier fireBaseNotifier = new FireBaseNotifier(title, message, users);

		Thread t = new Thread(fireBaseNotifier);
		t.start();
	}

	public void sendNotification(String title, String message, List<User> users){
		if(users == null || users.size() == 0)
			return;

		FireBaseNotifier fireBaseNotifier = new FireBaseNotifier(title, message, users);
		Thread t = new Thread(fireBaseNotifier);

		t.start();
	}

	private class FireBaseNotifier implements Runnable{
		private String title, message;
		private List<User> users ;

		public FireBaseNotifier(String title, String message, List<User> users){
			this.title = title;
			this.message = message;
			this.users = users;
		}

		@Override
		public void run() {
			if(users == null || users.size() == 0)
				return;

			JSONObject jmessage = new JSONObject();

			if(users.size() == 1){
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

			JSONObject notification = new JSONObject();
			notification.put("title", title);
			notification.put("body", message);
			notification.put("sound", "default");

			jmessage.put("notification", notification);

			RequestBody body = RequestBody.create(JSON, jmessage.toString());

			Request request = new Request.Builder()
	                .url(FIREBASE_ENDPOINT)
	                .post(body)
	                .addHeader("Content-type", "application/json")
	                .addHeader("Authorization", SERVER_KEY)
	                .build();

			Response response = null;
			String responseMsg = null;
	        try {
	            response = httpClient.newCall(request).execute();
	            responseMsg = response.body().string();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}

	public void test() {
		User user = new User();
		user.setDeviceID("dJQ3iY_rsO8:APA91bEG2bJdFW9vfF0tMkzX-A22LpchEJVaQEIXBQRmLPTSb-R-4PXnFMQw_kEJ89Q2mXZgvgy6JlEzFfJTqXr20MksCo-PmkAOs5pu6HMX9MUPYeZWwtcpBMaYjKnxC42Lnh09-jkJ");

		User user2 = new User();
		user2.setDeviceID("cYHoRWH5n6k:APA91bH6kmHqoEmY-GAowLYGkwu_eC7RXfgxZwjg0RXR6Z9HZ7ADvLZBpd3R4VSl0BeXABJPUnmSlbsAjGWZ6s_R5c1MJjX8u_930e92ruyRFDC-tRzpjdb0Lf4WnmB0sN61TAZL7iA2");

		List<User> users = new ArrayList<User>();
		users.add(user);
		users.add(user2);

		//new NotificationService().sendNotification("Server Notification 3", "Hello World", users);
	}
}
