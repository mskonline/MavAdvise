package org.mavadvise.adaptors;

import android.app.Activity;
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

/**
 * Created by SaiKumar on 5/8/2017.
 */

public class AppointmentsDataAdaptor extends BaseAdapter {

    private JSONArray appointments;
    private Context context;
    private LayoutInflater layoutInflater;

    private int dColor, cColor, sColor;

    public AppointmentsDataAdaptor(JSONArray appointments, Fragment fragment) {
        this.appointments = appointments;
        initResources(fragment);
    }

    public AppointmentsDataAdaptor(JSONArray appointments, Activity activity) {
        this.appointments = appointments;
        initResources(activity);
    }

    public int getCount() {
        return appointments != null ? appointments.length() : 0;
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

        if (row == null) {
            row = layoutInflater.inflate(R.layout.list_appointments_item, parent, false);
        }

        TextView aHeader, aDate, aTime, aStat, aReason, aSlot;

        aHeader = (TextView) row.findViewById(R.id.appmnt_header);
        aTime = (TextView) row.findViewById(R.id.appmnt_time);
        aDate = (TextView) row.findViewById(R.id.appmnt_date);
        aStat = (TextView) row.findViewById(R.id.appmnt_status);
        aReason = (TextView) row.findViewById(R.id.apptreasonTV);
        aSlot = (TextView) row.findViewById(R.id.appmnt_slot);

        JSONObject obj = null;

        try {
            obj = appointments.getJSONObject(position);

            aHeader.setText(obj.getString("firstname") + " " + obj.getString("lastname"));
            aTime.setText(obj.getString("starttime") + " - " + obj.getString("endtime"));
            aDate.setText(obj.getString("date"));
            aSlot.setText("Slot : " + obj.getString("slot"));

            String status = obj.getString("appStatus");

            if (status.startsWith("S"))
                aStat.setTextColor(sColor);

            // Cancelled
            if (status.startsWith("C"))
                aStat.setTextColor(cColor);

            // Done
            if (status.startsWith("D") || status.startsWith("O"))
                aStat.setTextColor(dColor);

            aStat.setText(status);
            aReason.setText(obj.getString("reason"));
        } catch (Exception e) {
            Toast.makeText(context, "Error in retrieving the list", Toast.LENGTH_SHORT);
        }

        return row;
    }

    private void initResources(Fragment fragment) {
        layoutInflater = fragment.getActivity().getLayoutInflater();
        cColor = ResourcesCompat.getColor(fragment.getResources(), R.color.colorCancelled, null);
        dColor = ResourcesCompat.getColor(fragment.getResources(), R.color.colorDone, null);
        sColor = ResourcesCompat.getColor(fragment.getResources(), R.color.colorAccent, null);
        context = fragment.getContext();
    }

    private void initResources(Activity activity) {
        layoutInflater = activity.getLayoutInflater();
        cColor = ResourcesCompat.getColor(activity.getResources(), R.color.colorCancelled, null);
        dColor = ResourcesCompat.getColor(activity.getResources(), R.color.colorDone, null);
        sColor = ResourcesCompat.getColor(activity.getResources(), R.color.colorAccent, null);
        context = activity.getApplicationContext();
    }

    public void setAppointments(JSONArray appointments) {
        this.appointments = appointments;
    }
}
