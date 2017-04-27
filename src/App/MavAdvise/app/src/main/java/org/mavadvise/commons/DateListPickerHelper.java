package org.mavadvise.commons;

/**
 * Created by Remesh on 4/26/2017.
 */

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.mavadvise.R;
import org.mavadvise.activities.ManageAppointments;
import org.mavadvise.activities.tabs.AppointmentsAddTab;
import org.mavadvise.app.AppConfig;

public class DateListPickerHelper extends DialogFragment{

    OptionsAdapter optionsAdapter;
    String advisor;

    private JSONArray sessionslist;
    private DateListPickerListener dateListPickerListener;
    private ListView list;

    public void setSessionDates(JSONArray sessions) {
        this.sessionslist = sessions;
        //optionsAdapter.notifyDataSetChanged();
        Log.i("constr",sessionslist.toString());
    }

    public interface DateListPickerListener {
        public void onDateListPickerFinish(JSONObject session);
    }

    public DateListPickerHelper(){}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_datelist_picker, container, false);
        getDialog().setTitle("Select Date");
        Log.i("me3", "Clicked3");

            if(sessionslist != null) {
                Log.i("no1", "not null 1");
            }else
                Log.i("no2","it is null");

            //setAdvisorlist();

            list = (ListView) rootView.findViewById(R.id.datelist);

            optionsAdapter = new OptionsAdapter();
            list.setAdapter(optionsAdapter);

            setUpDateListListener();

        return rootView;

    }

    private void setUpDateListListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject obj = null;
                try{

                    obj = sessionslist.getJSONObject(position);
                    Log.i("pos",Integer.toString(position));
                    Log.i("Here", obj.getString("date"));

                    // advisor = obj.getString("firstName") + " " + obj.getString("lastName");
                    onDateListPickerFinish(obj);

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    public void setOnClickListener(DateListPickerListener dateList){
        this.dateListPickerListener = dateList;
        Log.i("me4","Clicked4");
    }

    private void onDateListPickerFinish(JSONObject session){
        Log.i("me5","Clicked5");
        dateListPickerListener.onDateListPickerFinish(session);
        getDialog().dismiss();
    }


    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            Log.i("me8","Clicked8");
            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_date_select, parent, false);
            }

            TextView adv = (TextView) row.findViewById(R.id.datelistTV);
            TextView datetime = (TextView) row.findViewById(R.id.datetimeTV);


            //Log.i("nlst",);

            JSONObject obj = null;

            Log.i("me9","Clicked9");

            try {
                obj = sessionslist.getJSONObject(position);
                Log.i("lst",obj.toString());
                Log.i("pos",Integer.toString(position));
                Log.i("Here", obj.getString("date"));
                adv.setText(obj.getString("date"));
                datetime.setText(obj.getString("starttime")+ "-" + obj.getString("endtime"));
                advisor = obj.getString("firstName");
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
            }
            return row;
        }

        public OptionsAdapter() {
        }

        public int getCount() {
            Log.i("posi",""+sessionslist.length());
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
