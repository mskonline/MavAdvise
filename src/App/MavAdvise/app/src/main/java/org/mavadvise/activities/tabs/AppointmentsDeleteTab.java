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
        //optionsAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments_delete, container, false);

        sColor = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);
        deleteAppointmentsList = (ListView) rootView.findViewById(R.id.appsDeletelist);
        deleteButton = (Button) rootView.findViewById(R.id.sessionDeleteBT);
        cancelButton = (Button) rootView.findViewById(R.id.sessionCancelDeleteBT);

        optionsAdapter = new OptionsAdaptor();
        deleteAppointmentsList.setAdapter(optionsAdapter);

        deleteAppointmentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final View aView = view;
                try {
                    tempobj = appointments.getJSONObject(position);
                    String status = tempobj.getString("status");
                    boolean vError = false;

                    if(!status.startsWith("S"))
                        vError = true;

                    String appts = tempobj.getString("slotCounter");

                    if(!appts.equalsIgnoreCase("0"))
                        vError = true;

                    if(vError){
                        AlertDialogHelper helper =
                                AlertDialogHelper.newInstance(AppConfig.SESSIONS_DELETE_ONLY_SCHD_ERR,
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                deleteAppointmentsList.clearChoices();
                                                deleteAppointmentsList.requestLayout();
                                            }
                                        });
                        helper.setCancelable(false);
                        helper.show(getFragmentManager(), "Alert");
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                if(!selectedAppointments.contains(position)){
                    selectedAppointments.add(position);
                } else
                    selectedAppointments.remove(new Integer(position));

                if(selectedAppointments.size() == 0)
                    deleteButton.setText("Delete");
                else{
                    String str = "Delete (" + selectedAppointments.size() + ")";
                    deleteButton.setText(str);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedAppointments.clear();
                deleteButton.setText("Delete");

                deleteAppointmentsList.clearChoices();
                deleteAppointmentsList.requestLayout();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedAppointments.size() != 0){
                    int count = selectedAppointments.size();
                    String msg = "Do you want to delete " + count + " session(s)?";

                    alertDialog = AlertDialogHelper.newInstance(msg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteDialog = ProgressDialogHelper.newInstance();
                                    deleteDialog.setMsg("Deleting...");
                                    deleteDialog.show(getFragmentManager(), "Delete");
                                    new DeleteAppointments().execute();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    selectedAppointments.clear();
                                    deleteButton.setText("Delete");

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

            return null;
        }
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
            
            return row;
        }
    }




}
