package org.mavadvise.commons;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
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
import org.mavadvise.app.AppConfig;

/**
 * Created by Remesh on 4/21/2017.
 */

public class AdvisorPickerHelper extends DialogFragment {

    OptionsAdapter optionsAdapter;
    String advisor;

    private AppConfig appConfig;
    private DialogFragment mDialog;
    private ProgressDialogHelper saveDialog;


    private JSONArray advisorslist;
    private AdvisorPickerListener advPickerListener;
    private ListView list;

    public void setAdvisors(JSONArray advisors) {
        this.advisorslist = advisors;
        //optionsAdapter.notifyDataSetChanged();
        Log.i("constr",advisorslist.toString());
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
        Log.i("me3","Clicked3");

        if(advisorslist != null) {
            Log.i("no1", "not null 1");
        }else
            Log.i("no2","it is null");

        //setAdvisorlist();

        list = (ListView) rootView.findViewById(R.id.advisorlist);

        optionsAdapter = new OptionsAdapter();
        list.setAdapter(optionsAdapter);

        setUpAdvisorListener();

//        Button saveButton = (Button) rootView.findViewById(R.id.advSelectBT);
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                 onAdvisorPickerFinish(advisor);
//            }
//        });
//
//        Button cancelButton = (Button) rootView.findViewById(R.id.advCancelBT);
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getDialog().dismiss();
//            }
//        });


        return rootView;
    }

    private void setUpAdvisorListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                JSONObject obj = null;
                try{

                    obj = advisorslist.getJSONObject(position);
                    Log.i("pos",Integer.toString(position));
                    Log.i("Here", obj.getString("firstName"));

                   // advisor = obj.getString("firstName") + " " + obj.getString("lastName");
                    onAdvisorPickerFinish(obj);
                    
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

    private void onAdvisorPickerFinish(JSONObject adv){
        Log.i("me5","Clicked5");
        advPickerListener.onAdvisorPickerFinish(adv);
        getDialog().dismiss();
    }

//    private void setAdvisorlist() {
//        new AdvisorsData().execute();
//    }
//
//    private class AdvisorsData extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            try {
//                //Thread.sleep(500);
//            } catch (Exception e) {
//            }
//
//            try {
//                OkHttpClient client = new OkHttpClient();
//
//                HttpUrl url = new HttpUrl.Builder()
//                        .scheme("http")
//                        .host(appConfig.getHostName())
//                        .port(appConfig.getPort())
//                        .addPathSegment("MavAdvise")
//                        .addPathSegment("getAdvisors")
//                        .build();
//
//                RequestBody formBody = new FormBody.Builder()
//                        .add("branch", appConfig.getUser().getDepartment())
//                        .build();
//
//                Request request = new Request.Builder()
//                        .url(url)
//                        //.addHeader("Cookie",sessionId)
//                        .post(formBody)
//                        .build();
//
//                Response response = client.newCall(request).execute();
//
//                return response.body().string();
//            } catch (Exception e) {
//                Log.e("HTTP Error", e.getMessage());
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            try {
//                if (result != null) {
//                    JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
//                    advisors = obj.getJSONArray("result");
//                    if (advisors != null) {
//                        Log.i("no", "not null");
//                    }
//                    Log.i("In", "In post execute");
//                    Log.i("jso", obj.getString("result"));
//
//                } else {
//                    Toast.makeText(getContext(),
//                            "Error retrieving the sessions.", Toast.LENGTH_LONG).show();
//                }
//            } catch (Exception e) {
//                Log.e("JSON Parse", e.getMessage());
//            }
//            //           mDialog.dismiss();
//        }
//    }

    public class OptionsAdapter extends BaseAdapter {

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;
            Log.i("me8","Clicked8");
            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_advisor_select, parent, false);
            }

            TextView adv = (TextView) row.findViewById(R.id.advisorTV);

           //Log.i("nlst",);

            adv.setText("Advisors");
            JSONObject obj = null;

            Log.i("me9","Clicked9");

            try {
                obj = advisorslist.getJSONObject(position);
                Log.i("lst",obj.toString());
                Log.i("pos",Integer.toString(position));
                Log.i("Here", obj.getString("firstName"));
                adv.setText(obj.getString("firstName") + " " + obj.getString("lastName"));
                advisor = obj.getString("firstName");
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
            }
            return row;
        }

        public OptionsAdapter() {
        }

        public int getCount() {
            Log.i("posi",""+advisorslist.length());
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
