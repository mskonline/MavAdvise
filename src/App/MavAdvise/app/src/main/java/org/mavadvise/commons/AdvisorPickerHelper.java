package org.mavadvise.commons;

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

/**
 * Created by Remesh on 4/21/2017.
 */

public class AdvisorPickerHelper extends DialogFragment {

    private OptionsAdapter optionsAdapter;

    private JSONArray advisorslist;
    private AdvisorPickerListener advPickerListener;
    private ListView list;

    public void setAdvisors(JSONArray advisors) {
        this.advisorslist = advisors;
    }

    public interface AdvisorPickerListener {
        public void onAdvisorPickerFinish(JSONObject adv);
    }

    public AdvisorPickerHelper(){
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_advisorpicker, container, false);
        getDialog().setTitle("Select Advisor");

        list = (ListView) rootView.findViewById(R.id.advisorlist);

        optionsAdapter = new OptionsAdapter();
        list.setAdapter(optionsAdapter);

        setUpAdvisorListener();
        return rootView;
    }

    private void setUpAdvisorListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject obj = null;
                try{
                    obj = advisorslist.getJSONObject(position);
                    onAdvisorPickerFinish(obj);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
                }
            }
        });

    }

    public void setOnSelectListener(AdvisorPickerListener advisorPickerListener){
        this.advPickerListener = advisorPickerListener;
    }

    private void onAdvisorPickerFinish(JSONObject adv){
        advPickerListener.onAdvisorPickerFinish(adv);
        getDialog().dismiss();
    }

    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_advisor_select_item, parent, false);
            }

            TextView adv = (TextView) row.findViewById(R.id.advisorTV);

            JSONObject obj = null;

            try {
                obj = advisorslist.getJSONObject(position);
                adv.setText(obj.getString("firstName") + " " + obj.getString("lastName"));
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
            }
            return row;
        }

        public OptionsAdapter() {
        }

        public int getCount() {
            return advisorslist != null ? advisorslist.length() : 0;
        }

        public Object getItem(int arg0) {
            return null;
        }

        public long getItemId(int position) {
            return position;
        }

    }
}
