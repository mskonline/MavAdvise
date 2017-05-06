package org.mavadvise.commons;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.mavadvise.app.AppConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by SaiKumar on 4/24/2017.
 */

public class URLResourceHelper extends AsyncTask<Void, Void , String> {

    public interface onFinishListener{
        public void onFinishSuccess(JSONObject response);
        public void onFinishFailed(String msg);
    }

    private onFinishListener listener;
    private String action;
    private RequestBody requestBody;
    private static OkHttpClient client;

    public URLResourceHelper(String action,
                             RequestBody requestBody,
                             onFinishListener listener){
        if(client == null){
            Log.i("OkHttpClient", "Initialized...");
            client = new OkHttpClient.Builder()
                    .cookieJar(new CookieJar() {
                        private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            cookieStore.put(url.host(), cookies);
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            List<Cookie> cookies = cookieStore.get(url.host());
                            return cookies != null ? cookies : new ArrayList<Cookie>();
                        }
                    }).build();
        }

        this.action = action;
        this.requestBody = requestBody;
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(AppConfig.hostName)
                .port(AppConfig.port)
                .addPathSegment("MavAdvise")
                .addPathSegment(action)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            Thread.sleep(500);
        } catch (Exception e){
            Log.e("Thread", "Thread exception");
        }

        if(result != null) {
            try {
                JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
                String status = obj.getString("type");

                if(status.equalsIgnoreCase("success")){
                    listener.onFinishSuccess(obj);
                } else{
                    listener.onFinishFailed(obj.getString("message"));
                }

            } catch (Exception e) {
                Log.e("JSON Parse", e.getMessage());
                e.printStackTrace();
                listener.onFinishFailed("Bad response from Server. Try again later");
            }
        } else
            listener.onFinishFailed("No response from Server. Try again later");
    }
}
