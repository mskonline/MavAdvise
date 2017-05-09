package org.mavadvise.activities.tabs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.adaptors.AppointmentsDataAdaptor;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class AppointmentHistory extends AppCompatActivity {

    private JSONArray appointments;
    private AppointmentsDataAdaptor appointmentsDataAdaptor;

    private AppConfig appConfig;
    private ProgressDialogHelper mDialog;

    public AppointmentHistory() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        ListView list = (ListView) findViewById(R.id.historylist);
        appointmentsDataAdaptor = new AppointmentsDataAdaptor(appointments, this);
        list.setAdapter(appointmentsDataAdaptor);

        appConfig = ((MavAdvise) getApplication()).getAppConfig();
        getAllAppointments();
    }

    private void getAllAppointments() {
        mDialog = ProgressDialogHelper.newInstance();
        mDialog.setMsg("Loading appointments...");
        mDialog.show(getSupportFragmentManager(), "Loading");

        RequestBody formBody = new FormBody.Builder()
                .add("netID", appConfig.getUser().getNetID())
                .build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("getAppointments", formBody,
                        new URLResourceHelper.onFinishListener() {
                            @Override
                            public void onFinishSuccess(JSONObject obj) {
                                try {
                                    appointments = obj.getJSONArray("result");
                                } catch (Exception e) {
                                    Log.e("JSON Parse", e.getMessage());
                                }

                                appointmentsDataAdaptor.setAppointments(appointments);
                                appointmentsDataAdaptor.notifyDataSetChanged();
                                mDialog.dismiss();
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