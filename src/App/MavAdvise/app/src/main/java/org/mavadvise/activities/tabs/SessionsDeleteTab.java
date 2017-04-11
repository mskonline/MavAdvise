package org.mavadvise.activities.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.mavadvise.R;

/**
 * Created by SaiKumar on 4/7/2017.
 */

public class SessionsDeleteTab extends Fragment {

    private JSONArray sessions;
    private OptionsAdapter optionsAdapter;

    public SessionsDeleteTab() {
    }

    public static SessionsDeleteTab newInstance() {
        SessionsDeleteTab fragment = new SessionsDeleteTab();
        return fragment;
    }

    public void refreshContent(JSONArray sessions){
        optionsAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sessions_delete, container, false);

        /*ListView list = (ListView) rootView.findViewById(R.id.sessionslist);

        optionsAdapter = new OptionsAdapter();
        list.setAdapter(optionsAdapter);*/
        return rootView;
    }

    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.list_session_item, parent, false);

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
