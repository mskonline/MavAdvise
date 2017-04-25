package org.mavadvise.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mavadvise.R;

import java.text.SimpleDateFormat;

public class ViewSession extends AppCompatActivity {

    private SimpleDateFormat fromDateFormat, toDateFormat;
    private SimpleDateFormat fromTimeFormat, toTimeFormat;

    private int cColor, dColor;
    private TextView sStatus;
    private Button cancelSessionBtn;

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

        String date = extras.getString("date");
        sHeader.setText(date);

        String startTime = extras.getString("starttime");
        String endTime = extras.getString("endtime");

        sTime.setText(startTime + " - " + endTime);
        sLocation.setText(extras.getString("location"));

        sAppCounter.setText(extras.getInt("slotCounter") + "/" + extras.getInt("noOfSlots"));

        String status = extras.getString("status");
        // Done
        if(status.startsWith("D"))
            sStatus.setTextColor(dColor);

        // Cancelled
        if(status.startsWith("C")){
            sStatus.setTextColor(cColor);
            cancelSessionBtn.setVisibility(View.GONE);
        } else {
            cancelSessionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(ViewSession.this, CancelSession.class);
                    i.putExtra("sessionID", extras.getInt("sessionID"));
                    startActivityForResult(i,1);
                }
            });
        }

        sStatus.setText(status);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                String status = data.getStringExtra("status");

                if(status.equalsIgnoreCase("C")){
                    sStatus.setText("CANCELLED");
                    sStatus.setTextColor(cColor);
                    cancelSessionBtn.setVisibility(View.GONE);
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
            }
        }
    }
}
