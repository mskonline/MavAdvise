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
import org.mavadvise.activities.ManageAppointments;
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

public class AppointmentsDeleteTab extends Fragment {

    private JSONArray appointments;

    private ListView deleteAppointmentsList;
    private OptionsAdaptor optionsAdapter;
    private ArrayList<Integer> selectedAppointments = new ArrayList<Integer>();

    private AppConfig appConfig;
    private AlertDialogHelper alertDialog;

    private TextView deleteButton, cancelButton;
    private ProgressDialogHelper deleteDialog;

    int sColor;
    JSONObject tempobj;

    public AppointmentsDeleteTab() {
    }

    public static AppointmentsDeleteTab newInstance() {
        AppointmentsDeleteTab fragment = new AppointmentsDeleteTab();
        return fragment;
    }

    public void refreshContent(JSONArray appointments){
        this.appointments = appointments;
        optionsAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments_delete, container, false);

        sColor = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);
        deleteAppointmentsList = (ListView) rootView.findViewById(R.id.appsDeletelist);
        //deleteButton = (Button) rootView.findViewById(R.id.sessionDeleteBT);
        cancelButton = (Button) rootView.findViewById(R.id.appCancelBT);

        optionsAdapter = new OptionsAdaptor();
        deleteAppointmentsList.setAdapter(optionsAdapter);

        deleteAppointmentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final View aView = view;

                Log.i("Del", "Delete1");
                try {
                    Log.i("Del", "Delete21");
                    tempobj = appointments.getJSONObject(position);
                    Log.i("Del", tempobj.toString());
                    String status = tempobj.getString("appStatus");
                    boolean vError = false;
                    Log.i("Del", "Delete2");
                }catch(JSONException e){
                    Log.i("Del", "Delete3");
                }

                if(!selectedAppointments.contains(position)){
                    selectedAppointments.add(position);
                } else
                    selectedAppointments.remove(new Integer(position));

                if(selectedAppointments.size() == 0)
                    cancelButton.setText("Cancel");
                else{
                    String str = "Cancel (" + selectedAppointments.size() + ")";
                    cancelButton.setText(str);
                }
            }
        });

//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectedAppointments.clear();
//               // deleteButton.setText("Delete");
//                deleteDialog = ProgressDialogHelper.newInstance();
////                                    deleteDialog.setMsg("Deleting...");
////                                    deleteDialog.show(getFragmentManager(), "Delete");
//                                    new DeleteAppointments().execute();
//
//                deleteAppointmentsList.clearChoices();
//                deleteAppointmentsList.requestLayout();
//            }
//        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedAppointments.size() != 0){
                    int count = selectedAppointments.size();
                    String msg = "Do you want to cancel " + count + " Appointment(s)?";

                    alertDialog = AlertDialogHelper.newInstance(msg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteDialog = ProgressDialogHelper.newInstance();
                                    deleteDialog.setMsg("Cancelling...");
                                    deleteDialog.show(getFragmentManager(), "Delete");
                                    new DeleteAppointments().execute();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    selectedAppointments.clear();
                                    deleteButton.setText("Cancel");

                                    deleteAppointmentsList.clearChoices();
                                    deleteAppointmentsList.requestLayout();
                                }
                            });
                    alertDialog.show(getFragmentManager(), "Alert");
                }
            }
        });

        appConfig = ((MavAdvise) getActivity().getApplication()).getAppConfig();
        return rootView;
    }

    private class DeleteAppointments extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {

            try {
                Thread.sleep(500);
            } catch (Exception e) {
                Log.e("DeleteAppointments", "Thread exception");
            }

            StringBuilder appIDsBuilder = new StringBuilder();
            JSONObject obj = null;
           // Integer[] appIdsInt = null;

            for(int i : selectedAppointments){
                try {
                    obj = appointments.getJSONObject(i);
                    appIDsBuilder.append(obj.getString("session_id") + ",");
                    //appIdsInt[i] = Integer.parseInt(obj.getString("session_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            String appIDs = appIDsBuilder.toString();
            appIDs = appIDs.replaceAll(",$", "");

            Log.i("AppointmentsDeleteTab",appIDs);

            try {
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = new HttpUrl.Builder()
                        .scheme("http")
                        .host(appConfig.getHostName())
                        .port(appConfig.getPort())
                        .addPathSegment("MavAdvise")
                        .addPathSegment("deleteAppointments")
                        .build();

                User user = appConfig.getUser();

                RequestBody formBody = new FormBody.Builder()
                        .add("netID", user.getNetID())
                        .add("sessionID",appIDs)
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
                Log.e("AddApps", "Thread exception");
            }

            if(result != null) {
                try {
                    JSONObject obj = (JSONObject) new JSONTokener(result).nextValue();
                    String resultStr = obj.getString("type");

                    if(resultStr.equalsIgnoreCase("success")){
                        Toast.makeText(getContext(), "Appointment(s) cancelled",
                                Toast.LENGTH_LONG).show();

                        appointments = obj.getJSONArray("result");
                        ((ManageAppointments) getActivity()).refreshAppointmentsData(appointments);

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
        selectedAppointments.clear();
        cancelButton.setText("Cancel");

        deleteAppointmentsList.clearChoices();
        deleteAppointmentsList.requestLayout();
    }

    public class OptionsAdaptor extends BaseAdapter {


        public int getCount() {
            return appointments != null ? appointments.length() : 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View row = convertView;

            if (row == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                row = inflater.inflate(R.layout.list_appointments_item, parent, false);
            }

            TextView aHeader, aDate, aTime, aStat;

            aHeader = (TextView) row.findViewById(R.id.appmnt_header);
            aTime = (TextView) row.findViewById(R.id.appmnt_time);
            aDate = (TextView) row.findViewById(R.id.appmnt_date);
            aStat = (TextView) row.findViewById(R.id.appmnt_status);

            JSONObject obj = null;

            try {
                obj = appointments.getJSONObject(position);
                String status = obj.getString("appStatus");
                Log.i("stat", status);
                row.setVisibility(View.VISIBLE);
                if(status.startsWith("S")) {
                    aHeader.setText(obj.getString("firstname") + " " + obj.getString("lastname"));
                    Log.i("jso", obj.getString("firstname"));
                    aTime.setText(obj.getString("starttime") + " - " + obj.getString("endtime"));
                    aDate.setText(obj.getString("date"));
                    aStat.setText(obj.getString("appStatus"));
                }else{
                    row.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error in retrieving the list", Toast.LENGTH_SHORT);
            }


            return row;
        }
    }

}






