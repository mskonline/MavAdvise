package org.mavadvise.activities.tabs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.activities.ManageAppointments;
import org.mavadvise.adaptors.AppointmentsDataAdaptor;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.AlertDialogHelper;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;
import org.mavadvise.data.User;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class AppointmentsDeleteTab extends Fragment {

    private JSONArray appointments;

    private ListView cancelAppointmentsList;
    private AppointmentsDataAdaptor appointmentsDataAdaptor;
    private ArrayList<Integer> selectedAppointments = new ArrayList<Integer>();

    private AppConfig appConfig;
    private AlertDialogHelper alertDialog;

    private TextView cancelButton;
    private ProgressDialogHelper deleteDialog;

    public AppointmentsDeleteTab() {
    }

    public static AppointmentsDeleteTab newInstance() {
        AppointmentsDeleteTab fragment = new AppointmentsDeleteTab();
        return fragment;
    }

    public void refreshContent(JSONArray appointments) {
        this.appointments = appointments;
        appointmentsDataAdaptor.setAppointments(appointments);
        appointmentsDataAdaptor.notifyDataSetChanged();
        cancelAppointmentsList.requestLayout();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments_delete, container, false);

        appointmentsDataAdaptor = new AppointmentsDataAdaptor(appointments, this);
        cancelAppointmentsList = (ListView) rootView.findViewById(R.id.appsDeletelist);
        cancelButton = (Button) rootView.findViewById(R.id.appCancelBT);

        cancelAppointmentsList.setAdapter(appointmentsDataAdaptor);

        cancelAppointmentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final View aView = view;

                if (!selectedAppointments.contains(position)) {
                    selectedAppointments.add(position);
                } else
                    selectedAppointments.remove(new Integer(position));

                if (selectedAppointments.size() == 0)
                    cancelButton.setText("Cancel");
                else {
                    String str = "Cancel (" + selectedAppointments.size() + ")";
                    cancelButton.setText(str);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedAppointments.size() != 0) {
                    int count = selectedAppointments.size();
                    String msg = "Do you want to cancel " + count + " Appointment(s)?";

                    alertDialog = AlertDialogHelper.newInstance(msg,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteAppointments();
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    selectedAppointments.clear();
                                    cancelButton.setText("Cancel");

                                    cancelAppointmentsList.clearChoices();
                                    cancelAppointmentsList.requestLayout();
                                }
                            });

                    alertDialog.show(getFragmentManager(), "Alert");
                }
            }
        });

        appConfig = ((MavAdvise) getActivity().getApplication()).getAppConfig();
        deleteDialog = ProgressDialogHelper.newInstance();
        deleteDialog.setMsg("Cancelling...");

        return rootView;
    }

    private void deleteAppointments() {
        if (!deleteDialog.isAdded())
            deleteDialog.show(getFragmentManager(), "Delete");

        StringBuilder appIDsBuilder = new StringBuilder();
        JSONObject obj = null;

        for (int i : selectedAppointments) {
            try {
                obj = appointments.getJSONObject(i);
                appIDsBuilder.append(obj.getString("session_id") + ",");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        String appIDs = appIDsBuilder.toString();
        appIDs = appIDs.replaceAll(",$", "");

        User user = appConfig.getUser();

        RequestBody formBody = new FormBody.Builder()
                .add("netID", user.getNetID())
                .add("sessionID", appIDs)
                .build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("cancelAppointments", formBody,
                        new URLResourceHelper.onFinishListener() {
                            @Override
                            public void onFinishSuccess(JSONObject obj) {
                                try {
                                    Toast.makeText(getContext(), "Appointment(s) cancelled",
                                            Toast.LENGTH_LONG).show();

                                    appointments = obj.getJSONArray("result");
                                    ((ManageAppointments) getActivity()).refreshAppointmentsData(appointments);

                                    resetForm();
                                } catch (Exception e) {
                                    Log.e("JSON Parse", e.getMessage());
                                }

                                deleteDialog.dismiss();
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                deleteDialog.dismiss();
                                Toast.makeText(getContext(),
                                        msg, Toast.LENGTH_LONG).show();
                            }
                        });

        urlResourceHelper.execute();
    }

    private void resetForm() {
        selectedAppointments.clear();
        cancelButton.setText("Cancel");

        cancelAppointmentsList.clearChoices();
        cancelAppointmentsList.requestLayout();
    }
}