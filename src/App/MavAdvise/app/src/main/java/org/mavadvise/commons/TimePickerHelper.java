package org.mavadvise.commons;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import org.mavadvise.R;

import java.util.Calendar;

/**
 * Created by SaiKumar on 4/10/2017.
 */

public class TimePickerHelper extends DialogFragment {

    public interface TimePickerListener {
        public void onTimePickerFinish(int hrs, int mins);
    }

    private TimePickerListener timePickerListener;

    public TimePickerHelper(){}


    @Override
    @TargetApi(23)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timepicker, container, false);
        getDialog().setTitle("Select Time");

        final TimePicker tp = (TimePicker) rootView.findViewById(R.id.timePicker);

        Button saveButton = (Button) rootView.findViewById(R.id.tpSelectBT);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                int hours, mins;
                if(Build.VERSION.SDK_INT >= 23){
                    hours = tp.getHour();
                    mins = tp.getMinute();
                } else {
                    hours = tp.getCurrentHour();
                    mins = tp.getCurrentMinute();
                }

                onDatePickerFinish(hours, mins);
            }
        });
        Button cancelButton = (Button) rootView.findViewById(R.id.tpCancelBT);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return rootView;
    }

    private void onDatePickerFinish(int hrs, int mins){
        timePickerListener.onTimePickerFinish(hrs, mins);
        getDialog().dismiss();
    }

    public void setOnSumbitListener(TimePickerListener timePickerListener){
        this.timePickerListener = timePickerListener;
    }
}
