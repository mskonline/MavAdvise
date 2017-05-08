package org.mavadvise.activities.tabs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;

public class AppointmentHistory extends AppCompatActivity {

    private JSONArray appointments;
    private OptionsAdapter optionsAdapter;

    // public AppointmentHistory(JSONArray appointments){
    //     this.appointments = appointments;
    // }

    public AppointmentHistory() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_history);

        Intent intent = getIntent();
        String jsonArray = intent.getStringExtra("jsonArray");

        try {
            appointments = new JSONArray(jsonArray);
            System.out.println(appointments.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView list = (ListView) findViewById(R.id.historylist);

        optionsAdapter = new OptionsAdapter();
        list.setAdapter(optionsAdapter);
    }

    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            Log.i("H", "Inside View");

            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.list_appointments_item, parent, false);
            }

            TextView aHeader, aDate, aTime, aStat;

            aHeader = (TextView) row.findViewById(R.id.appmnt_header);
            aTime = (TextView) row.findViewById(R.id.appmnt_time);
            aDate = (TextView) row.findViewById(R.id.appmnt_date);
            aStat = (TextView) row.findViewById(R.id.appmnt_status);

            JSONObject obj = null;

            try {
                obj = appointments.getJSONObject(position);

                aHeader.setText(obj.getString("firstname") + " " + obj.getString("lastname"));
                Log.i("jso", obj.getString("firstname"));
                aTime.setText(obj.getString("starttime") + " - " + obj.getString("endtime"));
                aDate.setText(obj.getString("date"));
                aStat.setText(obj.getString("appStatus"));
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
            }

            return row;
        }

        public OptionsAdapter() {
        }

        public int getCount() {
            return appointments != null ? appointments.length() : 0;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }
    }
}


