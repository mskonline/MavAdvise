package org.mavadvise.commons;

import android.content.Intent;
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
import org.mavadvise.R;
import org.mavadvise.activities.ManageAppointments;

import java.util.Calendar;

/**
 * Created by Remesh on 4/21/2017.
 */

public class AdvisorPickerHelper extends DialogFragment {

    OptionsAdapter optionsAdapter;
    String advisor;

    private JSONArray advisors;
    private AdvisorPickerListener advPickerListener;
    private ListView list;

    public void setAdvisors(JSONArray advisors) {
        this.advisors = advisors;
    }

    public interface AdvisorPickerListener {
        public void onAdvisorPickerFinish(String adv);
    }


    public AdvisorPickerHelper(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_advisorpicker, container, false);
        getDialog().setTitle("Select Advisor");
        Log.i("me3","Clicked3");

        list = (ListView) rootView.findViewById(R.id.advisorlist);

        optionsAdapter = new OptionsAdapter();
        list.setAdapter(optionsAdapter);

        setUpAdvisorListener();

        Button saveButton = (Button) rootView.findViewById(R.id.advSelectBT);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                 onAdvisorPickerFinish(advisor);
            }
        });

        Button cancelButton = (Button) rootView.findViewById(R.id.advCancelBT);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        return rootView;
    }

    private void setUpAdvisorListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject obj = null;
                try{

                    obj = advisors.getJSONObject(position);
                    advisor = obj.getString("firstname") + " " + obj.getString("lastname");
                    
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    public void setOnSelectListener(AdvisorPickerListener advisorPickerListener){
        this.advPickerListener = advisorPickerListener;
        Log.i("me4","Clicked4");
    }

    private void onAdvisorPickerFinish(String adv){
        Log.i("me5","Clicked5");
        advPickerListener.onAdvisorPickerFinish(adv);
        getDialog().dismiss();
    }

    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            Log.i("me6","Clicked6");
            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_advisor_select, parent, false);
            }

            TextView adv = (TextView) row.findViewById(R.id.advisorTV);

            JSONObject obj = null;

            try {
                obj = advisors.getJSONObject(position);
                adv.setText(obj.getString("firstname") + " " + obj.getString("lastname"));
                advisor = obj.getString("firstname");
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
            }
            return row;
        }

        public OptionsAdapter() {
        }

        public int getCount() {
            return advisors != null ? advisors.length() : 0;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

    }
}
