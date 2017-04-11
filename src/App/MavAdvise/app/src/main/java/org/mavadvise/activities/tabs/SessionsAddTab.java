package org.mavadvise.activities.tabs;

import java.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.mavadvise.R;
import org.mavadvise.commons.DatePickerHelper;

/**
 * Created by SaiKumar on 4/7/2017.
 */

public class SessionsAddTab
        extends Fragment {

    private String startDate,
                     endDate,
                   startTime,
                     endTime,
                   noOfSlots,
                   frequency;

    private int SET_START_DATE = 0,
                SET_END_DATE = 1;

    private int SET_START_TIME = 2,
                SET_END_TIME = 3;

    private TextView startDateTV, endDateTV;

    public SessionsAddTab() {
    }

    public static SessionsAddTab newInstance() {
        SessionsAddTab fragment = new SessionsAddTab();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sessions_add, container, false);

        startDateTV = (TextView) rootView.findViewById(R.id.startDateTV);
        endDateTV = (TextView) rootView.findViewById(R.id.endDateTV);

        Button startDateBtn = (Button) rootView.findViewById(R.id.ChangeStartDateBT);

        startDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();
                DatePickerHelper datePickerHelper = new DatePickerHelper();

                datePickerHelper.setOnSumbitListener(new DatePickerHelper.DatePickerListener(){
                    @Override
                    public void onDatePickerFinish(Calendar date){
                        String d = DateFormat.format("MM/dd/yyyy", date.getTimeInMillis()).toString();
                        startDateTV.setText(d);
                    }
                });

                datePickerHelper.show(fm, "DatePick");
            }
        });

        Button endDateBtn = (Button) rootView.findViewById(R.id.ChangeEndDateBT);

        endDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FragmentManager fm = getFragmentManager();
                DatePickerHelper datePickerHelper = new DatePickerHelper();

                datePickerHelper.setOnSumbitListener(new DatePickerHelper.DatePickerListener(){
                    @Override
                    public void onDatePickerFinish(Calendar date){
                        String d = DateFormat.format("MM/dd/yyyy", date.getTimeInMillis()).toString();
                        endDateTV.setText(d);
                    }
                });

                datePickerHelper.show(fm, "DatePick");
            }
        });


        initControls(rootView);
        return rootView;
    }

    private void initControls(View view){

    }

    private void validateAndSubmit(){

    }
}
