package org.mavadvise.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.adaptors.SessionAppointmentsDataAdaptor;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;

import okhttp3.FormBody;
import okhttp3.RequestBody;


public class ViewSession extends AppCompatActivity {

    private int cColor, dColor;
    private TextView sStatus;
    private Button cancelSessionBtn;

    private int sessionID;
    private ProgressDialogHelper apptsDialog;

    private JSONArray appointments;
    private SessionAppointmentsDataAdaptor appointmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session);
        cColor = ResourcesCompat.getColor(getResources(), R.color.colorCancelled, null);
        dColor = ResourcesCompat.getColor(getResources(), R.color.colorDone, null);

        final Bundle extras = getIntent().getExtras();

        TextView sHeader, sTime, sAppCounter, sLocation;

        sHeader = (TextView) findViewById(R.id.vsession_header);
        sTime = (TextView) findViewById(R.id.vsession_time);
        sAppCounter = (TextView) findViewById(R.id.vsession_appointments_ctr);
        sStatus = (TextView) findViewById(R.id.vsession_statusTV);
        sLocation = (TextView) findViewById(R.id.vsession_location);
        cancelSessionBtn = (Button) findViewById(R.id.sessionCancelBT);

        sessionID = extras.getInt("sessionID");
        String date = extras.getString("date");
        sHeader.setText(date);

        String startTime = extras.getString("starttime");
        String endTime = extras.getString("endtime");

        sTime.setText(startTime + " - " + endTime);
        sLocation.setText(extras.getString("location"));

        sAppCounter.setText(extras.getInt("slotCounter") + "/" + extras.getInt("noOfSlots"));

        String status = extras.getString("status");
        // Done
        if (status.startsWith("D"))
            sStatus.setTextColor(dColor);

        // Cancelled
        if (status.startsWith("C")) {
            sStatus.setTextColor(cColor);
            cancelSessionBtn.setVisibility(View.GONE);
        } else if (status.startsWith("D")) {
            sStatus.setTextColor(dColor);
            cancelSessionBtn.setVisibility(View.GONE);
        } else {
            cancelSessionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ViewSession.this, CancelSession.class);
                    i.putExtra("sessionID", extras.getInt("sessionID"));
                    startActivityForResult(i, 1);
                }
            });
        }

        sStatus.setText(status);

        ListView apptsView = (ListView) findViewById(R.id.sessionApptsLV);
        appointmentsAdapter = new SessionAppointmentsDataAdaptor(this, appointments);
        apptsView.setAdapter(appointmentsAdapter);

        apptsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject obj = appointments.getJSONObject(position);
                    Intent i = new Intent(ViewSession.this, ViewAppointment.class);
                    i.putExtra("firstname", obj.getString("firstname"));
                    i.putExtra("lastname", obj.getString("lastname"));
                    i.putExtra("slot_number", obj.getString("slot_number"));
                    i.putExtra("status", obj.getString("status"));
                    i.putExtra("reason", obj.getString("reason"));
                    i.putExtra("sessionID", sessionID);
                    i.putExtra("appointmentID", obj.getInt("appointment_id"));
                    i.putExtra("netID", obj.getString("net_id"));

                    startActivityForResult(i, 2);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        apptsDialog = ProgressDialogHelper.newInstance();
        apptsDialog.setMsg("Fetching appointments...");

        getAppointments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String status = data.getStringExtra("status");

                if (status.equalsIgnoreCase("C")) {
                    sStatus.setText("CANCELLED");
                    sStatus.setTextColor(cColor);
                    cancelSessionBtn.setVisibility(View.GONE);

                    getAppointments();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }

        if(requestCode == 2){
            if (resultCode == Activity.RESULT_OK) {
                String status = data.getStringExtra("status");

                if (status.equalsIgnoreCase("C")) {
                    getAppointments();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }

    private void getAppointments() {
        if (!apptsDialog.isAdded())
            apptsDialog.show(getSupportFragmentManager(), "appointments");

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
                                    appointmentsAdapter.setAppointments(appointments);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                appointmentsAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                apptsDialog.dismiss();
                                Toast.makeText(getApplicationContext(),
                                        msg, Toast.LENGTH_LONG).show();
                            }
                        });

        urlResourceHelper.execute();
    }
}
