package org.mavadvise.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.adaptors.SessionsDataAdaptor;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class StartSession extends AppCompatActivity {

    private JSONArray sessions;
    private SessionsDataAdaptor sessionsDataAdaptor;
    private ProgressDialogHelper mDialog;

    private AppConfig appConfig;
    ListView sessionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_session);

        sessionsList = (ListView) findViewById(R.id.start_sessionsLV);
        appConfig = ((MavAdvise) getApplication()).getAppConfig();
        sessionsDataAdaptor = new SessionsDataAdaptor(sessions, this);

        sessionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        JSONObject obj = sessions.getJSONObject(position);
                        Intent i = new Intent(StartSession.this, ActiveSession.class);
                        i.putExtra("sessionID", obj.getInt("sessionID"));
                        startActivity(i);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

        getSessionsData();
    }

    private void getSessionsData(){
        mDialog =  ProgressDialogHelper.newInstance();
        mDialog.setMsg("Loading sessions...");
        mDialog.show(getSupportFragmentManager(), "Loading");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        RequestBody formBody = new FormBody.Builder()
                .add("netID", appConfig.getUser().getNetID())
                .add("date", sdf.format(new Date()))
                .build();

        URLResourceHelper urlResourceHelper =
            new URLResourceHelper("getScheduledSessions", formBody,
                new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        try {
                            sessions = obj.getJSONArray("result");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        sessionsDataAdaptor.setSessions(sessions);
                        sessionsList.setAdapter(sessionsDataAdaptor);
                        mDialog.dismiss();

                        if(sessions.length() == 0){
                            Toast.makeText(getApplicationContext(),
                                    "No Sessions to start", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        mDialog.dismiss();
                        Toast.makeText(getApplicationContext(),
                                msg, Toast.LENGTH_LONG).show();
                    }
                });

        urlResourceHelper.execute();
    }
}
