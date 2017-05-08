package org.mavadvise.activities;

import android.content.DialogInterface;
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
import org.mavadvise.commons.AlertDialogHelper;
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
    private ListView sessionsList;

    private AlertDialogHelper alertDialog;

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
                String status = null;

                try {
                    JSONObject sobj = sessions.getJSONObject(position);
                    status = sobj.getString("status");
                } catch (Exception e) {

                }

                if (status.equalsIgnoreCase("STARTED")) {
                    try {
                        JSONObject obj = sessions.getJSONObject(position);
                        Intent intent = new Intent(StartSession.this, ActiveSession.class);
                        intent.putExtra("sessionID", obj.getInt("sessionID"));
                        intent.putExtra("status", status);

                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    String msg = "Do you want to start this session?";
                    final int pos = position;

                    alertDialog = AlertDialogHelper.newInstance(msg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    try {
                                        JSONObject obj = sessions.getJSONObject(pos);
                                        Intent intent = new Intent(StartSession.this, ActiveSession.class);
                                        intent.putExtra("sessionID", obj.getInt("sessionID"));
                                        intent.putExtra("status", obj.getString("status"));

                                        startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Do nothing
                                }
                            });
                    alertDialog.show(getSupportFragmentManager(), "Alert");
                }

            }
        });

        mDialog = ProgressDialogHelper.newInstance();
        mDialog.setMsg("Loading sessions...");

        getSessionsData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mDialog.isAdded())
            mDialog.show(getSupportFragmentManager(), "Loading");

        getSessionsData();
    }

    private void getSessionsData() {
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

                                if (sessions.length() == 0) {
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
