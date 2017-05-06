package org.mavadvise.adaptors;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mavadvise.R;

/**
 * Created by SaiKumar on 5/3/2017.
 */

public class SessionAppointmentsAdaptor extends BaseAdapter {

    Activity activity;
    JSONArray appointments;

    public SessionAppointmentsAdaptor(Activity activity, JSONArray appointments){
        this.activity = activity;
        this.appointments = appointments;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if(row == null){
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(R.layout.list_session_appointments_item, parent, false);
        }

        try {
            JSONObject obj = appointments.getJSONObject(position);

            TextView apptName, apptSlotNumber;

            apptName = (TextView) row.findViewById(R.id.session_apptnameTV);
            apptSlotNumber = (TextView) row.findViewById(R.id.session_appntslotTV);

            apptName.setText(obj.getString("firstname") + " " + obj.getString("lastname"));
            apptSlotNumber.setText(obj.getString("slot_number"));
        } catch (Exception e){

        }

        return row;
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

    public void setAppointments(JSONArray appointments) {
        this.appointments = appointments;
    }
}
