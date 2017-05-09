package org.mavadvise.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.mavadvise.R;

public class ViewAppointment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_appointment);

        TextView vApptName, vApptSlot, vApptReason, vApptStatus;
        Button vApptCancelBT;

        vApptName = (TextView) findViewById(R.id.vApptName);
        vApptSlot = (TextView) findViewById(R.id.vApptSlot);
        vApptReason = (TextView) findViewById(R.id.vApptReason);
        vApptStatus = (TextView) findViewById(R.id.vApptStatus);
        vApptCancelBT = (Button) findViewById(R.id.vApptCancelBT);

        final Bundle extras = getIntent().getExtras();

        vApptName.setText(extras.getString("firstname") + " " + extras.getString("lastname"));
        vApptSlot.setText(extras.getString("slot_number"));
        vApptReason.setText(extras.getString("reason"));
        vApptStatus.setText(extras.getString("status"));

        if(extras.getString("status").startsWith("C")){
            vApptCancelBT.setVisibility(View.GONE);
        }

        vApptCancelBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ViewAppointment.this, CancelAppointment.class);
                i.putExtra("sessionID", extras.getInt("sessionID"));
                i.putExtra("appointmentID", extras.getInt("appointmentID"));
                i.putExtra("netID", extras.getString("netID"));

                startActivityForResult(i, 1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {

            }
        }
    }
}
