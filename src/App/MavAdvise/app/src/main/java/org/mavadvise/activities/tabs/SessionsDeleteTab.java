package org.mavadvise.activities.tabs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
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
import org.mavadvise.activities.ManageSessions;
import org.mavadvise.adaptors.SessionsDataAdaptor;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.AlertDialogHelper;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;
import org.mavadvise.data.User;

import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by SaiKumar on 4/7/2017.
 */

public class SessionsDeleteTab extends Fragment {

    private JSONArray sessions;

    private ListView deleteSessionsList;
    private SessionsDataAdaptor sessionsDataAdaptor;
    private ArrayList<Integer> selectedSessions = new ArrayList<Integer>();

    private AppConfig appConfig;
    private AlertDialogHelper alertDialog;

    private TextView deleteButton, cancelButton;
    private ProgressDialogHelper deleteDialog;

    private int sColor;
    private JSONObject tempobj;

    public SessionsDeleteTab() {
    }

    public static SessionsDeleteTab newInstance() {
        SessionsDeleteTab fragment = new SessionsDeleteTab();
        return fragment;
    }

    public void refreshContent(JSONArray sessions){
        this.sessions = sessions;
        sessionsDataAdaptor.setSessions(sessions);
        sessionsDataAdaptor.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("SessionsDeleteTab", "init");
        View rootView = inflater.inflate(R.layout.fragment_sessions_delete, container, false);

        sColor = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);
        deleteSessionsList = (ListView) rootView.findViewById(R.id.sessionsDeletelist);
        deleteButton = (Button) rootView.findViewById(R.id.sessionDeleteBT);
        cancelButton = (Button) rootView.findViewById(R.id.sessionCancelDeleteBT);

        sessionsDataAdaptor = new SessionsDataAdaptor(sessions, this);
        deleteSessionsList.setAdapter(sessionsDataAdaptor);

        deleteSessionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final View aView = view;
                try {
                    tempobj = sessions.getJSONObject(position);
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
                                        deleteSessionsList.clearChoices();
                                        deleteSessionsList.requestLayout();
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

                if(!selectedSessions.contains(position)){
                    selectedSessions.add(position);
                } else
                    selectedSessions.remove(new Integer(position));

                if(selectedSessions.size() == 0)
                    deleteButton.setText("Delete");
                else{
                    String str = "Delete (" + selectedSessions.size() + ")";
                    deleteButton.setText(str);
                }
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
                                    deleteSessions();
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

    private void deleteSessions(){
        deleteDialog = ProgressDialogHelper.newInstance();
        deleteDialog.setMsg("Deleting...");
        deleteDialog.show(getFragmentManager(), "Delete");

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

        User user = appConfig.getUser();

        RequestBody formBody = new FormBody.Builder()
                .add("netID", user.getNetID())
                .add("sessionIDs",sessionIDs)
                .build();

        URLResourceHelper urlResourceHelper =
            new URLResourceHelper("deleteSessions", formBody,
                new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        deleteDialog.dismiss();

                        try {
                            Toast.makeText(getContext(), "Session(s) deleted",
                                    Toast.LENGTH_LONG).show();

                            sessions = obj.getJSONArray("result");
                            ((ManageSessions) getActivity()).refreshSessionsData(sessions);

                            resetForm();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        deleteDialog.dismiss();

                        Toast.makeText(getContext(), msg,
                                Toast.LENGTH_LONG).show();
                    }
                });

        urlResourceHelper.execute();
    }

    private void resetForm(){
        selectedSessions.clear();
        deleteButton.setText("Delete");

        deleteSessionsList.clearChoices();
        deleteSessionsList.requestLayout();
    }
}
