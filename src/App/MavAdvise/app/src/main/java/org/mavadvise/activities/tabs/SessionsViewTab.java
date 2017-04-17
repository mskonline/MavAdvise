package org.mavadvise.activities.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mavadvise.R;

/**
 * Created by SaiKumar on 4/7/2017.
 */

public class SessionsViewTab extends Fragment {

    private JSONArray sessions;
    private OptionsAdapter optionsAdapter;

    public SessionsViewTab(){}

    public static SessionsViewTab newInstance() {
        return new SessionsViewTab();
    }

    public void refreshContent(JSONArray sessions){
        this.sessions = sessions;
        optionsAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sessions_view, container, false);

        ListView list = (ListView) rootView.findViewById(R.id.sessionslist);

        optionsAdapter = new OptionsAdapter();
        list.setAdapter(optionsAdapter);

        return rootView;
    }

    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if(row == null){
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_session_item, parent, false);
            }

            TextView sHeader, sTime, sAppCounter, sStatus;

            sHeader = (TextView) row.findViewById(R.id.session_header);
            sTime = (TextView) row.findViewById(R.id.session_time);
            sAppCounter = (TextView) row.findViewById(R.id.session_appointments_ctr);
            sStatus = (TextView) row.findViewById(R.id.session_statusTV);

            try {
                JSONObject obj = sessions.getJSONObject(position);
                sHeader.setText(obj.getString("date"));
                sTime.setText(obj.getString("startTime") + " - " + obj.getString("endTime"));
                sAppCounter.setText(obj.getString("slotCounter"));

                String status = obj.getString("status");

                if(status.equalsIgnoreCase("cancelled")){
                    int color = ResourcesCompat.getColor(getResources(), R.color.colorCancelled, null);
                    sStatus.setTextColor(color);
                }

                sStatus.setText(status);
            } catch (Exception e){
                Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
            }

            return row;
        }

        public OptionsAdapter(){}

        public int getCount() {
            return sessions != null ? sessions.length() : 0;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }
    }
}
