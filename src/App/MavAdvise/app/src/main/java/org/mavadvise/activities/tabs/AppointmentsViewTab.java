package org.mavadvise.activities.tabs;

/**
 * Created by Remesh on 4/12/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mavadvise.R;

public class AppointmentsViewTab extends Fragment{

    private JSONArray appointments;
    private AppointmentsViewTab.OptionsAdapter optionsAdapter;


    public AppointmentsViewTab(){}

    public static AppointmentsViewTab newInstance() {
        return new AppointmentsViewTab();
    }

    public void refreshContent(JSONArray appointments){
        this.appointments = appointments;
        optionsAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments_view, container, false);
Log.i("view", "in view");


        ListView list = (ListView) rootView.findViewById(R.id.appointmentlist);

        optionsAdapter = new OptionsAdapter();
        list.setAdapter(optionsAdapter);
if(rootView == null){
    Log.i("roo","Root view is null");
}else{
    Log.i("roo1", "Root view is not null");
}
        return rootView;
    }

    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            Log.i("H","Inside View");

            if(row == null){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_appointments_item, parent, false);
            }

            TextView aHeader, aDate, aTime;

            aHeader = (TextView) row.findViewById(R.id.appmnt_header);
            aTime = (TextView) row.findViewById(R.id.appmnt_time);
            aDate = (TextView) row.findViewById(R.id.appmnt_date);

            JSONObject obj = null;

            try {
                obj = appointments.getJSONObject(position);
                aHeader.setText(obj.getString("status"));
                Log.i("jso", obj.getString("status"));
                aTime.setText(obj.getString("session_id") + " - " + obj.getString("net_id"));
                aDate.setText(obj.getString("date"));
            } catch (Exception e){
                Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
            }

            return row;
        }

        public OptionsAdapter(){}

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
