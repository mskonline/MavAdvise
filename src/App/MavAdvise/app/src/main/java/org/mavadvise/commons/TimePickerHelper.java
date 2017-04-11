package org.mavadvise.commons;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import org.mavadvise.R;

import java.util.Calendar;

/**
 * Created by SaiKumar on 4/10/2017.
 */

public class TimePickerHelper extends DialogFragment {

    public interface TimePickerListener {
        public void onTimePickerFinish(Calendar date, int flag);
    }

    private int flag;

    public TimePickerHelper(){}

    public void setFlag(int flag) {
        this.flag = flag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_datepicker, container, false);
        getDialog().setTitle("Select Date");

        final TimePicker dp = (TimePicker) rootView.findViewById(R.id.timePicker);

        return rootView;
    }

    private void onDatePickerFinish(Calendar date){
        TimePickerListener activity = (TimePickerListener) getActivity();
        activity.onTimePickerFinish(date, flag);
        getDialog().dismiss();
    }
}
