package org.mavadvise.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.adaptors.SessionAppointmentsAdaptor;
import org.mavadvise.commons.AlertDialogHelper;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class ActiveSession extends AppCompatActivity {

    private SessionAppointmentsAdaptor sessionAppointmentsAdaptor;
    private JSONArray appointments;

    private ProgressDialogHelper apptsDialog;
    private ListView listView;

    private int sessionID;
    private String status;
    private int onGoingAppt = 0;
    private int totalAppts = 0;

    private int updateAppt = 0;
    private int dColor;

    private Button nextBtn, noShowBtn;
    private AlertDialogHelper alertDialog, noShowAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_session);

        sessionAppointmentsAdaptor = new SessionAppointmentsAdaptor(this, appointments);

        listView = (ListView) findViewById(R.id.actSessionApptsLV);
        listView.setAdapter(sessionAppointmentsAdaptor);

        final Bundle extras = getIntent().getExtras();
        sessionID = extras.getInt("sessionID");
        status = extras.getString("status");

        noShowBtn = (Button) findViewById(R.id.sessionNoShowBT);
        nextBtn = (Button) findViewById(R.id.sessionNextBT);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                advanceSession("N");
            }
        });

        noShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!noShowAlert.isAdded())
                    noShowAlert.show(getSupportFragmentManager(), "NoShow");
            }
        });

        noShowAlert= AlertDialogHelper.newInstance("Do you mark this appointment as No Show ?",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    advanceSession("Y");
                }
            },
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                }
            });

        dColor = ResourcesCompat.getColor(getResources(), R.color.colorDone, null);

        startSession(sessionID);
    }

    private void startSession(final int sessionID){
        apptsDialog = ProgressDialogHelper.newInstance();
        apptsDialog.setMsg("Fetching appointments...");
        apptsDialog.show(getSupportFragmentManager(),"appointments");

        RequestBody formBody = new FormBody.Builder()
                .add("sessionID", "" + sessionID)
                .add("status", status)
                .build();

        URLResourceHelper urlResourceHelper =
            new URLResourceHelper("startSession", formBody,
                new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        apptsDialog.dismiss();
                        try {
                            appointments = obj.getJSONArray("result");
                            sessionAppointmentsAdaptor.setAppointments(appointments);

                            totalAppts = appointments.length();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        sessionAppointmentsAdaptor.notifyDataSetChanged();

                        if(!status.equalsIgnoreCase("STARTED")) {
                            Toast.makeText(getApplicationContext(),
                                    "Session started. All appointments notified", Toast.LENGTH_LONG).show();

                            listView.setItemChecked(0, true);
                            onGoingAppt = 0;
                        }else{
                            highlightOnGoingAppointment();
                        }

                        if(onGoingAppt == (totalAppts - 1) || totalAppts == 0){
                            nextBtn.setText("DONE");
                        }

                        if(totalAppts == 0)
                            noShowBtn.setEnabled(false);
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        apptsDialog.dismiss();
                    }
                });

        urlResourceHelper.execute();
    }

    private void highlightOnGoingAppointment(){
        try{
            JSONObject obj = null;

            for(int i = 0; i < appointments.length(); ++i){
                obj = appointments.getJSONObject(i);

                if(obj.getString("status").startsWith("D"))
                    continue;
                else {
                    listView.setItemChecked(i, true);
                    onGoingAppt = i;
                    break;
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void advanceSession(String noShow){
        updateAppt = onGoingAppt;

        if(onGoingAppt == (totalAppts - 1) || totalAppts == 0){
            markSessionAsDone(noShow);
            return;
        }

        listView.setItemChecked(onGoingAppt, false);
        int prevApptID = 0, nextApptID = 0;

        try{
            prevApptID = appointments.getJSONObject(onGoingAppt).getInt("appointment_id");
            ++onGoingAppt;

            int nextAppt = onGoingAppt + 1;

            if(nextAppt == totalAppts)
                nextApptID = 0;
            else
                nextApptID = appointments.getJSONObject(nextAppt).getInt("appointment_id");
        } catch (Exception e){

        }

        listView.setItemChecked(onGoingAppt, true);

        RequestBody formBody = new FormBody.Builder()
                .add("prevAppointmentID", "" + prevApptID)
                .add("nextAppointmentID", "" + nextApptID)
                .add("sessionID", "" + sessionID)
                .add("noShow", noShow)
                .build();

        URLResourceHelper urlResourceHelper =
            new URLResourceHelper("advanceSession", formBody,
                new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        String uStatus = "";

                        try {
                            uStatus = obj.getString("result");
                        } catch (Exception e){}

                        View  v = listView.getChildAt(updateAppt);

                        if(v == null)
                            return;

                        TextView statusTV = (TextView) v.findViewById(R.id.session_appntstatusTV);

                        if(uStatus.startsWith("D"))
                            statusTV.setTextColor(dColor);

                        if(!uStatus.startsWith("S"))
                            statusTV.setText(uStatus);
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        //Do nothing
                    }
                });

        urlResourceHelper.execute();

        if(onGoingAppt == (totalAppts - 1)){
            nextBtn.setText("DONE");
        }
    }

    private void markSessionAsDone(String noShow){
        int apptID = 0;

        try{
            apptID = appointments.getJSONObject(onGoingAppt).getInt("appointment_id");
        } catch (Exception e){
        }

        RequestBody formBody = new FormBody.Builder()
                .add("prevAppointmentID", "" + apptID)
                .add("nextAppointmentID", "-1")
                .add("sessionID", "" + sessionID)
                .add("noShow", noShow)
                .build();

        URLResourceHelper urlResourceHelper =
            new URLResourceHelper("advanceSession", formBody,
                new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        String msg = "Session completed";

                        alertDialog = AlertDialogHelper.newInstance(msg,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        alertDialog.dismiss();
                                        finish();
                                    }
                                });
                        alertDialog.show(getSupportFragmentManager(), "Alert");
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        //Do nothing
                    }
                });

        urlResourceHelper.execute();
    }
}
