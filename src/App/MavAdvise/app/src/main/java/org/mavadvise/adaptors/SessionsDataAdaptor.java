package org.mavadvise.adaptors;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mavadvise.R;

import java.text.SimpleDateFormat;

/**
 * Created by SaiKumar on 4/18/2017.
 */

public class SessionsDataAdaptor extends BaseAdapter {

    private JSONArray sessions;
    private boolean formattingRequired = false;

    private SimpleDateFormat fromDateFormat, toDateFormat;
    private SimpleDateFormat fromTimeFormat, toTimeFormat;

    private LayoutInflater layoutInflater;
    private Context context;
    private int cColor, dColor;

    public SessionsDataAdaptor(JSONArray sessions, Fragment fragment){
        this.sessions = sessions;
        initResources(fragment);
    }

    public SessionsDataAdaptor(JSONArray sessions, Activity activity){
        this.sessions = sessions;
        initResources(activity);
    }

    public SessionsDataAdaptor(JSONArray sessions, Activity activity, boolean formattingRequired){
        this.sessions = sessions;
        this.formattingRequired = formattingRequired;

        fromDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        toDateFormat = new SimpleDateFormat("EEE, MMM d yyyy");

        fromTimeFormat = new SimpleDateFormat("HH:mm:ss");
        toTimeFormat = new SimpleDateFormat("h:mm a");

        initResources(activity);
    }

    @Override
    public int getCount() {
        return sessions != null ? sessions.length() : 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if(row == null)
            row = layoutInflater.inflate(R.layout.list_session_item, parent, false);

        TextView sHeader, sTime, sAppCounter, sStatus, sLocation;

        sHeader = (TextView) row.findViewById(R.id.session_header);
        sTime = (TextView) row.findViewById(R.id.session_time);
        sAppCounter = (TextView) row.findViewById(R.id.session_appointments_ctr);
        sStatus = (TextView) row.findViewById(R.id.session_statusTV);
        sLocation = (TextView) row.findViewById(R.id.session_location);

        try {
            JSONObject obj = sessions.getJSONObject(position);

            if(formattingRequired){
                String date = obj.getString("date");
                sHeader.setText(toDateFormat.format(fromDateFormat.parse(date)));

                String startTime = obj.getString("startTime");
                String endTime = obj.getString("endTime");

                startTime = toTimeFormat.format(fromTimeFormat.parse(startTime));
                endTime = toTimeFormat.format(fromTimeFormat.parse(endTime));

                sTime.setText(startTime + " - " + endTime);
            } else {
                sHeader.setText(obj.getString("date"));
                sTime.setText(obj.getString("starttime") + " - " + obj.getString("endtime"));
            }

            sLocation.setText(obj.getString("location"));
            sAppCounter.setText(obj.getInt("slotCounter") + "/" + obj.getInt("noOfSlots"));

            String status = obj.getString("status");

            // Cancelled
            if(status.startsWith("C"))
                sStatus.setTextColor(cColor);

            // Done
            if(status.startsWith("D"))
                sStatus.setTextColor(dColor);

            sStatus.setText(status);
        } catch (Exception e){
            Toast.makeText(context, "Error in retrieving the list", Toast.LENGTH_SHORT);
        }

        return row;
    }

    private void initResources(Fragment fragment){
        layoutInflater = fragment.getActivity().getLayoutInflater();
        cColor = ResourcesCompat.getColor(fragment.getResources(), R.color.colorCancelled, null);
        dColor = ResourcesCompat.getColor(fragment.getResources(), R.color.colorDone, null);
        context = fragment.getContext();
    }

    private void initResources(Activity activity){
        layoutInflater = activity.getLayoutInflater();
        cColor = ResourcesCompat.getColor(activity.getResources(), R.color.colorCancelled, null);
        dColor = ResourcesCompat.getColor(activity.getResources(), R.color.colorDone, null);
        context = activity.getApplicationContext();
    }
    public void setSessions(JSONArray sessions) {
        this.sessions = sessions;
    }
}
