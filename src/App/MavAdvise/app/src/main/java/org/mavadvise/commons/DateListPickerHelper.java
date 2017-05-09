package org.mavadvise.commons;

/**
 * Created by Remesh on 4/26/2017.
 */

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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

public class DateListPickerHelper extends DialogFragment {

    OptionsAdapter optionsAdapter;

    private JSONArray sessionslist;
    private DateListPickerListener dateListPickerListener;
    private ListView list;

    public void setSessionDates(JSONArray sessions) {
        this.sessionslist = sessions;
    }

    public interface DateListPickerListener {
        public void onDateListPickerFinish(JSONObject session);
    }

    public DateListPickerHelper() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_datelist_picker, container, false);
        getDialog().setTitle("Select Date");

        list = (ListView) rootView.findViewById(R.id.datelist);

        optionsAdapter = new OptionsAdapter();
        list.setAdapter(optionsAdapter);

        setUpDateListListener();
        return rootView;
    }

    private void setUpDateListListener() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject obj = null;
                try {
                    obj = sessionslist.getJSONObject(position);
                    onDateListPickerFinish(obj);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    public void setOnClickListener(DateListPickerListener dateList) {
        this.dateListPickerListener = dateList;
    }

    private void onDateListPickerFinish(JSONObject session) {
        dateListPickerListener.onDateListPickerFinish(session);
        getDialog().dismiss();
    }


    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_date_select_item, parent, false);
            }

            TextView adv = (TextView) row.findViewById(R.id.datelistTV);
            TextView datetime = (TextView) row.findViewById(R.id.datetimeTV);

            JSONObject obj = null;

            try {
                obj = sessionslist.getJSONObject(position);
                adv.setText(obj.getString("date"));
                datetime.setText(obj.getString("starttime") + " - " + obj.getString("endtime"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
            }
            return row;
        }

        public OptionsAdapter() {
        }

        public int getCount() {
            return sessionslist != null ? sessionslist.length() : 0;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

    }
}
