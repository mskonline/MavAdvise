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
import org.mavadvise.adaptors.SessionsDataAdaptor;

/**
 * Created by SaiKumar on 4/7/2017.
 */

public class SessionsViewTab extends Fragment {

    private JSONArray sessions;
    private SessionsDataAdaptor sessionsDataAdaptor;

    public SessionsViewTab(){}

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

        return rootView;
    }
}
