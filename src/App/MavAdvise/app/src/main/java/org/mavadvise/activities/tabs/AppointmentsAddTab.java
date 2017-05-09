package org.mavadvise.activities.tabs;

/**
 * Created by Remesh on 4/12/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.activities.ManageAppointments;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.AdvisorPickerHelper;
import org.mavadvise.commons.DateListPickerHelper;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.URLResourceHelper;
import org.mavadvise.data.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class AppointmentsAddTab extends Fragment {

    private AppConfig appConfig;
    private ProgressDialogHelper saveDialog;
    private String netid, sessionid, aDate;
    private Button dateBtn, createBtn;
    private Button advBtn;

    private JSONArray appointments, advisors, sessions;
    private SimpleDateFormat fromDateFormat, toDateFormat;
    private TextView dateTV;

    private TextView advTV;

    private Calendar appDate = Calendar.getInstance();

    public AppointmentsAddTab() {
    }

    public static AppointmentsAddTab newInstance() {
        AppointmentsAddTab fragment = new AppointmentsAddTab();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_appointments_add, container, false);

        appConfig = ((MavAdvise) getActivity().getApplication()).getAppConfig();
        initControls(rootView);

        return rootView;
    }

    private void initControls(View view) {
        dateTV = (TextView) view.findViewById(R.id.DateTV);
        advTV = (TextView) view.findViewById(R.id.AdvTV);

        appDate = Calendar.getInstance();
        dateTV.setText(DateFormat.format("EEE, MMM d yyyy", appDate.getTimeInMillis()).toString());

        dateBtn = (Button) view.findViewById(R.id.ChangeDateBT);
        advBtn = (Button) view.findViewById(R.id.SelectAdvBT);
        createBtn = (Button) view.findViewById(R.id.createAppBT);

        netid = null;
        dateBtn.setEnabled(false);
        dateBtn.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
        createBtn.setEnabled(false);
        createBtn.setBackgroundColor(getResources().getColor(R.color.colorDisabled));

        getAdvisorList();

        dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();

                if (netid == null) {

                    dateBtn.setEnabled(false);
                    dateBtn.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
                }

                DateListPickerHelper datePicker = new DateListPickerHelper();
                if (sessions != null) {
                    datePicker.setSessionDates(sessions);
                }

                datePicker.setOnClickListener(new DateListPickerHelper.DateListPickerListener() {
                    @Override
                    public void onDateListPickerFinish(JSONObject sess) {
                        try {
                            aDate = sess.getString("date");
                            dateTV.setText(aDate);
                            sessionid = sess.getString("session_id");

                            if (sessionid != null) {
                                createBtn.setEnabled(true);
                                createBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            } else {
                                createBtn.setEnabled(false);
                                createBtn.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
                            }

                        } catch (JSONException e) {

                        }
                    }
                });

                datePicker.show(fm, "DatePick");
            }
        });


        advBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();

                AdvisorPickerHelper advisorPicker = new AdvisorPickerHelper();
                if (advisors != null) {
                    advisorPicker.setAdvisors(advisors);
                }

                advisorPicker.show(fm, "AdvisorPick");

                advisorPicker.setOnSelectListener(new AdvisorPickerHelper.AdvisorPickerListener() {
                    @Override
                    public void onAdvisorPickerFinish(JSONObject adv) {
                        try {
                            String name = adv.getString("firstName") + " " + adv.getString("lastName");
                            advTV.setText(name);
                            netid = adv.getString("netID");

                            if (netid != null) {
                                dateBtn.setEnabled(true);
                                dateBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            } else {
                                dateBtn.setEnabled(false);
                                dateBtn.setBackgroundColor(getResources().getColor(R.color.colorDisabled));
                            }

                            getDateList();
                        } catch (JSONException e) {

                        }
                    }


                });
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndCreateAppointment();
            }
        });
    }

    private void validateAndCreateAppointment() {
        saveDialog = ProgressDialogHelper.newInstance();
        saveDialog.show(getFragmentManager(), "Creating");

        fromDateFormat = new SimpleDateFormat("EEE, MMM d yyyy");
        toDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        User user = appConfig.getUser();
        String d = null;

        try {
            d = toDateFormat.format(fromDateFormat.parse(aDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        RequestBody formBody = new FormBody.Builder()
                .add("sessionID", sessionid)
                .add("netID", user.getNetID())
                .add("date", d)
                .build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("createAppointment", formBody, new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        saveDialog.dismiss();

                        try {
                            appointments = obj.getJSONArray("result");

                            ((ManageAppointments) getActivity()).refreshAppointmentsData(appointments);
                            ((ManageAppointments) getActivity()).showViewTab();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Exception on exiting create.",
                                    Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        saveDialog.dismiss();
                        Toast.makeText(getContext(), msg,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        urlResourceHelper.execute();
    }

    private void getAdvisorList(){
        RequestBody formBody = new FormBody.Builder()
                .add("branch", appConfig.getUser().getDepartment())
                .build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("getAdvisors", formBody,
                        new URLResourceHelper.onFinishListener() {
                            @Override
                            public void onFinishSuccess(JSONObject obj) {
                                try {
                                    advisors = obj.getJSONArray("result");
                                } catch (Exception e) {
                                    Log.e("JSON Parse", e.getMessage());
                                }
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                Toast.makeText(getContext(),
                                        msg, Toast.LENGTH_LONG).show();
                            }
                        });

        urlResourceHelper.execute();
    }

    private void getDateList(){
        RequestBody formBody = new FormBody.Builder()
                .add("netID", netid)
                .build();

        URLResourceHelper urlResourceHelper =
                new URLResourceHelper("getSessionDates", formBody,
                        new URLResourceHelper.onFinishListener() {
                            @Override
                            public void onFinishSuccess(JSONObject obj) {
                                try {
                                    sessions = obj.getJSONArray("result");
                                } catch (Exception e) {
                                    Log.e("JSON Parse", e.getMessage());
                                }
                            }

                            @Override
                            public void onFinishFailed(String msg) {
                                Toast.makeText(getContext(),
                                        msg, Toast.LENGTH_LONG).show();
                            }
                        });

        urlResourceHelper.execute();
    }
}
