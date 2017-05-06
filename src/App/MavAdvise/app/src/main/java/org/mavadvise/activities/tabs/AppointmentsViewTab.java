package org.mavadvise.activities.tabs;

/**
 * Created by Remesh on 4/12/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mavadvise.R;

public class AppointmentsViewTab extends Fragment {

    private JSONArray appointments;
    private OptionsAdapter optionsAdapter;


    public AppointmentsViewTab() {
    }

    public static AppointmentsViewTab newInstance() {
        return new AppointmentsViewTab();
    }

    public void refreshContent(JSONArray appointments) {
        this.appointments = appointments;
        Log.i("refresh", "Refreshed");
        optionsAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments_view, container, false);
        Log.i("view", "in view");
//        TextView textView = (TextView) rootView.findViewById(R.id.appointmenttext);
//        textView.setText("Text");
//
        ListView list = (ListView) rootView.findViewById(R.id.appointmentlist);

        optionsAdapter = new OptionsAdapter();
        list.setAdapter(optionsAdapter);

        Button hist = (Button) rootView.findViewById(R.id.appHistoryBT);

        hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //AppointmentHistory history = new AppointmentHistory(appointments);
                Intent i = null;
                i = new Intent(view.getContext(), AppointmentHistory.class);
                i.putExtra("jsonArray", appointments.toString());
                startActivity(i);
            }
            });

        return rootView;
    }

    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            Log.i("H", "Inside View");

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_appointments_item, parent, false);
            }

            TextView aHeader, aDate, aTime, aStat;

            aHeader = (TextView) row.findViewById(R.id.appmnt_header);
            aTime = (TextView) row.findViewById(R.id.appmnt_time);
            aDate = (TextView) row.findViewById(R.id.appmnt_date);
            aStat =  (TextView) row.findViewById(R.id.appmnt_status);

            JSONObject obj = null;


//            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
//            SimpleDateFormat toDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            SimpleDateFormat fromDateFormat = new SimpleDateFormat("EEE, MMM d yyyy");


            try {
                obj = appointments.getJSONObject(position);
                String status = obj.getString("appStatus");

//                Calendar date = Calendar.getInstance();
//                String dateStr = obj.getString("date");
//                Date date1 =  (Date)formatter.parse(toDateFormat.format(fromDateFormat.parse(dateStr)));
                row.setVisibility(View.VISIBLE);
                if(status.startsWith("S")||status.startsWith("C")) {
                    aHeader.setText(obj.getString("firstname") + " " + obj.getString("lastname"));
                    Log.i("jso", obj.getString("firstname"));
                    aTime.setText(obj.getString("starttime") + " - " + obj.getString("endtime"));
                    aDate.setText(obj.getString("date"));
                    aStat.setText(obj.getString("appStatus"));
                }else{
                    row.setVisibility(View.GONE);
                }

            } catch (Exception e) {
                Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
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
