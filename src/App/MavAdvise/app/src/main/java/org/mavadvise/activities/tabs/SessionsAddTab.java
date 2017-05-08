package org.mavadvise.activities.tabs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mavadvise.R;
import org.mavadvise.activities.ManageSessions;
import org.mavadvise.activities.SessionsAddError;
import org.mavadvise.app.AppConfig;
import org.mavadvise.app.MavAdvise;
import org.mavadvise.commons.DatePickerHelper;
import org.mavadvise.commons.ProgressDialogHelper;
import org.mavadvise.commons.TimePickerHelper;
import org.mavadvise.commons.URLResourceHelper;
import org.mavadvise.commons.Utils;
import org.mavadvise.data.User;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by SaiKumar on 4/7/2017.
 */

public class SessionsAddTab extends Fragment {

    private String startTime = "09:00:00",
                     endTime = "11:00:00",
                   noOfSlots = "",
                    location = "",
                   frequency = "0000000";

    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate;

    private TextView startDateTV, endDateTV;
    private TextView startTimeTV, endTimeTV;
    private RelativeLayout repeatRL;

    private View thisView;
    private AppConfig appConfig;
    private ProgressDialogHelper saveDialog;

    private JSONArray sessions;
    private int sHrs, eHrs;

    public SessionsAddTab() {
    }

    public static SessionsAddTab newInstance() {
        SessionsAddTab fragment = new SessionsAddTab();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("SessionsAddTab", "init");
        View rootView = inflater.inflate(R.layout.fragment_sessions_add, container, false);

        initControls(rootView);

        thisView = rootView;

        appConfig = ((MavAdvise) getActivity().getApplication()).getAppConfig();
        return rootView;
    }

