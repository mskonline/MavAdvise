package org.mavadvise.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.adaptors.SessionAppointmentsAdaptor;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ActiveSession extends AppCompatActivity {

    SessionAppointmentsAdaptor sessionAppointmentsAdaptor;
    JSONArray appointments;

    private ProgressDialogHelper apptsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);

        sessionAppointmentsAdaptor = new SessionAppointmentsAdaptor(this, appointments);

        ListView listView = (ListView) findViewById(R.id.actSessionApptsLV);
        listView.setAdapter(sessionAppointmentsAdaptor);

        final Bundle extras = getIntent().getExtras();
        int sessionID = extras.getInt("sessionID");

        getAppointments(sessionID);
    }

    private void getAppointments(int sessionID){
        apptsDialog = ProgressDialogHelper.newInstance();
        apptsDialog.setMsg("Fetching appointments...");
        apptsDialog.show(getSupportFragmentManager(),"appointments");

        RequestBody formBody = new FormBody.Builder()
                .add("sessionID", "" + sessionID)
                .build();

        URLResourceHelper urlResourceHelper =
            new URLResourceHelper("getSessionAppointments", formBody,
                new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        apptsDialog.dismiss();
                        try {
                            appointments = obj.getJSONArray("result");
                            sessionAppointmentsAdaptor.setAppointments(appointments);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        sessionAppointmentsAdaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        apptsDialog.dismiss();
                    }
                });

        urlResourceHelper.execute();
    }
}
