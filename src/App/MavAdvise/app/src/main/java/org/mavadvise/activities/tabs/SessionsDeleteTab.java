package org.mavadvise.activities.tabs;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.mavadvise.R;
import org.mavadvise.activities.ManageSessions;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.AlertDialogHelper;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.data.User;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by SaiKumar on 4/7/2017.
 */

public class SessionsDeleteTab extends Fragment {

    private JSONArray sessions;

    private ListView deleteSessionsList;
    private OptionsAdapter optionsAdapter;
    private ArrayList<Integer> selectedSessions = new ArrayList<Integer>();

    private AppConfig appConfig;
    private AlertDialogHelper alertDialog;

    private TextView deleteButton, cancelButton;
    private ProgressDialogHelper deleteDialog;

    int sColor;
    JSONObject tempobj;

    public SessionsDeleteTab() {
    }

    public static SessionsDeleteTab newInstance() {
        SessionsDeleteTab fragment = new SessionsDeleteTab();
        return fragment;
    }

    public void refreshContent(JSONArray sessions){
        this.sessions = sessions;
        optionsAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sessions_delete, container, false);

        sColor = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);
        deleteSessionsList = (ListView) rootView.findViewById(R.id.sessionsDeletelist);
        deleteButton = (Button) rootView.findViewById(R.id.sessionDeleteBT);
        cancelButton = (Button) rootView.findViewById(R.id.sessionCancelBT);

        optionsAdapter = new OptionsAdapter();
        deleteSessionsList.setAdapter(optionsAdapter);

        deleteSessionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    tempobj = sessions.getJSONObject(position);
                    String status = tempobj.getString("status");

                    if(!status.startsWith("S"))
                    {
                        Toast.makeText(getContext(), "Only scheduled sessions can be deleted",
                                Toast.LENGTH_LONG).show();
                        view.setActivated(false);
                        return;
                    }

                    String appts = tempobj.getString("slotCounter");

                    if(!appts.equalsIgnoreCase("0")){
                        Toast.makeText(getContext(), "This sessions has appointments. Cancel the session instead",
                                Toast.LENGTH_LONG).show();
                        view.setActivated(false);
                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                if(!selectedSessions.contains(position)){
                    selectedSessions.add(position);
                } else
                    selectedSessions.remove(new Integer(position));

                String str = "Delete (" + selectedSessions.size() + ")";
                deleteButton.setText(str);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedSessions.clear();
                deleteButton.setText("Delete");

                deleteSessionsList.clearChoices();
                deleteSessionsList.requestLayout();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedSessions.size() != 0){
                    int count = selectedSessions.size();
                    String msg = "Do you want to delete " + count + " session(s)?";

                    alertDialog = AlertDialogHelper.newInstance(msg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteDialog = ProgressDialogHelper.newInstance();
                                    deleteDialog.setMsg("Deleting...");
                                    deleteDialog.show(getFragmentManager(), "Delete");
                                    new DeleteSessions().execute();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    selectedSessions.clear();
                                    deleteButton.setText("Delete");

                                    deleteSessionsList.clearChoices();
                                    deleteSessionsList.requestLayout();
                                }
                            });
                    alertDialog.show(getFragmentManager(), "Alert");
                }
            }
        });

        appConfig = ((MavAdvise) getActivity().getApplication()).getAppConfig();
        return rootView;
    }

    private class DeleteSessions extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params){

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e("DeleteSessions", "Thread exception");
            }

            StringBuilder sessionIDsBuilder = new StringBuilder();
            JSONObject obj = null;

            for(int i : selectedSessions){
                try {
                    obj = sessions.getJSONObject(i);
                    sessionIDsBuilder.append(obj.getString("sessionID") + ",");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String sessionIDs = sessionIDsBuilder.toString();
            sessionIDs = sessionIDs.replaceAll(",$", "");

            Log.i("SessionsDeleteTab",sessionIDs);

            try {
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http")
                        .host(appConfig.getHostName())
                        .port(appConfig.getPort())
                        .addPathSegment("MavAdvise")
                        .addPathSegment("deleteSessions")
                        .build();

                User user = appConfig.getUser();

                RequestBody formBody = new FormBody.Builder()
                        .add("netID", user.getNetID())
                        .add("sessionIDs",sessionIDs)
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();

                return response.body().string();
            } catch (Exception e){
                Log.e("HTTP Error", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            deleteDialog.dismiss();

            try {
                Thread.sleep(500);
            } catch (Exception e){
                Log.e("AddSessions", "Thread exception");
            }

            if(result != null) {
                try {
                    JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
                    String resultStr = obj.getString("type");

                    if(resultStr.equalsIgnoreCase("success")){
                        Toast.makeText(getContext(), "Session(s) deleted",
                                Toast.LENGTH_LONG).show();

                        sessions = obj.getJSONArray("result");
                        ((ManageSessions) getActivity()).refreshSessionsData(sessions);

                        resetForm();
                    } else {
                        String msg = obj.getString("message");

                        Toast.makeText(getContext(), msg,
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Log.e("AddSessions.onPostExec", e.getMessage());
                }
            }

        }
    }

    private void resetForm(){
        selectedSessions.clear();
        deleteButton.setText("Delete");

        deleteSessionsList.clearChoices();
        deleteSessionsList.requestLayout();
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