    private void initControls(View view){
        startDateTV = (TextView) view.findViewById(R.id.startDateTV);
        endDateTV = (TextView) view.findViewById(R.id.endDateTV);

        startTimeTV = (TextView) view.findViewById(R.id.startTimeTV);
        endTimeTV = (TextView) view.findViewById(R.id.endTimeTV);

        startDate = Calendar.getInstance();
        startDateTV.setText(DateFormat.format("MM/dd/yyyy", startDate.getTimeInMillis()).toString());

        Button startDateBtn = (Button) view.findViewById(R.id.ChangeStartDateBT);
        Button endDateBtn = (Button) view.findViewById(R.id.ChangeEndDateBT);
        Button startTimeBtn = (Button) view.findViewById(R.id.ChangeStartTimeBT);
        Button endTimeBtn = (Button) view.findViewById(R.id.ChangeEndTimeBT);
        CheckBox repeatCB = (CheckBox) view.findViewById(R.id.repeatCB);

        repeatRL = (RelativeLayout) view.findViewById(R.id.repeatRL);
        endDate = Calendar.getInstance();

        sHrs = 9;
        eHrs = 11;

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            FragmentManager fm = getFragmentManager();
            DatePickerHelper datePickerHelper = new DatePickerHelper();

            datePickerHelper.setOnSumbitListener(new DatePickerHelper.DatePickerListener(){
                @Override
                public void onDatePickerFinish(Calendar date){
                    startDate = date;
                    endDate = date;
                    String d = DateFormat.format("MM/dd/yyyy", date.getTimeInMillis()).toString();
                    startDateTV.setText(d);
                }
            });

            datePickerHelper.show(fm, "DatePick");
            }
        });

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();
                DatePickerHelper datePickerHelper = new DatePickerHelper();
                datePickerHelper.setMinDate(startDate);

                datePickerHelper.setOnSumbitListener(new DatePickerHelper.DatePickerListener(){
                    @Override
                    public void onDatePickerFinish(Calendar date){
                        endDate = date;
                        String d = DateFormat.format("MM/dd/yyyy", date.getTimeInMillis()).toString();
                        endDateTV.setText(d);
                    }
                });

                datePickerHelper.show(fm, "DatePick");
            }
        });

        repeatCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isChecked = ((CheckBox) view).isChecked();

                if(isChecked){
                    repeatRL.setVisibility(View.VISIBLE);
                } else{
                    repeatRL.setVisibility(View.GONE);
                    ((CheckBox) view).setChecked(false);
                }
            }
        });

        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();
                TimePickerHelper timePickerHelper = new TimePickerHelper();

                timePickerHelper.setOnSumbitListener(new TimePickerHelper.TimePickerListener() {
                    @Override
                    public void onTimePickerFinish(int hrs, int mins) {
                        startTime = "" + hrs + ":" + mins + ":00";
                        String text;
                        try {
                            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            final Date dateObj = sdf.parse(startTime);
                            text = new SimpleDateFormat("hh:mm a").format(dateObj);
                        } catch (final Exception e) {
                            e.printStackTrace();
                            text = startTime;
                        }
                        startTimeTV.setText(text);
                        sHrs = hrs;
                    }
                });

                timePickerHelper.show(fm, "TimePick");
            }
        });

        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();
                TimePickerHelper timePickerHelper = new TimePickerHelper();

                timePickerHelper.setOnSumbitListener(new TimePickerHelper.TimePickerListener() {
                    @Override
                    public void onTimePickerFinish(int hrs, int mins) {
                        endTime = "" + hrs + ":" + mins + ":00";
                        String text;
                        try {
                            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                            final Date dateObj = sdf.parse(endTime);
                            text = new SimpleDateFormat("hh:mm a").format(dateObj);
                        } catch (final Exception e) {
                            e.printStackTrace();
                            text = endTime;
                        }
                        endTimeTV.setText(text);
                        eHrs = hrs;
                    }
                });

                timePickerHelper.show(fm, "TimePick");
            }
        });

        Button addSessionsBT = (Button) view.findViewById(R.id.addSessionsBT);

        addSessionsBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                validateAndSubmit();
            }
        });
    }

    private void validateAndSubmit() {
        Calendar today = Calendar.getInstance();
        if(Utils.isSameDay(startDate, today)){
            int hrs = today.get(Calendar.HOUR_OF_DAY);

            if(sHrs < hrs){
                Toast.makeText(getContext(),
                        AppConfig.SESSIONS_STIME_VALIDATION_ERR,
                        Toast.LENGTH_LONG).show();
                return;
            }
        }

        if(eHrs < sHrs){
            Toast.makeText(getContext(),
                    AppConfig.SESSIONS_ETIME_VALIDATION_ERR,
                    Toast.LENGTH_LONG).show();
            return;
        }

        StringBuffer fq = new StringBuffer();
        if (repeatRL.getVisibility() == View.VISIBLE) {
            if (((CheckBox) thisView.findViewById(R.id.monCB)).isChecked())
                fq.append("1");
            else
                fq.append("0");

            if (((CheckBox) thisView.findViewById(R.id.tueCB)).isChecked())
                fq.append("1");
            else
                fq.append("0");

            if (((CheckBox) thisView.findViewById(R.id.wedCB)).isChecked())
                fq.append("1");
            else
                fq.append("0");

            if (((CheckBox) thisView.findViewById(R.id.thuCB)).isChecked())
                fq.append("1");
            else
                fq.append("0");

            if (((CheckBox) thisView.findViewById(R.id.friCB)).isChecked())
                fq.append("1");
            else
                fq.append("0");

            fq.append("00");
        } else {
            int dayOfWeek = startDate.get(Calendar.DAY_OF_WEEK) - 1;

            for(int i = 1; i < 7; ++i){
                if(i == dayOfWeek)
                    fq.append("1");
                else
                    fq.append("0");
            }

            if((dayOfWeek + 1) == Calendar.SUNDAY)
                fq.append("1");
            else
                fq.append("0");
        }

        frequency = fq.toString();

        if (frequency.equalsIgnoreCase("0000000")) {
            Toast.makeText(getContext(),
                    AppConfig.SESSIONS_FREQ_VALIDATION_ERR,
                    Toast.LENGTH_LONG).show();
            return;
        }

        EditText slotsET = (EditText) thisView.findViewById(R.id.slotsET);
        noOfSlots = slotsET.getText().toString().trim();

        if(noOfSlots.length() == 0){
            Toast.makeText(getContext(),
                    AppConfig.SESSIONS_SLOTS_VALIDATION_ERR,
                    Toast.LENGTH_LONG).show();
            return;
        }

        EditText locationET = (EditText) thisView.findViewById(R.id.locationET);
        location = locationET.getText().toString().trim();

        if(location.length() == 0){
            Toast.makeText(getContext(),
                    AppConfig.SESSIONS_LOCATION_VALIDATION_ERR,
                    Toast.LENGTH_LONG).show();
            return;
        }

        postSessionData();
    }

    private void postSessionData(){
        saveDialog = ProgressDialogHelper.newInstance();
        saveDialog.show(getFragmentManager(), "Saving");

        String stDate = DateFormat.format("yyyy-MM-dd", startDate.getTimeInMillis()).toString();
        String edDate = DateFormat.format("yyyy-MM-dd", endDate.getTimeInMillis()).toString();

        User user = appConfig.getUser();

        RequestBody formBody = new FormBody.Builder()
                .add("netID", user.getNetID())
                .add("startDate", stDate)
                .add("endDate", edDate)
                .add("startTime", startTime)
                .add("endTime", endTime)
                .add("noOfSlots", noOfSlots)
                .add("frequency", frequency)
                .add("location", location)
                .add("status", "SCHEDULED")
                .build();

        URLResourceHelper urlResourceHelper =
            new URLResourceHelper("addSessions", formBody,
                new URLResourceHelper.onFinishListener() {
                    @Override
                    public void onFinishSuccess(JSONObject obj) {
                        saveDialog.dismiss();

                        try {
                            JSONObject res = obj.getJSONObject("result");
                            sessions = res.getJSONArray("allSessions");

                            JSONArray conflictingSessions = res.getJSONArray("conflictingSessions");

                            ((ManageSessions) getActivity()).refreshSessionsData(sessions);
                            ((ManageSessions) getActivity()).showViewTab();

                            try {
                                Thread.sleep(500);
                            } catch (Exception e){
                                Log.e("AddSessions", "Thread exception");
                            }

                            if(conflictingSessions.length() > 0)
                            {
                                appConfig.setConflictingSessions(conflictingSessions);
                                Intent i = new Intent(getActivity(), SessionsAddError.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(getContext(), AppConfig.SESSIONS_ADD_SUCCESS,
                                        Toast.LENGTH_LONG).show();
                            }

                            resetForm();
                        } catch (Exception e){
                        }
                    }

                    @Override
                    public void onFinishFailed(String msg) {
                        saveDialog.dismiss();

                        Toast.makeText(getContext(), msg,
                                Toast.LENGTH_LONG).show();
                    }
                });

        urlResourceHelper.execute();

    }

    private void resetForm(){
        EditText slotsET = (EditText) thisView.findViewById(R.id.slotsET);
        slotsET.setText("");
        slotsET.clearFocus();

        CheckBox repeatCB = (CheckBox) thisView.findViewById(R.id.repeatCB);
        repeatCB.setChecked(false);

        startDate = Calendar.getInstance();
        startDateTV.setText(DateFormat.format("MM/dd/yyyy", startDate.getTimeInMillis()).toString());

        endDate = Calendar.getInstance();
        endDateTV.setText(DateFormat.format("MM/dd/yyyy", endDate.getTimeInMillis()).toString());

        frequency = "0000000";

        startTime = "09:00:00";
        startTimeTV.setText("9:00 AM");

        endTime = "11:00:00";
        endTimeTV.setText("11:00 AM");

        sHrs = 9;
        eHrs = 11;
    }
}
