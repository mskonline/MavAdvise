package org.mavadvise.activities.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.activities.ViewSession;
import org.mavadvise.adaptors.SessionsDataAdaptor;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;

/**
 * Created by SaiKumar on 4/7/2017.
 */

public class SessionsViewTab extends Fragment {

    private JSONArray sessions;
    private SessionsDataAdaptor sessionsDataAdaptor;

    public SessionsViewTab(){}
    private AppConfig appConfig;

    public static SessionsViewTab newInstance() {
        return new SessionsViewTab();
    }

    public void refreshContent(JSONArray sessions){
        this.sessions = sessions;
        sessionsDataAdaptor.setSessions(sessions);
        sessionsDataAdaptor.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sessions_view, container, false);

        ListView list = (ListView) rootView.findViewById(R.id.sessionslist);

        sessionsDataAdaptor = new SessionsDataAdaptor(sessions, this);
        list.setAdapter(sessionsDataAdaptor);
        appConfig = ((MavAdvise) getActivity().getApplication()).getAppConfig();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    JSONObject obj = sessions.getJSONObject(position);
                    Intent i = new Intent(getActivity(), ViewSession.class);
                    i.putExtra("sessionID", obj.getInt("sessionID"));
                    i.putExtra("date", obj.getString("date"));
                    i.putExtra("starttime", obj.getString("starttime"));
                    i.putExtra("endtime", obj.getString("endtime"));
                    i.putExtra("slotCounter", obj.getInt("slotCounter"));
                    i.putExtra("noOfSlots", obj.getInt("noOfSlots"));
                    i.putExtra("location", obj.getString("location"));
                    i.putExtra("comment", obj.getString("comment"));
                    startActivity(i);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }
}
